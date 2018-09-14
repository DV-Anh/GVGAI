package tracks.singlePlayer.ECAssignment2.controllers.MultiStepAhead;
import tracks.singlePlayer.tools.Heuristics.SimpleStateHeuristic;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Utils;
import tracks.singlePlayer.tools.Heuristics.StateHeuristic;

import java.util.List;
import java.util.Random;
/**
 *
 */
public class Agent extends AbstractPlayer {
    public double epsilon = 1e-6;
    // 0 for 1 step ahead, 1 for two and so on
    public int extraStep=1;
    public int timeBudget=2;// in milliseconds
    public List<Types.ACTIONS> actions;
    public StateHeuristic heuristic=new SimpleStateHeuristic(null);
    public Random m_rnd = new Random();
    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        actions=stateObs.getAvailableActions(false);
    }
    /**
     *
     * multiple step look ahead
     *
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        Types.ACTIONS bestAction = null;
        double maxQ = Double.NEGATIVE_INFINITY,Q;
        for (Types.ACTIONS action : actions){
            if(elapsedTimer.remainingTimeMillis()<timeBudget)return bestAction;
            StateObservation stCopy = stateObs.copy();
            // move according to searched action
            stCopy.advance(action);
            Types.WINNER t = stCopy.getGameWinner();
            if(t == Types.WINNER.PLAYER_WINS){
                return action;
            }else if(t == Types.WINNER.PLAYER_LOSES){
                continue;
            }else {
                // measure n successive action's value
                Q = act_extra(stCopy, extraStep, elapsedTimer);
            }
            if (Q > maxQ) {
                maxQ = Q;
                bestAction = action;
            }
        }
        //System.out.println("======== "  + maxQ + " " + bestAction + "============");
        return bestAction;
    }


    private double act_extra(StateObservation stateObs, int stepsLeft, ElapsedCpuTimer elapsedTimer){
        if(stepsLeft==0||elapsedTimer.remainingTimeMillis()<timeBudget)
            return Utils.noise(heuristic.evaluateState(stateObs), this.epsilon, this.m_rnd.nextDouble());
        double maxQ = Double.NEGATIVE_INFINITY,Q;
        for (Types.ACTIONS action : actions){
            if(elapsedTimer.remainingTimeMillis()<timeBudget)return maxQ;
            StateObservation stCopy = stateObs.copy();
            stCopy.advance(action);

            // prioritise winning and reject losing
            Types.WINNER t = stCopy.getGameWinner();
            if(t == Types.WINNER.PLAYER_WINS){
                return Utils.noise(heuristic.evaluateState(stCopy), this.epsilon, this.m_rnd.nextDouble());
            }else if(t == Types.WINNER.PLAYER_LOSES){
                continue;
            }else {
                Q = act_extra(stCopy, stepsLeft - 1, elapsedTimer);
            }
            if (Q > maxQ) {
                maxQ = Q;
            }
        }
        //System.out.println("======== "  + maxQ + " " + bestAction + "============");
        return maxQ;
    }
}