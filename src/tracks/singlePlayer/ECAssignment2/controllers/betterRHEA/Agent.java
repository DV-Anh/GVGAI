package tracks.singlePlayer.ECAssignment2.controllers.betterRHEA;

import java.util.*;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tracks.singlePlayer.tools.Heuristics.StateHeuristic;
import tracks.singlePlayer.tools.Heuristics.WinScoreHeuristic;

public class Agent extends AbstractPlayer {

    // HyperParameters
    enum UpdateType {RANDOM, SHIFT, ROTATE, TRANSSHIFT, TRANSROTATE};
    enum AverageType {MEDIAN, MEAN};
    enum ShareType {NONE, ALL, POP};
    private UpdateType UPDATETYPE = UpdateType.TRANSROTATE;
    private AverageType INFOTYPE = AverageType.MEAN;
    private ShareType INFOSHARE = ShareType.NONE;
    private int INDIVIDUAL_DEPTH = 20;
    private int POPULATION_SIZE = 5;
    private int ELITIST_SIZE = POPULATION_SIZE;
    private int TOURNAMENT_SIZE = 1;
    private double CROSSOVER_RATE = 0.50;
    private double MUTATION_RATE = 0.10;
    private int SIMULATION_DEPTH = 0;
    private int SIMULATION_REPEATS = 0;
    private AverageType SIMULATION_STAT=AverageType.MEAN;
    private boolean PRESTART = false;

    // Class Globals
    private final long BREAK_MS = 5;
    private int NUM_ACTIONS;
    private ArrayList<Individual> population;
    private ArrayList<Types.ACTIONS> action_mapping;
    private ElapsedCpuTimer timer;
    private Random randomGenerator;
    private StateHeuristic heuristic;
    private double acumTimeTakenEval,avgTimeTakenEval, avgTimeTaken, acumTimeTaken;
    private int numEvals, numIters, numAdvances;
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
        action_mapping = new ArrayList<>();
        for (int i=0; i<NUM_ACTIONS-1; i++)
            action_mapping.add(stateObs.getAvailableActions().get(i));
        action_mapping.add(Types.ACTIONS.ACTION_NIL);

        // Initialise an empty population
        population=new ArrayList<>();

        // If "prestarting", use up the rest of the first frame time by evolving population
        if (PRESTART) act(stateObs, elapsedTimer);
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
        numAdvances=0;

        // Create InfoSharing List if it is being used
        if (INFOSHARE==ShareType.ALL || INFOSHARE==ShareType.POP) {
            infoShareList = new List[NUM_ACTIONS];
            for (int i=0; i<NUM_ACTIONS; i++)
                infoShareList[i]=new ArrayList<>();
        }

        // Remove non elite population
        while (population.size()>ELITIST_SIZE) population.remove(0);


        // Apply update rule to all existing population members
        double remaining = timer.remainingTimeMillis();
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
                remaining = timer.remainingTimeMillis();
            }
            else break;
        }

        // Add new randomised population members if population is not full size
        while (population.size() < POPULATION_SIZE) {
            if (population.size() == 0 || remaining > avgTimeTakenEval && remaining > BREAK_MS) {
                population.add(new Individual(INDIVIDUAL_DEPTH, NUM_ACTIONS));
                population.get(population.size() - 1).randomise();
                evaluate(population.get(population.size() - 1), heuristic, stateObs);
                remaining = timer.remainingTimeMillis();
            } else break;
        }

        // Run (n+1) elitist evolution until we run out of time
        while (remaining > avgTimeTaken && remaining > BREAK_MS) {
            ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
            Individual individual = new Individual(INDIVIDUAL_DEPTH, NUM_ACTIONS);
            Individual[] parent={population.get(tournament()),population.get(tournament())};
            individual.crossmutation(parent,CROSSOVER_RATE,MUTATION_RATE);
            evaluate(individual, heuristic, stateObs);
            population.add(individual);
            Collections.sort(population);
            population.remove(0);
            numIters++;
            acumTimeTaken += (elapsedTimerIteration.elapsedMillis());
            avgTimeTaken = acumTimeTaken / numIters;
            remaining = timer.remainingTimeMillis();
        }

        // Find the best action to return
        switch (INFOSHARE) {
            case POP:
                for (Individual individual : population)
                    infoShareList[individual.actions.get(0)].add(individual.value);
                // POP deliberately has no break and falls through to case ALL
            case ALL:
                double bestValue=Double.NEGATIVE_INFINITY;
                double value=0;
                for (int i=0; i<NUM_ACTIONS; i++) {
                    switch (INFOTYPE) {
                        case MEDIAN: value=median(infoShareList[i]); break;
                        case MEAN:value=average(infoShareList[i]); break;
                    }
                    if (value>bestValue){
                        bestValue=value;
                        bestAction=i;
                    }
                }
                break;
            case NONE:
                Collections.sort(population);
                bestAction=population.get(population.size()-1).actions.get(0);
                break;
        }

//        System.out.println("State advance calls: "+numAdvances);
        return action_mapping.get(bestAction);
    }

    // Evaluate individual using simulated rollouts
    private void evaluate(Individual individual, StateHeuristic heuristic, StateObservation state) {
        List<Double> valueList = new ArrayList<>();
        ElapsedCpuTimer elapsedTimerIterationEval = new ElapsedCpuTimer();

        // Apply all actions the individual has
        StateObservation st = state.copy();
        double acum = 0, avg=0, runs=0;
        for (int i = 0; i < INDIVIDUAL_DEPTH; i++) {
            if (!st.isGameOver()) {
                ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
                st.advance(action_mapping.get(individual.actions.get(i)));
                numAdvances++;
                runs++;
                acum += elapsedTimerIteration.elapsedMillis();
                avg = acum / runs;
                remaining = timer.remainingTimeMillis();
                if (remaining < 2*avg || remaining < BREAK_MS) break;
            }
            else break;
        }

        // Simulate multiple runs of random moves after the individuals actions
        // a number of times, storing the values for each simulation run
        if (SIMULATION_REPEATS>0) {
            StateObservation backup_st = st.copy();
            for (int j=0; j< SIMULATION_REPEATS; j++) {
                st = backup_st.copy();
                for (int i = 0; i < SIMULATION_DEPTH; i++) {
                    if (!st.isGameOver()) {
                        ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
                        st.advance(action_mapping.get(randomGenerator.nextInt(NUM_ACTIONS)));
                        numAdvances++;
                        runs++;
                        acum += elapsedTimerIteration.elapsedMillis();
                        avg = acum / runs;
                        remaining = timer.remainingTimeMillis();
                        if (remaining < 2 * avg || remaining < BREAK_MS) break;
                    }
                    else break;
                    }
                }
                valueList.add(heuristic.evaluateState(st));
            }
        else valueList.add(heuristic.evaluateState(st));

        // Find the value of this individual if all simulation iterations completed
        switch(SIMULATION_STAT){
            case MEAN: individual.value=average(valueList); break;
            case MEDIAN: individual.value=median(valueList); break;
        }
        if (INFOSHARE==ShareType.ALL) infoShareList[individual.actions.get(0)].add(individual.value);

        // Update evaluation time keeping statistics
        numEvals++;
        acumTimeTakenEval += (elapsedTimerIterationEval.elapsedMillis());
        avgTimeTakenEval = acumTimeTakenEval / numEvals;
        remaining = timer.remainingTimeMillis();
    }

    // Tournament select an individual
    private int tournament() {
        // Return obvious results quickly
        if (TOURNAMENT_SIZE<2) return randomGenerator.nextInt(population.size());
        if (TOURNAMENT_SIZE>=POPULATION_SIZE) return 0;

        // Use a fast probabalistic solution to determine who would be selected
        int member_num=0;
        double current_probability=TOURNAMENT_SIZE/POPULATION_SIZE;
        while(randomGenerator.nextDouble()>current_probability){
            member_num++;
            current_probability=TOURNAMENT_SIZE/(POPULATION_SIZE-member_num);
        }
        return member_num;
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

    // Find the average for a list of values giving an empty list a value of negative infinity
    private double average(List<Double> valueList) {
        if (valueList.size()==0) return Double.NEGATIVE_INFINITY;
        double value=0;
        for (double num : valueList) value+=num;
        return value/valueList.size();
    }
}
