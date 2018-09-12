package tracks.singlePlayer.ECAssignment2.controllers.betterRHEA;

import java.util.*;

public class Individual implements Comparable {
    private Random randomGenerator;
    private int num_actions;
    public int size;
    public double value;
    public ArrayList<Integer> actions;

    // Construct with an empty actions list
    public Individual(int size, int num_actions) {
        actions = new ArrayList<>();
        randomGenerator = new Random();
        this.size=size;
        this.num_actions=num_actions;
        value=Double.NEGATIVE_INFINITY;
    }

    // Define compareTo for sorting individuals in a population
    @Override
    public int compareTo(Object other) {return Double.compare(this.value,((Individual)other).value);}

    // Create a new action list with random actions
    public void randomise() {
        actions = new ArrayList<>();
        for (int i=0; i<size; i++)
            actions.add(randomGenerator.nextInt(num_actions));
    }

    // Remove first action and add a random last action
    public void shift() {
        actions.add(randomGenerator.nextInt(num_actions));
        actions.remove(0);
    }

    // Move first action to the last action
    public void rotate() {
        actions.add(actions.get(0));
        actions.remove(0);
    }

    // Remove first entry of an action and add a random last action
    // performing a "shift" if the action isn't in the action list
    public void transshift(int action) {
        int index=actions.indexOf(action);
        if (index>=0) {
            actions.add(randomGenerator.nextInt(num_actions));
            actions.remove(index);
        }
        else this.shift();
    }

    // Move first entry of an action to the last action
    // performing a "rotate" if the action isn't in the action list
    public void transrotate(int action) {
        int index=actions.indexOf(action);
        if (index>=0) {
            actions.add(action);
            actions.remove(index);
        }
        else this.rotate();
    }

    // Construct an action list from the action lists of two parents
    // reading from one parent, and swapping between the parents with "crossoverRate" probability
    // and have a "mutationRate" probability of choosing another action so as to mutate
    public void crossmutation(Individual[] parent, double crossoverRate, double mutationRate) {
        actions = new ArrayList<>();
        int pointer=0;
        for (int i=0; i<size; i++) {
            int parentaction=parent[pointer].actions.get(i);
            int action=parentaction;
            if (randomGenerator.nextDouble()<crossoverRate) pointer=1-pointer;
            if (randomGenerator.nextDouble()<mutationRate)
                while (action==parentaction)
                    action=randomGenerator.nextInt(num_actions);
            actions.add(action);
        }
    }
}
