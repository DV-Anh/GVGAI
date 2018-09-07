package tracks.singlePlayer;
import java.util.Random;

import core.logging.Logger;
import tools.Utils;
import tracks.ArcadeMachine;



public class exercise1 {

	public static void main(String[] args) 
	{
		// Available tracks:
		String simpleRandomController = "tracks.singlePlayer.simple.simpleRandom.Agent";
		String sampleOneStepController = "tracks.singlePlayer.simple.sampleonesteplookahead.Agent";		
		String geneticController = "tracks.singlePlayer.deprecated.sampleGA.Agent";
		String sampleTwoStepController = "tracks.singlePlayer.simple.sampletwosteplookahead.Agent";
		
		
		//Load available games
		String spGamesCollection =  "examples/all_games_sp.csv";
		String[][] games = Utils.readGames(spGamesCollection);

		//Game settings
		boolean visuals = true;
		int seed = new Random().nextInt();
		
		
		// Games to play 0:alien 11:Boulderdash 13:butterflies 18:chase
		
		int[] gameIdx = {0,11,13,18};
		
		int[] levelIdx = {0,1,2}; // level names from 0 to 4 (game_lvlN.txt).
		String gameName;
		String game;
		String level1;
		
		int M = 5; // repetition
		for(int i=0; i<gameIdx.length; i++)
		{
			for(int j=0; j<levelIdx.length; j++)
			{
				game = games[gameIdx[i]][0];
				gameName = games[gameIdx[i]][1];
				level1 = game.replace(gameName, gameName + "_lvl" + levelIdx[j]);			
				
				System.out.println("Random Controller "+levelIdx[j]);
				ArcadeMachine.runGames(game, new String[]{level1}, M, simpleRandomController, null);
				
				System.out.println("OneStep Controller "+levelIdx[j]);
				ArcadeMachine.runGames(game, new String[]{level1}, M, sampleOneStepController, null);
				
				System.out.println("Genetic Controller "+levelIdx[j]);
				ArcadeMachine.runGames(game, new String[]{level1}, M, geneticController, null);	
				
				System.out.println("TwoStep Controller "+levelIdx[j]);
				ArcadeMachine.runGames(game, new String[]{level1}, M, sampleTwoStepController, null);
			}
			
		}	

	}

}
