package tracks.singlePlayer.ECAssignment2.controllers.sampleOneStepAhead;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tracks.singlePlayer.tools.Heuristics.SimpleStateHeuristic;

import java.util.ArrayList;
import java.util.Random;

public class Agent extends AbstractPlayer {

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
    }

    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        Types.ACTIONS bestAction = null;
        double maxQ = Double.NEGATIVE_INFINITY; //Variable to store the max reward (Q) found.
        SimpleStateHeuristic heuristic = new SimpleStateHeuristic(stateObs);
        for (Types.ACTIONS action : stateObs.getAvailableActions()) { //For all available actions.
            StateObservation stCopy = stateObs.copy();  //Copy the original state (to apply action from it)
            stCopy.advance(action);                     //Apply the action. Object 'stCopy' holds the next state.
            double Q = heuristic.evaluateState(stCopy); //Get the reward for this state.

            //Keep the action with the highest reward.
            if (Q > maxQ) {
                maxQ = Q;
                bestAction = action;
            }
        }
        //Return the best action found.
        return bestAction;
    }
}