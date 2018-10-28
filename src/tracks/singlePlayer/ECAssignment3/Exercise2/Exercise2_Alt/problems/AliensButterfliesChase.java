package problems;

import java.util.Random;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;
import jmetal.util.wrapper.XReal;
import tools.Utils;


import tracks.ArcadeMachine;


public class AliensButterfliesChase extends Problem
{
	String gaController = "tracks.singlePlayer.deprecated.sampleGA.Agent";
	String[][] games;
	int[] gameIdx = {0,13,18};

 /**
  * Constructor.
  * Creates a instance of  problem.
  * @param numberOfVariables Number of variables.
  * @param solutionType The solution type must "Real", "BinaryReal, and "ArrayReal".
  */
  public AliensButterfliesChase(int gamenum)
  {
    numberOfVariables_  = 5;
    numberOfObjectives_ =  2;
    numberOfConstraints_=  0;

    // set gameid1 gameid2 and gameid3 according to given number 0:alien  13:butterflies 18:chase
    if (gamenum==0)
    {
    	problemName_        = "AliensButterflies";
    	gameIdx[0]=0;
    	gameIdx[1]=13;
    }
    else if (gamenum==1)
    {
    	problemName_        = "AliensChase";
    	gameIdx[0]=0;
    	gameIdx[1]=18;
    }
    else if (gamenum==2)
    {
    	problemName_        = "ButterfliesChase";
    	gameIdx[0]=13;
    	gameIdx[1]=18;
    }
    else
    {
    	problemName_        = "AliensButterfliesChase";
    	numberOfObjectives_ =  3;
    	gameIdx[0]=0;
    	gameIdx[1]=13;
    	gameIdx[2]=18;
    }


    upperLimit_ = new double[numberOfVariables_];
    lowerLimit_ = new double[numberOfVariables_];

    // variables GAMMA, SIMULATION DEPTH, POPULATION SIZE, RECPROB, MUT

    //gamma
    lowerLimit_[0] = 0.0; upperLimit_[0] = 1.0;
    //SIMULATION DEPTH
    lowerLimit_[1] = 1.0; upperLimit_[1] = 10.0;
    //POPULATION SIZE
    lowerLimit_[2] = 5.0; upperLimit_[2] = 50.0;
    //RECPROB
    lowerLimit_[3] = 0.0; upperLimit_[3] = 1.0;
    //MUT
    lowerLimit_[4] = 0.0; upperLimit_[4] = 1.0;

    solutionType_ = new RealSolutionType(this) ;

    String spGamesCollection =  "examples/all_games_sp.csv";
    games = Utils.readGames(spGamesCollection);

  }



  /**
  * Evaluates a solution
  * @param solution The solution to evaluate
   * @throws JMException
  */
  public void evaluate(Solution solution) throws JMException
  {
	  XReal x = new XReal(solution) ;
	  Variable[] ss=solution.getDecisionVariables();
	  double [] sol=new double[5];
	  sol[0]=ss[0].getValue();
	  sol[1]=ss[1].getValue();
	  sol[2]=ss[2].getValue();
	  sol[3]=ss[3].getValue();
	  sol[4]=ss[4].getValue();

	  // 0th index is win/loss, 1st element is score, 2nd element is timetick
      double [][] f = new double[numberOfObjectives_][3] ;

      for (int i=0;i<numberOfObjectives_;i++)
      {
    	  int seed = new Random().nextInt();

    	  int levelIdx = 0;
    	  String gameName = games[gameIdx[i]][1];
    	  String game = games[gameIdx[i]][0];
    	  String level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);
          f[i]= ArcadeMachine.runOneGame(game, level1, false, gaController, null, seed, 0,sol);
          // we set 1st index element which is score for total point.
          // we get it negative since the aim is minimization
          solution.setObjective(i,-1*f[i][1]);
      }

  }


}


