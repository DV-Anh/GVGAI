package tracks.singlePlayer.ECAssignment3.Exercise3;

import java.util.Random;

public class HyperParamSet {
    public final double pow_factor;    // 0 - 1.0 , gamma / 1000, 0 - 1001
    public final double grth_lin;  // 1 - 20
    public final double grth_quad;   // 2 - 20
    public final double shrk_frame;   // 0 - 1.0, recprob / 1000, 0 - 1000
    public final double shrk_imp;
    private double fitness;

    public HyperParamSet(double v1, double v2, double v3, double v4, double v5) {
        pow_factor = v1;    // 0 - 1.0 , gamma / 1000, 0 - 1001
        grth_lin = v2;  // 1 - 20
        grth_quad = v3;   // 2 - 20
        shrk_frame = v4;   // 0 - 1.0, recprob / 1000, 0 - 1000
        shrk_imp = v5;
    }

//    public HyperParamSet() {
//        Random rand = new Random();
//        gamma = rand.nextInt(1001);
//        simulationDepth = 1 + rand.nextInt(20);
//        popSize = 2 + rand.nextInt(19);
//        recprob = rand.nextInt(1001);
//    }


    @Override
    public String toString() {
        return "HyperParamSet{" +
                "pow_factor=" + pow_factor +
                ", grth_lin=" + grth_lin +
                ", grth_quad=" + grth_quad +
                ", shrk_frame=" + shrk_frame +
                ", shrk_imp=" + shrk_imp +
                ", fitness=" + fitness +
                '}';
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getFitness() {
        return this.fitness;
    }
}
