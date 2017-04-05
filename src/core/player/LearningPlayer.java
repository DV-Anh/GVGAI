package core.player;

import com.google.gson.Gson;
import core.ServerComm;
import core.VGDLRegistry;
import core.game.SerializableStateObservation;
import core.game.StateObservation;
import core.game.StateObservationMulti;
import core.game.StateView;
import ontology.Types;
import tools.ElapsedCpuTimer;

import javax.sql.rowset.serial.SerialArray;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static core.ServerComm.commRecv;

/**
 * Created by Daniel on 07.03.2017.
 */
public class LearningPlayer extends Player {

    private static final Logger logger = Logger.getLogger(LearningPlayer.class.getName());

    /**
     * Last action executed by this agent.
     */
    private Types.ACTIONS lastAction = null;

    /**
     * Line separator for messages.
     */
    private String lineSep = System.getProperty("line.separator");

    /**
     * Client process
     */
    private Process client;

    /**
     * Server communication channel
     */
    private ServerComm serverComm;

    /**
     * Learning Player constructor.
     * Creates a new server side communication channel for every player.
     */
    public LearningPlayer(Process proc){
        this.serverComm = new ServerComm(proc);
    }

//
//    public LearningPlayer(Process client) {
//        isLearner = true;
//
//
//        this.client = client;
//        initBuffers();
//
//
//    }

//    /**
//     * Creates the buffers for pipe communication.
//     */
//    private void initBuffers() {
//
//        input = new BufferedReader(new InputStreamReader(client.getInputStream()));
//        output = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
//
//
//    }

    public Types.ACTIONS act(SerializableStateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        return null;
    }

    /**
     * Picks an action. This function is called every game step to request an
     * action from the player. The action returned must be contained in the
     * actions accessible from stateObs.getAvailableActions(), or action NIL
     * will be applied.
     *
     * @param so     Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    @Override
    public Types.ACTIONS act(StateObservation so, ElapsedCpuTimer elapsedTimer) {
        serverComm.initBuffers();

        //Sending messages.
        try {
            // Set the game state to the appropriate state and the millisecond counter, then send the serialized observation.
            SerializableStateObservation sso = new SerializableStateObservation(so);
            sso.elapsedTimer = elapsedTimer.remainingTimeMillis();
            serverComm.commSend(sso.serialize(null));

            String response = commRecv(elapsedTimer, "ACT");
            logger.fine("Received ACTION: " + response + "; ACT Response time: "
                    + elapsedTimer.elapsedMillis() + " ms.");

            // TODO: 04/04/2017 Daniel: check for game ending (currentGameState is not static) ?
            if ("ABORT".equals(response)){
                so.currentGameState = Types.GAMESTATES.ABORT_STATE;
            }

            Types.ACTIONS action = Types.ACTIONS.fromString(response);
            return action;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Types.ACTIONS act(StateObservationMulti stateObs, ElapsedCpuTimer elapsedTimer) {
        return null;
    }

    @Override
    public Types.ACTIONS act(String stateObsStr, ElapsedCpuTimer elapsedTimer) {
        return null;
    }


    public void finishGame(StateObservation so, ElapsedCpuTimer elapsedTimer) throws IOException {
        serverComm.finishGame(so, elapsedTimer);
    }

    public ServerComm getServerComm() {
        return serverComm;
    }

//    /**
//     * Sends a message through the pipe.
//     *
//     * @param msg message to send.
//     */
//    public void commSend(String msg) throws IOException {
//
//        output.write(msg + lineSep);
//        output.flush();
//
//    }

//    /**
//     * Waits for a response during T milliseconds.
//     *
//     * @param elapsedTimer Timer when the initialization is due to finish.
//     * @param idStr        String identifier of the phase the communication is in.
//     * @return the response got from the client, or null if no response was received after due time.
//     */
//    // TODO: 27/03/2017 Daniel: check the whole method
//    public static String commRecv(ElapsedCpuTimer elapsedTimer, String idStr) throws IOException {
//        String ret = null;
//
//        while (elapsedTimer.remainingTimeMillis() > 0) {
//            if (input.ready()) {
//
//                ret = input.readLine();
//                if (ret != null && ret.trim().length() > 0) {
//                    //System.out.println("TIME OK");
//                    return ret.trim();
//                }
//            }
//        }
//
//
//        //if(elapsedTimer.remainingTimeMillis() <= 0)
//        //    System.out.println("TIME OUT (" + idStr + "): " + elapsedTimer.elapsedMillis());
//
//        return null;
//    }

//    public final void close() {
//        try {
//            input.close();
//            output.close();
//
//        } catch (IOException e) {
//            logger.severe("IO Exception closing the buffers: " + e.getStackTrace());
//
//        } catch (Exception e) {
//            logger.severe("Exception closing the buffers: " + e.getStackTrace());
//
//        }
//    }

}

