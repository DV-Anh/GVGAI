package tracks.singlePlayer.ECAssignment2.controllers.betterRHEA;

import java.util.*;

import com.sun.org.apache.bcel.internal.generic.POP;
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
    enum UpdateType {RANDOM, SHIFT, ROTATE, TRANSSHIFT, TRANSROTATE}
    private UpdateType UPDATETYPE = UpdateType.TRANSSHIFT;
    private boolean INFOSHARE = true;
    private int POPULATION_SIZE = 5;
    private int INDIVIDUAL_DEPTH = 5;
    private int SIMULATION_DEPTH = 0;
    private int SIMULATION_REPEATS = 0;
    private int TOURNAMENT_SIZE = 2;

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

    // Constructor called before first frame
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

        // Initialise an empty population
        population=new LinkedList<>();

        // Set last frame best action to null action
        bestAction=NUM_ACTIONS-1;
    }

    // Action called for every frame
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
        Collections.sort(population);

        // Run evolution
        remaining = timer.remainingTimeMillis();
        while (remaining > avgTimeTaken && remaining > BREAK_MS && keepIterating) {
            runIteration(stateObs);
            remaining = timer.remainingTimeMillis();
        }

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

    // Evaluate individual using simulated rollouts
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

    // Update last frame's last population to be this frame's initial population
    private void init_pop(StateObservation stateObs) {
        double remaining = timer.remainingTimeMillis();

        // Apply update rule to all existing population members
        for (Individual individual : population) {
            if (remaining > avgTimeTakenEval && remaining > BREAK_MS) {
                switch (UPDATETYPE) {
                    case RANDOM: individual.randomise(); break;
                    case SHIFT: individual.shift(); break;
                    case ROTATE: individual.rotate(); break;
                    case TRANSSHIFT: individual.transshift(bestAction); break;
                    case TRANSROTATE: individual.transrotate(bestAction); break;
                }
                evaluate(individual, heuristic, stateObs);
            } else break;
        }

        // Add more new randomised population members if population is not full size yet
        while (population.size() < POPULATION_SIZE) {
            if (population.size() == 0 || remaining > avgTimeTakenEval && remaining > BREAK_MS) {
                population.add(new Individual(INDIVIDUAL_DEPTH, NUM_ACTIONS));
                population.get(population.size() - 1).randomise();
                evaluate(population.get(population.size() - 1), heuristic, stateObs);
                remaining = timer.remainingTimeMillis();
            } else break;
        }
    }

    // Tournament select an individual
    private int tournament() {
        if (TOURNAMENT_SIZE<2) return randomGenerator.nextInt(POPULATION_SIZE);
        if (TOURNAMENT_SIZE>=POPULATION_SIZE) return 0;
        int member_num=0;
        double current_probability=TOURNAMENT_SIZE/POPULATION_SIZE;
        while(randomGenerator.nextDouble()>current_probability){
            member_num++;
            current_probability=TOURNAMENT_SIZE/(POPULATION_SIZE-member_num);
        }
        return member_num;
    }

    // TODO: Perform evolutions on population
    private void runIteration(StateObservation stateObs){}
}
