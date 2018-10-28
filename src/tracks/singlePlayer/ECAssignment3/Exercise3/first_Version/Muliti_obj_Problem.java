package tracks.singlePlayer.ECAssignment3.Exercise3.first_Version;

import org.uma.jmetal.problem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;
import tools.Utils;
import tracks.singlePlayer.ECAssignment3.controllers.betterRHEA.ArcadeMachine;
import tracks.singlePlayer.ECAssignment3.controllers.betterRHEA.HyperParamSet;

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
        this(5,3);
    }

    public Muliti_obj_Problem(int numberOfVariables,int numberOfObjectives)
    {
        setName("betterRHEA");
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(numberOfObjectives);
        List<Integer> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
        List<Integer> upperLimit = new ArrayList<>(getNumberOfVariables()) ;
        //Domain
        /**
         *     private int INDIVIDUAL_DEPTH = 20;        0
         *     private double CROSSOVER_RATE = 0.50;     1
         *     private double MUTATION_RATE = 0.10;      2
         *     private double WIN_BONUS=0.1;             3
         *     private double LOSE_PENALTY=1000000;      4
         */
        for (int i = 0; i < getNumberOfVariables(); i++)
        {
            if (i==0)
            {
                lowerLimit.add(0);
                upperLimit.add(30);
            }

            if (i==1||i==2)
            {
                lowerLimit.add(0);
                upperLimit.add(100);
            }
            if (i==3||i==4)
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

        HyperParamSet param=new HyperParamSet(solution.getVariableValue(0),
                solution.getVariableValue(1),solution.getVariableValue(2),solution.getVariableValue(3),solution.getVariableValue(4));

        int runGameTime=1;
        double[] Objectives = new double[this.getNumberOfObjectives()];
        //Maximun score, need add new objective: win!
//        int[] games=new int[]{0,11,13,18};
        int[] games=new int[]{0,13,18};//(0,13) (0,18),(13,18)
        for (int i = 0; i < games.length; i++)
            solution.setObjective(i, getNTimesScore(games[i],param,runGameTime));

    }




    private  double getNTimesScore(int gameID, HyperParamSet params, int runGameTime) {
        // Play and get average score
        int levelIdx = 0; // level names from 0 to 4 (game_lvlN.txt).
        String gameName = games[gameID][1];
        String game = games[gameID][0];
        String level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);
        String recordActionsFile = null;// "actions_" + games[gameIdx] + "_lvl"

        double ttl = 0.0;
        double ttl_step = 0.0;
        for (int i = 0; i < runGameTime; i++) {
            int seed = new Random().nextInt();
            double[] res = ArcadeMachine.runOneGame(
                    game, level1,
                    false, controller,
                    null, seed, 0, params);
            System.out.println(" "+res[0] + "," + res[1] + "," + res[2]);
            ttl += res[1];
            ttl_step+=res[2];
        }
        double fitness=ttl/runGameTime+1.0/ttl_step*10;
        return Math.max(1/fitness,0.000000000001);
    }
}
