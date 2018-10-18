package tracks.singlePlayer.ECAssignment3.controllers.hyperRHEATune;

import core.game.StateObservation;
import tracks.singlePlayer.tools.Heuristics.StateHeuristic;

public class MyScoreHeuristic extends StateHeuristic {

    public MyScoreHeuristic(StateObservation stateObs){}

    // Use simplest possible heuristic
    public double evaluateState(StateObservation stateObs) {return stateObs.getGameScore();}
    }
