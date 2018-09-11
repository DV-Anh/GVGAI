package tracks.singlePlayer.ECAssignment2.controllers.betterRHEA;

import java.util.*;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tracks.singlePlayer.tools.Heuristics.StateHeuristic;
import tracks.singlePlayer.tools.Heuristics.WinScoreHeuristic;

// IDEAS
//      DONE
//          Rollout
//          Info Share
//          Crossover/Mutate
//      DOING
//          Shift Pop
//          Tournament


public class Agent extends AbstractPlayer {

    // HyperParameters
    enum UpdateType{RANDOM, SHIFT, ROTATE, TRANSSHIFT, TRANSROTATE}
    private UpdateType UPDATETYPE = UpdateType.RANDOM;
    private boolean INFOSHARE = false;
    private int POPULATION_SIZE = 10;
    private int INDIVIDUAL_DEPTH = 10;
    private int SIMULATION_DEPTH = 0;
    private int SIMULATION_REPEATS = 0;

    // Class Globals
    private final long BREAK_MS = 5;
    private int NUM_ACTIONS;
    private LinkedList<Individual> population;
    private HashMap<Integer, Types.ACTIONS> action_mapping;
    private ElapsedCpuTimer timer;
    private Random randomGenerator;
    private StateHeuristic heuristic;
    private double acumTimeTakenEval,avgTimeTakenEval, avgTimeTaken, acumTimeTaken;
    private int numEvals, numIters;
    private boolean keepIterating;
    private long remaining;
    private int bestAction;
    private List<Double>[] infoShareList;

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        randomGenerator = new Random();
        heuristic = new WinScoreHeuristic(stateObs);
        this.timer = elapsedTimer;

        // Set up action mapping
        NUM_ACTIONS = stateObs.getAvailableActions().size()+1;
        action_mapping = new HashMap<>();
        for (int i=0; i<NUM_ACTIONS-1; i++)
            action_mapping.put(i, stateObs.getAvailableActions().get(i));
        action_mapping.put(NUM_ACTIONS-1, Types.ACTIONS.ACTION_NIL);

        // Initialise a population with partially initiallised individuals
        population=new LinkedList<>();
        for (int i=0; i<POPULATION_SIZE-1; i++)
            population.add(new Individual(INDIVIDUAL_DEPTH, NUM_ACTIONS));
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        // Reset time keeping statistics
        this.timer = elapsedTimer;
        avgTimeTaken = 0;
        acumTimeTaken = 0;
        numEvals = 0;
        acumTimeTakenEval = 0;
        numIters = 0;
        remaining = timer.remainingTimeMillis();
        keepIterating = true;

        // Create InfoSharing List if it is being used
        if (INFOSHARE) {
            infoShareList = new List[NUM_ACTIONS];
            for (int i=0; i<NUM_ACTIONS; i++)
                infoShareList[i]=new ArrayList<>();
        }

        // Initialise population
        init_pop(stateObs);

//        // Run evolution
//        remaining = timer.remainingTimeMillis();
//        while (remaining > avgTimeTaken && remaining > BREAK_MS && keepIterating) {
//            runIteration(stateObs);
//            remaining = timer.remainingTimeMillis();
//        }

        // Find and return best action
        if (INFOSHARE){
            // Find best action from infoShareList
            double bestValue=Double.NEGATIVE_INFINITY;
            double value;
            for (int i=0; i<NUM_ACTIONS; i++) {
                value=median(infoShareList[i]);
                if (value>bestValue){
                    bestValue=value;
                    bestAction=i;
                }
            }
        }
        else bestAction=population.get(0).actions.get(0);
        return action_mapping.get(bestAction);
    }

    private void evaluate(Individual individual, StateHeuristic heuristic, StateObservation state) {
        individual.value=Double.NEGATIVE_INFINITY;
        List<Double> valueList = new ArrayList<>();
        ElapsedCpuTimer elapsedTimerIterationEval = new ElapsedCpuTimer();

        // Apply all actions the individual has
        StateObservation st = state.copy();
        double acum = 0, avg=0, runs=0;
        for (int i = 0; i < INDIVIDUAL_DEPTH; i++) {
            if (!st.isGameOver()) {
                ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
                st.advance(action_mapping.get(individual.actions.get(i)));
                runs++;
                acum += elapsedTimerIteration.elapsedMillis();
                avg = acum / runs;
                remaining = timer.remainingTimeMillis();
                if (remaining < 2*avg || remaining < BREAK_MS) {
                    keepIterating=false;
                    break;
                }
            } else {
                break;
            }
        }

        // Simulate multiple runs of random moves after the individuals actions
        // a number of times, storing the values for each simulation run
        if (SIMULATION_REPEATS>0) {
            StateObservation backup_st = st.copy();
            for (int j=0; j< SIMULATION_REPEATS && keepIterating; j++) {
                st = backup_st.copy();
                for (int i = 0; i < SIMULATION_DEPTH; i++) {
                    if (!st.isGameOver()) {
                        ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
                        st.advance(action_mapping.get(randomGenerator.nextInt(NUM_ACTIONS)));
                        runs++;
                        acum += elapsedTimerIteration.elapsedMillis();
                        avg = acum / runs;
                        remaining = timer.remainingTimeMillis();
                        if (remaining < 2 * avg || remaining < BREAK_MS) {
                            keepIterating=false;
                            break;
                        }
                    } else {
                        break;
                    }
                }
                valueList.add(heuristic.evaluateState(st));
            }
        }
        else valueList.add(heuristic.evaluateState(st));

        // Find the value of this individual if all simulation iterations completed
        if (keepIterating) {
            individual.value=median(valueList);
            if (INFOSHARE) infoShareList[individual.actions.get(0)].add(individual.value);
        }

        // Update evaluation time keeping statistics
        numEvals++;
        acumTimeTakenEval += (elapsedTimerIterationEval.elapsedMillis());
        avgTimeTakenEval = acumTimeTakenEval / numEvals;
        remaining = timer.remainingTimeMillis();
    }

    // Find the median for a list of values giving an empty list a value of negative infinity
    private double median(List<Double> valueList) {
        if (valueList.size()==0) return Double.NEGATIVE_INFINITY;
        double value;
        Collections.sort(valueList);
        if (valueList.size()%2==1)
            value=valueList.get(valueList.size()/2);
        else
            value=(valueList.get(valueList.size()/2-1)+valueList.get(valueList.size()/2))/2;
        return value;
    }

    private void init_pop(StateObservation stateObs) {
        double remaining = timer.remainingTimeMillis();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            if (i == 0 || remaining > avgTimeTakenEval && remaining > BREAK_MS) {
                population.get(i).randomise();
                evaluate(population.get(i),heuristic,stateObs);
                remaining = timer.remainingTimeMillis();
            } else {break;}
        }
        Collections.sort(population);
    }
}
