package tracks.singlePlayer.ECAssignment2.controllers.betterRHEA;

import java.util.*;

public class Individual implements Comparable {
    private Random randomGenerator;
    private int num_actions;
    public int size;
    public double value;
    public LinkedList<Integer> actions;

    public Individual(int size, int num_actions) {
        actions = new LinkedList<>();
        randomGenerator = new Random();
        this.size=size;
        this.num_actions=num_actions;
        value=Double.NEGATIVE_INFINITY;
    }

    @Override
    public int compareTo(Object other) {return Double.compare(this.value,((Individual)other).value);}

    public void randomise() {
        actions = new LinkedList<>();
        for (int i=0; i<size; i++)
            actions.add(randomGenerator.nextInt(num_actions));
    }

    public void shift() {
        actions.add(randomGenerator.nextInt(num_actions));
        actions.remove(0);
    }

    public void rotate() {
        actions.add(actions.get(0));
        actions.remove(0);
    }

    public void transshift(int move) {
        int index=actions.indexOf(move);
        if (index>=0) {
            actions.add(randomGenerator.nextInt(num_actions));
            actions.remove(index);
        }
        else this.shift();
    }

    public void transrotate(int move) {
        int index=actions.indexOf(move);
        if (index>=0) {
            actions.add(move);
            actions.remove(index);
        }
        else this.rotate();
    }

    public void crossmutation(Individual[] parent, double crossoverRate, double mutationRate) {
        actions = new LinkedList<>();
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