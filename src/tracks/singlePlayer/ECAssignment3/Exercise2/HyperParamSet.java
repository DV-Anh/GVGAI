package tracks.singlePlayer.ECAssignment3.Exercise2;

import java.util.Random;

public class HyperParamSet {
    public final int gamma;    // 0 - 1.0 , gamma / 1000, 0 - 1001
    public final int simulationDepth;  // 1 - 20
    public final int popSize;   // 2 - 20
    public final int recprob;   // 0 - 1.0, recprob / 1000, 0 - 1000
    private double fitness;

    public HyperParamSet(int g, int s, int p, int r) {
        gamma = g;
        simulationDepth = s;
        popSize = p;
        recprob = r;
    }

    public HyperParamSet() {
        Random rand = new Random();
        gamma = rand.nextInt(1001);
        simulationDepth = 1 + rand.nextInt(20);
        popSize = 2 + rand.nextInt(19);
        recprob = rand.nextInt(1001);
    }

    @Override
    public String toString() {
        return "HyperParamSet{" +
                "gamma=" + gamma +
                ", simulationDepth=" + simulationDepth +
                ", popSize=" + popSize +
                ", recprob=" + recprob +
                '}';
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getFitness() {
        return this.fitness;
    }
}
