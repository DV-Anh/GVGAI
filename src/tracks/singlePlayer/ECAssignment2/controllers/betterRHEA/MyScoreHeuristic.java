package tracks.singlePlayer.tools.Heuristics;

import core.game.StateObservation;
import ontology.Types;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 11/02/14
 * Time: 15:44
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class MyScoreHeuristic extends StateHeuristic {

    private static double HUGE_NEGATIVE;
    private static double HUGE_POSITIVE;

    double initialNpcCounter = 0;

    public MyScoreHeuristic(StateObservation stateObs, double bonus, double penalty) {
        HUGE_POSITIVE=bonus;
        HUGE_NEGATIVE=-penalty;
    }

    public double evaluateState(StateObservation stateObs) {
        boolean gameOver = stateObs.isGameOver();
        Types.WINNER win = stateObs.getGameWinner();
        double rawScore = stateObs.getGameScore();

        if(gameOver && win == Types.WINNER.PLAYER_LOSES)
            return HUGE_NEGATIVE;

        if(gameOver && win == Types.WINNER.PLAYER_WINS)
            return HUGE_POSITIVE;

        return rawScore;
    }
}


