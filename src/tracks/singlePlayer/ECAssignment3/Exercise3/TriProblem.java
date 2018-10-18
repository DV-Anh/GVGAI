package tracks.singlePlayer.ECAssignment3.Exercise3;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import tools.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TriProblem extends AbstractDoubleProblem {
    private int gameID1, gameID2, gameID3;
    private final static String[][] games = Utils.readGames("examples/all_games_sp.csv");
    private final static String controller = "tracks.singlePlayer.ECAssignment3.controllers.hyperRHEATune.Agent";

    public TriProblem(int g1, int g2, int g3)  {
        setNumberOfVariables(5);
        setNumberOfObjectives(3);
        gameID1 = g1;
        gameID2 = g2;
        gameID3 = g3;

//        POWER_FACTOR=1;      // Range {0,inf}
//        GROWTH_LIN=1;        // Range {0,inf}
//        GROWTH_QUAD=0.5;     // Range {0,inf}
//        SHRINK_FRAME=0.95;   // Range {0,1}
//        SHRINK_IMPROVE=0.25;  // Range {0,1}
        List<Double> lower = new ArrayList<>();
        List<Double> upper = new ArrayList<>();
        lower.add(0.0); upper.add(10.0);
        lower.add(0.0); upper.add(10.0);
        lower.add(0.0); upper.add(10.0);
        lower.add(0.0); upper.add(1.0);
        lower.add(0.0); upper.add(1.0);
        setLowerLimit(lower);
        setUpperLimit(upper);
    }

    @Override
    public void evaluate(DoubleSolution solution) {
        double pow_factor = solution.getVariableValue(0);
        double grth_lin = solution.getVariableValue(1);
        double grth_quad = solution.getVariableValue(2);
        double shrk_frame = solution.getVariableValue(3);
        double shrk_imp = solution.getVariableValue(4);

        HyperParamSet params = new HyperParamSet(pow_factor, grth_lin, grth_quad, shrk_frame, shrk_imp);
        solution.setObjective(0, fitness(gameID1, params));
        solution.setObjective(1, fitness(gameID2, params));
        solution.setObjective(2, fitness(gameID3, params));
    }

    private static double fitness(int gameID, HyperParamSet params) {
        return Math.max(1/getNTimesScore(gameID, params, 1),0.000000000001) ;
    }

    private static double getNTimesScore(int gameID, HyperParamSet params, int n) {
        // Play 5 times and get average score
        int levelIdx = 3; // level names from 0 to 4 (game_lvlN.txt).
        String gameName = games[gameID][1];
        String game = games[gameID][0];
        String level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);
        String recordActionsFile = null;// "actions_" + games[gameIdx] + "_lvl"

        double ttl = 0.0;
        double ttl_step = 0.0;

        for (int i = 0; i < n; i++) {
            int seed = new Random().nextInt();
            double[] res = ArcadeMachine.runOneGame(
                    game, level1,
                    false, controller,
                    null, seed, 0, params);
            System.out.println(res[0] + "," + res[1] + "," + res[2]);
            ttl += res[1];
            ttl_step += res[2];
        }
        return ttl / n + 1.0 / ttl_step * 10;
    }

}
