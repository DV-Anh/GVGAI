package tracks.singlePlayer.ECAssignment2.Exercise2;

import tools.Utils;
import core.logging.Logger;

import java.util.Random;

public class ResultsLog {

        public static void main(String[] args)
        {
            String sampleGAController = "tracks.singlePlayer.ECAssignment2.controllers.sampleGA.Agent";
            String sampleGAControllerOne = "tracks.singlePlayer.deprecated.sampleGA.Agent";

            String spGamesCollection = "examples/all_games_sp.csv";
            String[][] games = Utils.readGames(spGamesCollection);

            boolean visuals = true;
            int seed = new Random().nextInt();

            int[] gameIdx = {0,11,13,18};

            int[] levelIdx = {0,1,2};
            String gameName;
            String game;
            String level1;

            int M=5;
            for (int i=0; i<levelIdx.length; i++)
            {
                game = games[gameIdx[i]][0];
                gameName = games[gameIdx[i]][1];
                level1 = game.replace(gameName, gameName + "_lvl" + levelIdx[i]);

                System.out.println("Genetic Controller"+ levelIdx[i]);
                ArcadeMachine.runGames(game, new String[]{level1}, M, sampleGAController, null);

                System.out.println("Tuned Genetic Controller"+ levelIdx[i]);
                ArcadeMachine.runGames(game, new String[]{level1}, M, sampleGAControllerOne, null);


            }
        }

    }

