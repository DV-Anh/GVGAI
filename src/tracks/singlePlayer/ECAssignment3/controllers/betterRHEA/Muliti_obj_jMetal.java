package tracks.singlePlayer.ECAssignment3.controllers.betterRHEA;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.IntegerSBXCrossover;
import org.uma.jmetal.operator.impl.mutation.IntegerPolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;

import java.io.FileNotFoundException;
import java.util.List;

import static org.uma.jmetal.util.AbstractAlgorithmRunner.printFinalSolutionSet;
import static org.uma.jmetal.util.AbstractAlgorithmRunner.printQualityIndicators;

public class Muliti_obj_jMetal {
    public static void main(String[] arg0) throws FileNotFoundException
    {
        Problem<IntegerSolution> problem;
        Algorithm<List<IntegerSolution>> algorithm;
        CrossoverOperator<IntegerSolution> crossover;
        MutationOperator<IntegerSolution> mutation;
        SelectionOperator<List<IntegerSolution>,IntegerSolution> selection;
        String referenceParetoFront="";
        problem=new Muliti_obj_Problem();
        double crossoverProbability = 0.8;
        double DistributionIndex = 20.0;
        crossover = new IntegerSBXCrossover(crossoverProbability, DistributionIndex);
        double mutationProbability = 0.5;
        mutation = new IntegerPolynomialMutation(mutationProbability,DistributionIndex);
        selection = new BinaryTournamentSelection<>(
                new RankingAndCrowdingDistanceComparator<IntegerSolution>());
        algorithm = new NSGAIIBuilder<IntegerSolution>(problem, crossover, mutation)
                .setSelectionOperator(selection)
                .setMaxEvaluations(12)//100/10=10 generation
                .setPopulationSize(4)
                .build();
        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();
        List<IntegerSolution> population = algorithm.getResult();
        long computingTime = algorithmRunner.getComputingTime();
        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
        printFinalSolutionSet(population);
        if (!referenceParetoFront.equals(""))
            printQualityIndicators(population, referenceParetoFront);

    }
}
