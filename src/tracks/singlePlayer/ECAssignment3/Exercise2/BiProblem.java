package tracks.singlePlayer.ECAssignment3.Exercise2;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.problem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.IntegerSolution;
import tools.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BiProblem extends AbstractIntegerProblem {
    private int gameID1, gameID2;
    private final static String[][] games = Utils.readGames("examples/all_games_sp.csv");
    private final static String controller = "tracks.singlePlayer.ECAssignment3.controllers.tunedGA.Agent";

    public BiProblem(int g1, int g2)  {
        setNumberOfVariables(4);
        setNumberOfObjectives(2);
        gameID1 = g1;
        gameID2 = g2;

        List<Integer> lower = new ArrayList<>();
        List<Integer> upper = new ArrayList<>();
        lower.add(0); upper.add(1000);          //gamma
        lower.add(1); upper.add(20);            //depth
        lower.add(2); upper.add(20);            //popSize
        lower.add(0); upper.add(1000);          //recprob
        setLowerLimit(lower);
        setUpperLimit(upper);
    }

    @Override
    public void evaluate(IntegerSolution solution) {
        int gamma = solution.getVariableValue(0);
        int depth = solution.getVariableValue(1);
        int popSize = solution.getVariableValue(2);
        int recprob = solution.getVariableValue(3);

        HyperParamSet params = new HyperParamSet(gamma, depth, popSize, recprob);
        solution.setObjective(0, fitness(gameID1, params));
        solution.setObjective(1, fitness(gameID2, params));
    }

    private static double fitness(int gameID, HyperParamSet params) {
        return getNTimesScore(gameID, params, 1);
    }

    private static double getNTimesScore(int gameID, HyperParamSet params, int n) {
        // Play 5 times and get average score
        int levelIdx = 1; // level names from 0 to 4 (game_lvlN.txt).
        String gameName = games[gameID][1];
        String game = games[gameID][0];
        String level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);
        String recordActionsFile = null;// "actions_" + games[gameIdx] + "_lvl"

        double ttl = 0.0;
        for (int i = 0; i < n; i++) {
            int seed = new Random().nextInt();
            double[] res = ArcadeMachine.runOneGame(
                    game, level1,
                    false, controller,
                    null, seed, 0, params);
            System.out.println(res[0] + "," + res[1] + "," + res[2]);
            ttl += res[1];
        }
        return ttl / 5;
    }

}
