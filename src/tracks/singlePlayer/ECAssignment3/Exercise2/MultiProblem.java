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

public class MultiProblem extends AbstractIntegerProblem {
    private int[] gameIDs;
    private final static String[][] games = Utils.readGames("examples/all_games_sp.csv");
    private final static String controller = "tracks.singlePlayer.ECAssignment3.controllers.tunedGA.Agent";

    public MultiProblem(int[] gs)  {
        setNumberOfVariables(4);
        setNumberOfObjectives(2);
        gameIDs = new int[gs.length];
        for (int i = 0; i < gs.length; i++) gameIDs[i] = gs[i];

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
        for (int i = 0; i < gameIDs.length; i++)
            solution.setObjective(i, fitness(gameIDs[i], params));
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
