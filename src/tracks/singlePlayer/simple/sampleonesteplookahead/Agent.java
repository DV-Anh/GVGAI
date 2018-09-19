package tracks.singlePlayer.simple.sampletwosteplookahead;

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
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) 
    {
    	return act_inner(stateObs,elapsedTimer, 2);    	
    }
    
    
    public Types.ACTIONS act_inner(StateObservation stateObs,ElapsedCpuTimer elapsedTimer, int ntimes) 
    {
    	
    		
        Types.ACTIONS bestAction = null;
        double maxQ = Double.NEGATIVE_INFINITY;
        SimpleStateHeuristic heuristic =  new SimpleStateHeuristic(stateObs);
        for (Types.ACTIONS action : stateObs.getAvailableActions()) 
        {
        	long remaining = elapsedTimer.remainingTimeMillis();
            StateObservation stCopy = stateObs.copy();
            // move according to searched action
            stCopy.advance(action);
            
            // move one more step in depth if we have enough time
            if (ntimes>1 && remaining>50)
            	stCopy.advance(act_inner(stCopy,elapsedTimer,ntimes-1));
                        
            // measure ntimes successive action's value
            double Q = heuristic.evaluateState(stCopy);
            Q = Utils.noise(Q, this.epsilon, this.m_rnd.nextDouble());

            //System.out.println("Action:" + action + " score:" + Q);
            if (Q > maxQ) {
                maxQ = Q;
                bestAction = action;
            }
        }        
        return bestAction;
    }
    
    
    


}
