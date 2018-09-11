package tracks.singlePlayer.ECAssignment2.controllers.MultiStepAhead;
import tracks.singlePlayer.tools.Heuristics.SimpleStateHeuristic;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Utils;
import tracks.singlePlayer.tools.Heuristics.StateHeuristic;

import java.util.Random;
/**
 *
 */
public class Agent extends AbstractPlayer {
    public double epsilon = 1e-6;
    // 0 for 1 step ahead, 1 for two and so on
    public int extraStep=2;
    public int timeBudget=5;// in milliseconds
    public Random m_rnd;
    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        m_rnd = new Random();
    }
    /**
     *
     * two step look ahead
     *
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        Types.ACTIONS bestAction = null;
        double maxQ = Double.NEGATIVE_INFINITY;
        StateHeuristic heuristic =  new SimpleStateHeuristic(stateObs);
        for (Types.ACTIONS action : stateObs.getAvailableActions())
        {
            if(elapsedTimer.remainingTimeMillis()<timeBudget)return bestAction;
            StateObservation stCopy = stateObs.copy();
            // move according to searched action
            stCopy.advance(action);
            Types.WINNER t = stCopy.getGameWinner();
            if(t == Types.WINNER.PLAYER_WINS){
                return action;
            }else if(t == Types.WINNER.PLAYER_LOSES){
                continue;
            }
            // measure n successive action's value
            double Q= act_extra(stCopy,heuristic,extraStep,elapsedTimer);


            //System.out.println("Action:" + action + " score:" + Q);
            if (Q > maxQ) {
                maxQ = Q;
                bestAction = action;
            }
        }
        //System.out.println("======== "  + maxQ + " " + bestAction + "============");
        return bestAction;
    }


    private double act_extra(StateObservation stateObs, StateHeuristic heuristic, int stepsLeft, ElapsedCpuTimer elapsedTimer)
    {
        if(stepsLeft==0||elapsedTimer.remainingTimeMillis()<timeBudget)return heuristic.evaluateState(stateObs);
        double maxQ = Double.NEGATIVE_INFINITY;
        for (Types.ACTIONS action : stateObs.getAvailableActions())
        {
            if(elapsedTimer.remainingTimeMillis()<timeBudget)return maxQ;
            StateObservation stCopy = stateObs.copy();
            stCopy.advance(action);

            // prioritise winning and reject losing
            Types.WINNER t = stCopy.getGameWinner();
            if(t == Types.WINNER.PLAYER_WINS){
                return heuristic.evaluateState(stCopy);
            }else if(t == Types.WINNER.PLAYER_LOSES){
                continue;
            }
            double Q = act_extra(stCopy,heuristic,stepsLeft-1,elapsedTimer);
            Q = Utils.noise(Q, this.epsilon, this.m_rnd.nextDouble());
            //System.out.println("Action:" + action + " score:" + Q);
            if (Q > maxQ) {
                maxQ = Q;
            }
        }
        //System.out.println("======== "  + maxQ + " " + bestAction + "============");
        return maxQ;
    }
}