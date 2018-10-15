package tracks.singlePlayer.ECAssignment3.controllers.betterRHEA;

import org.uma.jmetal.problem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.IntegerSolution;
import tools.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Muliti_obj_Problem extends AbstractIntegerProblem {
    int numberOfVariables;
    int numberOfObjectives;
    private final static String controller = "tracks.singlePlayer.ECAssignment3.controllers.betterRHEA.Agent";
    private final static String[][] games = Utils.readGames("examples/all_games_sp.csv");

    public Muliti_obj_Problem()
    {
        this(7,3);
    }

    public Muliti_obj_Problem(int numberOfVariables,int numberOfObjectives)
    {
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(numberOfObjectives);
        List<Integer> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
        List<Integer> upperLimit = new ArrayList<>(getNumberOfVariables()) ;
        //Domain
        /**
         *     private int INDIVIDUAL_DEPTH = 20;        0
         *     private int POPULATION_SIZE = 2;          1
         *     private int TOURNAMENT_SIZE = 1;          2
         *     private double CROSSOVER_RATE = 0.50;     3
         *     private double MUTATION_RATE = 0.10;      4
         *     private double WIN_BONUS=0.1;             5
         *     private double LOSE_PENALTY=1000000;      6
         */
        for (int i = 0; i < getNumberOfVariables(); i++)
        {
            if (i==0)
            {
                lowerLimit.add(0);
                upperLimit.add(30);
            }
            if(i==1)
            {
                lowerLimit.add(3);
                upperLimit.add(10);
            }
            if(i==2)
            {
                lowerLimit.add(1);
                upperLimit.add(3);
            }
            if (i==3||i==4)
            {
                lowerLimit.add(0);
                upperLimit.add(1);
            }
            if (i==5||i==6)
            {
                lowerLimit.add(0);
                upperLimit.add(10000);
            }

        }
        setLowerLimit(lowerLimit);
        setUpperLimit(upperLimit);


    }

    public void setNumberOfVariables(int numberOfVariables) {
        this.numberOfVariables = numberOfVariables;
    }
    public void setNumberOfObjectives(int numberOfObjectives) {
        this.numberOfObjectives = numberOfObjectives;
    }


    public int getNumberOfVariables() {
        return numberOfVariables;
    }

    public int getNumberOfObjectives() {
        return numberOfObjectives;
    }

    @Override
    public void evaluate(IntegerSolution solution) {
        System.out.println("Solution "+solution);

        HyperParamSet param=new HyperParamSet(solution.getVariableValue(0),solution.getVariableValue(1),solution.getVariableValue(2),
                solution.getVariableValue(3),solution.getVariableValue(4),solution.getVariableValue(5),solution.getVariableValue(6));

        int runGameTime=1;
        double[] Objectives = new double[this.getNumberOfObjectives()];
        //Maximun score, need add new objective: win!
        Objectives[0]=80;//al
        Objectives[1]=80;//butt
        Objectives[2]=15;//bo
        Objectives[0]=getScore(0,param,runGameTime)+3;
        Objectives[1]=getScore(13,param,runGameTime)+3;
        Objectives[2]=getScore(18,param,runGameTime)+3;
        solution.setObjective(0, Objectives[0]);
        solution.setObjective(1, Objectives[1]);
        solution.setObjective(2, Objectives[2]);

    }




    private  double getScore(int gameID, HyperParamSet params, int runGameTime) {
        // Play and get average score
        int levelIdx = 0; // level names from 0 to 4 (game_lvlN.txt).
        String gameName = games[gameID][1];
        String game = games[gameID][0];
        String level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);
        String recordActionsFile = null;// "actions_" + games[gameIdx] + "_lvl"

        double ttl = 0.0;
        for (int i = 0; i < runGameTime; i++) {
            int seed = new Random().nextInt();
            double[] res = ArcadeMachine.runOneGame(
                    game, level1,
                    false, controller,
                    null, seed, 0, params);
            System.out.println(" "+res[0] + "," + res[1] + "," + res[2]);
            ttl += res[1];
        }
//        System.out.println(ttl / runGameTime);
        return ttl / runGameTime;
    }
}
