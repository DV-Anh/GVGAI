package tracks.singlePlayer.ECAssignment2.Exercise2;

import tools.Utils;
import tracks.ArcadeMachine;

import javax.tools.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
    private final static String AGENT_TEMPLATE_PATH = "src/tracks/singlePlayer/ECAssignment2/controllers/tunedGA/AgentTemplate";
    private final static String AGENT_PATH = "src/tracks/singlePlayer/ECAssignment2/controllers/tunedGA/Agent.java";
    private final static String AGENT_CLASS_PATH = "out/production/gvgai/tracks/singlePlayer/ECAssignment2/controllers/tunedGA/Agent.class";
    private final static String controller = "tracks.singlePlayer.ECAssignment2.controllers.tunedGA.Agent";
    private final static String[][] games = Utils.readGames("examples/all_games_sp.csv");

    public static void main(String[] args) throws IOException, InterruptedException {
        String content = readAgentTemplate(AGENT_TEMPLATE_PATH, Charset.defaultCharset());

        String agent = String.format(content, 0.9, 10, 5, 0.1);
        try (PrintWriter out = new PrintWriter(AGENT_PATH)) {
            out.println(agent);
            out.flush();
        }

//        Runtime rt = Runtime.getRuntime();
//        Process pr = rt.exec("javac -d ./out/production/gvgai -cp ./out/production/gvgai ./src/tracks/single\n" +
//                "Player/ECAssignment2/controllers/tunedGA/Agent.java");

        /** Compilation Requirements *********************************************************************************************/
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

        // This sets up the class path that the compiler will use.
        // I've added the .jar file that contains the DoStuff interface within in it...
        List<String> optionList = new ArrayList<String>();
        optionList.add("-classpath");
        optionList.add(System.getProperty("java.class.path") + ";./out/production/gvgai");
        optionList.add("-d");
        optionList.add("./out/production/gvgai");

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

        int gameID = 0;
        int levelIdx = 0; // level names from 0 to 4 (game_lvlN.txt).
        String gameName = games[gameID][1];
        String game = games[gameID][0];
        String level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);
        String recordActionsFile = null;// "actions_" + games[gameIdx] + "_lvl"
        for (int i = 0; i < 5; i++) {
            int seed = new Random().nextInt();
            double[] res = ArcadeMachine.runOneGame(
                    game, level1,
                    false, controller,
                    recordActionsFile, seed, 0);
            System.out.println(res[0] + "," + res[1] + "," + res[2]);
        }
    }

    private static String readAgentTemplate(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
