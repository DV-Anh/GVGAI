package tracks.singlePlayer.ECAssignment3.controllers.hyperRHEATune;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tracks.singlePlayer.ECAssignment3.Exercise3.HyperParamSet;
import tracks.singlePlayer.tools.Heuristics.StateHeuristic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Agent extends AbstractPlayer {

    // Hyperparameters to be tuned. Values set in Agent constructor
    private double POWER_FACTOR;    // Range {0,inf}
    private double GROWTH_LIN;      // Range {0,inf}
    private double GROWTH_QUAD;     // Range {0,inf}
    private double SHRINK_FRAME;    // Range {0,1}
    private double SHRINK_IMPROVE;  // Range {0,1}
    
    // Global constants and variables
    // Dont tune!
    private static double WIN_BONUS=0.1;
    private static double LOSE_PENALTY=1000;
    private final long BREAK_MS=20;
    private double DEPTH;
    private int NUM_ACTIONS;
    private ArrayList<Types.ACTIONS> action_mapping = new ArrayList<>();
    private ElapsedCpuTimer timer;
    private Random randomGenerator = new Random();
    private StateHeuristic heuristic;
    private double acumTimeTakenEval, avgTimeTakenEval, avgTimeTaken, acumTimeTaken;
    private int numEvals, numIters;
    private long remaining;
    private Individual bestIndividual;
    private List<Double>[] infoShareList;
    private int bestAction;
    private double currentScore;
    private double childscore;

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer, HyperParamSet params) {
        // Hyperparameters to be tuned
        POWER_FACTOR=1;      // Range {0,inf}
        GROWTH_LIN=1;        // Range {0,inf}
        GROWTH_QUAD=0.5;     // Range {0,inf}
        SHRINK_FRAME=0.95;   // Range {0,1}
        SHRINK_IMPROVE=0.25;  // Range {0,1}

        System.out.println("HyperRHEA: " + params.toString());
        // Use simplest possible heuristic
        heuristic = new MyScoreHeuristic(stateObs);

        // Set up action mapping
        NUM_ACTIONS = stateObs.getAvailableActions().size()+1;
        action_mapping.add(Types.ACTIONS.ACTION_NIL);
        for (int i=0; i<NUM_ACTIONS-1; i++)
            action_mapping.add(stateObs.getAvailableActions().get(i));

        // Set up initial best individual
        bestIndividual=new Individual(NUM_ACTIONS);
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        this.timer = elapsedTimer;
        avgTimeTaken = 0;
        acumTimeTaken = 0;
        numEvals = 0;
        acumTimeTakenEval = 0;
        numIters = 0;
        remaining = timer.remainingTimeMillis();

        StateObservation first = stateObs.copy();
        currentScore=heuristic.evaluateState(first);

        // Create new info sharing list for this frame
        infoShareList = new List[NUM_ACTIONS];
        for (int i=0; i<NUM_ACTIONS; i++)  infoShareList[i]=new ArrayList<>();

        // Update and reevaluate best individual, adding to infoshare
        // discarding individual is it loses
        bestIndividual.transshift(bestAction);
        evaluate(bestIndividual, heuristic, stateObs);
        infoShareList[bestIndividual.nextMove()].add(bestIndividual.value);
        if (bestIndividual.value<currentScore) bestIndividual= new Individual(NUM_ACTIONS);

//        // Some useful debugging data
//        System.out.print(currentScore+" ");
//        System.out.print(bestIndividual.value+" ");

        // Run (1+1) EA with length adaptation while enough time remains
        remaining = timer.remainingTimeMillis();
        while (remaining > avgTimeTaken && remaining > BREAK_MS) {
            runIteration(stateObs);
            remaining = timer.remainingTimeMillis();
        }

//        // More useful debugging data
//        System.out.print(childscore+" ");
//        System.out.print(numIters+" ");
//        System.out.print((int)DEPTH+" ");
//        System.out.print(bestIndividual.actions.size()+" ");
//        System.out.println();

        DEPTH*=SHRINK_FRAME;

        // Return best action from info sharing list
        double bestValue=Double.NEGATIVE_INFINITY;
        double value;
        ArrayList<Integer> actionlist= new ArrayList<>();
        for (int i=0; i<NUM_ACTIONS; i++) actionlist.add(i);
        Collections.shuffle(actionlist);
        for (int i : actionlist) {
            value=mean(infoShareList[i]);
            if (value>bestValue){
                bestValue=value;
                bestAction=i;
            }
        }
        return action_mapping.get(bestAction);
    }

    private void runIteration(StateObservation stateObs) {
        ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();

        // Formula for converting a uniform random distribution to a power law distribution
        // used to determine the number of mutations to apply
        // x = [(x1^(-n) - x0^(-n))*y + x0^(-n)]^(-1/n) : x0=1, x1=DEPTH+1, n=POWER_FACTOR, y=uniform random
        int mutations;
        if (bestIndividual.actions.size()>1) {
            double x=Math.pow(DEPTH+1,-POWER_FACTOR)-1;
            x*=randomGenerator.nextDouble();
            x=Math.pow(x+1,-1/POWER_FACTOR);
            mutations= (int)x;
        }
        else mutations=0;

        // Quadratic growth of child depth
        DEPTH+=Math.sqrt(DEPTH)*GROWTH_QUAD+GROWTH_LIN;

        // Create and evaluate child, and, if better than the parent,
        // replace the parent and decrease child depth
        Individual child= new Individual(bestIndividual, (int)DEPTH, mutations);
        evaluate(child, heuristic, stateObs);
        infoShareList[child.nextMove()].add(child.value);
        childscore=child.value;
        if (child.value>bestIndividual.value || (child.value==bestIndividual.value && child.actions.size()<bestIndividual.actions.size() && child.value>currentScore)){
            bestIndividual = child;
            DEPTH *= SHRINK_IMPROVE;
        }

        numIters++;
        acumTimeTaken += (elapsedTimerIteration.elapsedMillis());
        avgTimeTaken = acumTimeTaken / numIters;
    }

    // Evaluate score of individual when all of its actions are performed
    // unless game over condition is found or evaluation time runs out
    private void evaluate(Individual individual, StateHeuristic heuristic, StateObservation state) {
        ElapsedCpuTimer elapsedTimerIterationEval = new ElapsedCpuTimer();
        StateObservation st = state.copy();
        double acum = 0, avg;
        for (int i=0; i < individual.actions.size(); i++) {
            if (! st.isGameOver()) {
                ElapsedCpuTimer elapsedTimerIteration = new ElapsedCpuTimer();
                st.advance(action_mapping.get(individual.actions.get(i)));
                acum += elapsedTimerIteration.elapsedMillis();
                avg = acum / (i+1);
                remaining = timer.remainingTimeMillis();
                if (remaining < 2*avg || remaining < BREAK_MS) break;
            } else {
                break;
            }
        }

        // Score individual with bonus for completing game or penalty for losing
        StateObservation first = st.copy();
        individual.value = heuristic.evaluateState(first);
        if(st.isGameOver() && st.getGameWinner() == Types.WINNER.PLAYER_WINS) individual.value+=WIN_BONUS;
        if(st.isGameOver() && st.getGameWinner() == Types.WINNER.PLAYER_LOSES) individual.value-=LOSE_PENALTY;

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

    // Find the mean for a list of values giving an empty list a value of negative infinity
    private double mean(List<Double> valueList) {
        if (valueList.size()==0) return Double.NEGATIVE_INFINITY;
        double value=0;
        for (double num : valueList) value+=num;
        return value/valueList.size();
    }
}
