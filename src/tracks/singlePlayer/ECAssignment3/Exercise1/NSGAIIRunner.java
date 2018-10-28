package tracks.singlePlayer.ECAssignment3.Exercise1;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.*;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

/**
 * Class to configure and run the NSGA-II algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NSGAIIRunner extends AbstractAlgorithmRunner {
    /**
     * @param args Command line arguments.
     * @throws JMetalException
     * @throws FileNotFoundException
     * Invoking command:
    java org.uma.jmetal.runner.multiobjective.NSGAIIRunner problemName [referenceFront]
     */
    public static void main(String[] args) throws JMetalException, FileNotFoundException {
        Problem<DoubleSolution> problem, problem2;
        Algorithm<List<DoubleSolution>> algorithm;
        CrossoverOperator<DoubleSolution> crossover;
        MutationOperator<DoubleSolution> mutation;
        SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;
        String referenceParetoFront = "",referenceParetoFront2 = "";

        String problemName, problemName2;
        if (args.length == 1) {
            problemName = args[0];
            problemName2 = args[0];
        } else if (args.length == 2) {
            problemName = args[0] ;
            problemName2 = args[0];
            referenceParetoFront = args[1] ;
        } else {
            problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT2";
            problemName2 = "org.uma.jmetal.problem.multiobjective.zdt.ZDT3";
            referenceParetoFront = "jmetal-problem/src/test/resources/pareto_fronts/ZDT2.pf" ;
            referenceParetoFront2 = "jmetal-problem/src/test/resources/pareto_fronts/ZDT3.pf" ;
        }

        problem = ProblemUtils.<DoubleSolution> loadProblem(problemName);
        problem2 = ProblemUtils.<DoubleSolution> loadProblem(problemName2);
        double crossoverProbability = 0.9 ;
        double crossoverDistributionIndex = 20.0 ;
        crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex) ;

        double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
        double mutationDistributionIndex = 20.0 ;
        mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

        selection = new BinaryTournamentSelection<DoubleSolution>(
                new RankingAndCrowdingDistanceComparator<DoubleSolution>());

//    algorithm = new NSGAIIBuilder<DoubleSolution>(problem, crossover, mutation)
//        .setSelectionOperator(selection)
//        .setMaxEvaluations(10000)
//        .setPopulationSize(10)
//        .build() ;
//
//    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
//        .execute() ;
//
//    List<DoubleSolution> population = algorithm.getResult() ;
//    long computingTime = algorithmRunner.getComputingTime() ;
//
//    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
//
//    printFinalSolutionSet(population);
//    if (!referenceParetoFront.equals("")) {
//      printQualityIndicators(population, referenceParetoFront) ;
//    }


        int[] popSizes={10,100,1000};
        File fun1,fun2,var1,var2;
        AlgorithmRunner algorithmRunner;
        List<DoubleSolution> population;
        for (int i = 0; i < popSizes.length; i++) {
            algorithm = new NSGAIIBuilder<DoubleSolution>(problem, crossover, mutation)
                    .setSelectionOperator(selection)
                    .setMaxEvaluations(10000*popSizes[i])
                    .setPopulationSize(popSizes[i])
                    .build();
            algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();
            population = algorithm.getResult();
            printFinalSolutionSet(population);
            fun1=new File("FUN.tsv");
            var1=new File("VAR.tsv");
            fun2=new File("FUN ZDT2 POP = "+String.valueOf(popSizes[i])+".tsv");
            var2=new File("VAR ZDT2 POP = "+String.valueOf(popSizes[i])+".tsv");
            fun1.renameTo(fun2);var1.renameTo(var2);
            algorithm = new NSGAIIBuilder<DoubleSolution>(problem2, crossover, mutation)
                    .setSelectionOperator(selection)
                    .setMaxEvaluations(10000*popSizes[i])
                    .setPopulationSize(popSizes[i])
                    .build();
            algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();
            population = algorithm.getResult();
            printFinalSolutionSet(population);
            fun1=new File("FUN.tsv");
            var1=new File("VAR.tsv");
            fun2=new File("FUN ZDT3 POP = "+String.valueOf(popSizes[i])+".tsv");
            var2=new File("VAR ZDT3 POP = "+String.valueOf(popSizes[i])+".tsv");
            fun1.renameTo(fun2);var1.renameTo(var2);
        }
    }
}

