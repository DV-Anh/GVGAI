package tracks.singlePlayer.ECAssignment2.controllers.TwoStepAhead;
import tracks.singlePlayer.tools.Heuristics.SimpleStateHeuristic;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Utils;
import java.util.Random;
/**
 *
 */
public class Agent extends AbstractPlayer {
    public double epsilon = 1e-6;
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
        SimpleStateHeuristic heuristic =  new SimpleStateHeuristic(stateObs);
        for (Types.ACTIONS action : stateObs.getAvailableActions())
        {

            StateObservation stCopy = stateObs.copy();
            // move according to searched action
            stCopy.advance(action);

            // measure 2 successive action's value
            double Q=act2(stCopy);


            //System.out.println("Action:" + action + " score:" + Q);
            if (Q > maxQ) {
                maxQ = Q;
                bestAction = action;
            }
        }
        //System.out.println("======== "  + maxQ + " " + bestAction + "============");
        return bestAction;
    }


    private double act2(StateObservation stateObs)
    {
        double maxQ = Double.NEGATIVE_INFINITY;
        SimpleStateHeuristic heuristic =  new SimpleStateHeuristic(stateObs);
        for (Types.ACTIONS action : stateObs.getAvailableActions())
        {

            StateObservation stCopy = stateObs.copy();
            stCopy.advance(action);



            double Q = heuristic.evaluateState(stCopy);
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