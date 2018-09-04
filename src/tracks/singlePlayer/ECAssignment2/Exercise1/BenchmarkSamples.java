package tracks.singlePlayer.ECAssignment2.Exercise1;

import tools.Utils;
import tracks.ArcadeMachine;

import java.util.Random;

public class BenchmarkSamples {
    //Load available games
    final static String[][] games = Utils.readGames("examples/all_games_sp.csv");

    public static void main(String[] args) {
        String random = "tracks.singlePlayer.ECAssignment2.controllers.sampleRandom.Agent";
        String oneAhead = "tracks.singlePlayer.ECAssignment2.controllers.sampleOneStepAhead.Agent";
        String ga = "tracks.singlePlayer.ECAssignment2.controllers.sampleGA.Agent";
        String twoAhead = "tracks.singlePlayer.ECAssignment2.controllers.TwoStepAhead.Agent";

        int[] targets = new int[]{0, 11, 13, 18};
        String[] controllers = new String[]{random, oneAhead, ga, twoAhead};

        for (String controller : controllers) {
            for (int gameid : targets) {
                benchmark(gameid, controller, false);
            }
        }
    }

    private static void benchmark(int gameID, String controller, boolean visualising) {
        //Game settings
        int levelIdx = 0; // level names from 0 to 4 (game_lvlN.txt).
        String gameName = games[gameID][1];
        String game = games[gameID][0];
        String level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);
        String recordActionsFile = null;// "actions_" + games[gameIdx] + "_lvl"
        // + levelIdx + "_" + seed + ".txt";
        // where to record the actions
        // executed. null if not to save.
        System.out.println("game:" + gameName + ",controller:" + controller);
        for (int i = 0; i < 5; i++) {
            int seed = new Random().nextInt();
            ArcadeMachine.runOneGame(
                    game, level1,
                    visualising, controller,
                    recordActionsFile, seed, 0);
        }
    }
}
