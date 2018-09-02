package tracks.singlePlayer.ECAssignment2.Exercise1;

import tools.Utils;
import tracks.ArcadeMachine;

import java.util.Random;

public class BenchmarkSamples {
    //Load available games
    final static String[][] games = Utils.readGames("examples/all_games_sp.csv");

    public static void main(String[] args) {
        String random = "tracks.singlePlayer.ECAssignment2.controllers.sampleRandom.Agent";
        benchmark(0, random, true);
        benchmark(11, random, true);
        benchmark(13, random, true);
        benchmark(18, random, true);
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
        for (int i = 0; i < 5; i++) {
            System.out.println("play:" + i + ",game:" + gameName + ",controller:" + controller);
            int seed = new Random().nextInt();
            ArcadeMachine.runOneGame(
                    game, level1,
                    visualising, controller,
                    recordActionsFile, seed, 0);
        }
    }
}
