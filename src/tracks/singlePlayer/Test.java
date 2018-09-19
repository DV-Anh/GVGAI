package tracks.singlePlayer;

import java.util.Random;

import tools.Utils;
import tracks.ArcadeMachine;

/**
 * Created with IntelliJ IDEA. User: Diego Date: 04/10/13 Time: 16:29 This is a
 * Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Test {

	public static void main(String[] args) {

		// Available tracks:
		//	String sampleRandomController = "tracks.singlePlayer.simple.sampleRandom.Agent";
		//	String simpleRandomController = "tracks.singlePlayer.simple.simpleRandom.Agent";
		//	String doNothingController = "tracks.singlePlayer.simple.doNothing.Agent";
		//	String sampleOneStepController = "tracks.singlePlayer.simple.sampleonesteplookahead.Agent";
		//	String sampleFlatMCTSController = "tracks.singlePlayer.simple.greedyTreeSearch.Agent";
		String sampleGAController = "tracks.singlePlayer.ECAssignment2.controllers.sampleGA.Agent";
		String sampleGAControllerOne = "tracks.singlePlayer.deprecated.sampleGA.Agent";
		//   String tunedGAController = "tracks.singlePlayer.ECAssignment2.controllers.tunedGA.Agent";
		//	String sampleMCTSController = "tracks.singlePlayer.advanced.sampleMCTS.Agent";
		//  String sampleRSController = "tracks.singlePlayer.advanced.sampleRS.Agent";
		//  String sampleRHEAController = "tracks.singlePlayer.advanced.sampleRHEA.Agent";
		//	String sampleOLETSController = "tracks.singlePlayer.advanced.olets.Agent";

		//Load available games
		String spGamesCollection = "examples/all_games_sp.csv";
		String[][] games = Utils.readGames(spGamesCollection);

		//Game settings
		boolean visuals = false;
		int seed = new Random().nextInt();

		// Game and level to play
	//	int gameIdx = 18;//aliens=0, boulderdash=11, butterflies=13, chase=18
	//	int levelIdx = 2; // level names from 0 to 4 (game_lvlN.txt).
	//	String gameName = games[gameIdx][1];
	//	String game = games[gameIdx][0];
	//	String level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);

	//	String recordActionsFile = null;// "actions_" + games[gameIdx] + "_lvl"
		// + levelIdx + "_" + seed + ".txt";
		// where to record the actions
		// executed. null if not to save.

		// 1. This starts a game, in a level, played by a human.
		//ArcadeMachine.playOneGame(game, level1, recordActionsFile, seed);

		// 2. This plays a game in a level by the controller.
		// ArcadeMachine.runOneGame(game, level1, visuals, sampleGAController, recordActionsFile, seed, 0);

		int[] gameIdx = {0,11,13,18};

		int[] levelIdx = {0,1,2};
		String gameName;
		String game ;
		String level1;

		int M=5;
		for (int i=0; i<levelIdx.length; i++)
		{
			game = games[gameIdx[i]][0];
			gameName = games[gameIdx[i]][1];
			level1 = game.replace(gameName, gameName + "_lvl" + levelIdx[i]);

			// Run this controller for generating results for Original Parameters
			System.out.println("Genetic Controller"+ levelIdx[i]);
			ArcadeMachine.runGames(game, new String[]{level1}, M, sampleGAController, null);

			//Run this controller for generating results for Tuned Parameters
			System.out.println("Tuned Genetic Controller"+ levelIdx[i]);
			ArcadeMachine.runGames(game, new String[]{level1}, M, sampleGAControllerOne, null);


		}

		// 3. This replays a game from an action file previously recorded
		//	 String readActionsFile = recordActionsFile;
		//	 ArcadeMachine.replayGame(game, level1, visuals, readActionsFile);

		// 4. This plays a single game, in N levels, M times :
	/* String level2 = new String(game).replace(gameName, gameName + "_lvl" + 1);
		int M = 5;
		for(int i=0; i<games.length; i++){
			if(i==0||i==11||i==13||i==18){
			    game = games[i][0];
			    gameName = games[i][1];
			    level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);
				System.out.println(level1);
			    ArcadeMachine.runGames(game, new String[]{level1}, M, sampleGAControllerOne, null);
			}
		} */

/*	//5. This plays N games, in the first L levels, M times each. Actions to file optional (set saveActions to true).
		int N = games.length, L = 0, M = 5;
		boolean saveActions = false;
		String[] levels = new String[L];
		String[] actionFiles = new String[L * M];
		for (int i = 0; i < N; ++i) {
			if (i == 0 || i == 11 || i == 13 || i == 18) {
				int actionIdx = 0;
				game = games[i][0];
				gameName = games[i][1];
				for (int j = 0; j < L; ++j) {
					levels[j] = game.replace(gameName, gameName + "_lvl" + j);
					if (saveActions) for (int k = 0; k < M; ++k)
						actionFiles[actionIdx++] = "actions_game_" + i + "_level_" + j + "_" + k + ".txt";
				}
				ArcadeMachine.runGames(game, levels, M, sampleGAController, saveActions ? actionFiles : null);
			} */


		}
	}

