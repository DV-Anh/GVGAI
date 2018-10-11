package tracks.singlePlayer.ECAssignment3.Exercise2;

import tools.Utils;

import javax.tools.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * 1. Individual: a vector of hyperparameters
 * 2. Fitness: Average score of playing game 5 times
 * 3. CrossOver: Selecting one point and plain cross over
 * 4. Mutation: Mutating each parameter with a probability. How to mutate each parameter?
 * 5. Population size ?
 * 6. How many generation ?
 * 7. Using code from the first assignment.
 */
public class HyperTuning {
    private final static String AGENT_TEMPLATE_PATH = "src/tracks/singlePlayer/ECAssignment3/controllers/tunedGA/AgentTemplate";
    private final static String AGENT_PATH = "src/tracks/singlePlayer/ECAssignment3/controllers/tunedGA/Agent.java";
    private final static String AGENT_CLASS_PATH = "out/production/gvgai/tracks/singlePlayer/ECAssignment3/controllers/tunedGA/Agent.class";
    private final static String controller = "tracks.singlePlayer.ECAssignment3.controllers.tunedGA.Agent";
    private final static String[][] games = Utils.readGames("examples/all_games_sp.csv");

    public static void main(String[] args) throws IOException, InterruptedException {
        //System.out.println("Game 0: " + tuneGA(0));
        //System.out.println("Game 11: " + tuneGA(11));
        //System.out.println("Game 13: " + tuneGA(13));
        //System.out.println("Game 18: " + tuneGA(18));
        HyperParamSet params = new HyperParamSet(948, 11, 13, 409);
        getFiveTimesScore(0, params);
    }

    private static HyperParamSet tuneGA(int gameID) throws IOException {
        int popSize = 5;
        int gen = 10;
        
        List<HyperParamSet> population = new LinkedList<>();
        for (int i = 0; i < popSize; i++) {
            HyperParamSet hs = new HyperParamSet();
            hs.setFitness(fitness(gameID, hs));
            System.out.println(hs.toString());
            population.add(hs);
        }

        for (int i = 0; i < gen; i++) {
            HyperParamSet[] parents = parentSelect(population);
            HyperParamSet[] offsprings = Mutate(CrossOver(parents));
            offsprings[0].setFitness(fitness(gameID, offsprings[0]));
            offsprings[1].setFitness(fitness(gameID, offsprings[1]));
            population.add(offsprings[0]);
            population.add(offsprings[1]);
        }

        HyperParamSet best = null;
        double bestFit = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < population.size(); i++) {
            if (population.get(i).getFitness() > bestFit) {
                bestFit = population.get(i).getFitness();
                best = population.get(i);
            }
        }

        return best;
    }

    private static HyperParamSet[] Mutate(HyperParamSet[] individuals) {
        HyperParamSet[] mutated = new HyperParamSet[2];
        mutated[0] = uniformMutate(individuals[0]);
        mutated[1] = uniformMutate(individuals[1]);

        return mutated;
    }

    private static HyperParamSet uniformMutate(HyperParamSet individual) {
        Random rand = new Random();
        double prob = 1.0 / 5.0;
        int gamma = individual.gamma;
        int simuDepth = individual.simulationDepth;
        int popSize = individual.popSize;
        int recprob = individual.recprob;
        if (rand.nextDouble() < prob) {
            gamma = rand.nextInt(1001);
        }
        if (rand.nextDouble() < prob) {
            simuDepth = 1 + rand.nextInt(20);
        }
        if (rand.nextDouble() < prob) {
            popSize = 2 + rand.nextInt(19);
        }
        if (rand.nextDouble() < prob) {
            recprob = rand.nextInt(1001);
        }
        return new HyperParamSet(gamma, simuDepth, popSize, recprob);
    }

    private static HyperParamSet[] CrossOver(HyperParamSet[] parents) {
        HyperParamSet[] offsprings = new HyperParamSet[2];
        int x = new Random().nextInt(3);
        if (x == 0) {
            offsprings[0] = new HyperParamSet(
                    parents[1].gamma, parents[0].simulationDepth, parents[0].popSize, parents[0].recprob
            );
            offsprings[1] = new HyperParamSet(
                    parents[0].gamma, parents[1].simulationDepth, parents[1].popSize, parents[1].recprob
            );

        } else if (x == 1) {
            offsprings[0] = new HyperParamSet(
                    parents[1].gamma, parents[1].simulationDepth, parents[0].popSize, parents[0].recprob
            );
            offsprings[1] = new HyperParamSet(
                    parents[0].gamma, parents[0].simulationDepth, parents[1].popSize, parents[1].recprob
            );
        } else {
            offsprings[0] = new HyperParamSet(
                    parents[1].gamma, parents[1].simulationDepth, parents[1].popSize, parents[0].recprob
            );
            offsprings[1] = new HyperParamSet(
                    parents[0].gamma, parents[0].simulationDepth, parents[0].popSize, parents[1].recprob
            );
        }

        return offsprings;
    }

    private static HyperParamSet[] parentSelect(List<HyperParamSet> population) {
        HyperParamSet[] parents = new HyperParamSet[2];
        parents[0] = tournament(population);
        parents[1] = tournament(population);
        return parents;
    }

    private static HyperParamSet tournament(List<HyperParamSet> population) {
        Random rand = new Random();
        int i = rand.nextInt(population.size());
        int j = rand.nextInt(population.size());
        double vi = population.get(i).getFitness();
        double vj = population.get(j).getFitness();
        HyperParamSet res;

        if (vi > vj) {
            res = population.remove(i);
        } else {
            res = population.remove(j);
        }

        return res;
    }

    private static double fitness(int gameID, HyperParamSet params) throws IOException {
//        generateNewAgent(params.gamma / 1000.0,
//                params.simulationDepth,
//                params.popSize,
//                params.recprob / 1000.0);
//        recompileAgent();
        return getFiveTimesScore(gameID, params);
    }

    private static double getFiveTimesScore(int gameID, HyperParamSet params) {
        // Play 5 times and get average score
        int levelIdx = 1; // level names from 0 to 4 (game_lvlN.txt).
        String gameName = games[gameID][1];
        String game = games[gameID][0];
        String level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);
        String recordActionsFile = null;// "actions_" + games[gameIdx] + "_lvl"

        double ttl = 0.0;
        for (int i = 0; i < 5; i++) {
            int seed = new Random().nextInt();
            double[] res = ArcadeMachine.runOneGame(
                    game, level1,
                    false, controller,
                    null, seed, 0, params);
            System.out.println(res[0] + "," + res[1] + "," + res[2]);
            ttl += res[1];
        }
        return ttl / 5;
    }

    private static void generateNewAgent(double gamma, int depth, int popsize, double recprob) throws IOException {
        String content = readAgentTemplate(AGENT_TEMPLATE_PATH, Charset.defaultCharset());
        String agent = String.format(content, gamma, depth, popsize, recprob);
        try (PrintWriter out = new PrintWriter(AGENT_PATH)) {
            out.println(agent);
            out.flush();
        }
    }

    private static void recompileAgent() throws IOException {
        final String root = "./out/production/gvgai";
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        List<String> optionList = new ArrayList<>();
        optionList.add("-classpath");
        optionList.add(System.getProperty("java.class.path") + ";" + root);
        optionList.add("-d");
        optionList.add(root);
        Iterable<? extends JavaFileObject> compilationUnit
                = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(new File(AGENT_PATH)));
        JavaCompiler.CompilationTask task = compiler.getTask(
                null,
                fileManager,
                diagnostics,
                optionList,
                null,
                compilationUnit);
        task.call();
        fileManager.close();
    }

    private static String readAgentTemplate(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
