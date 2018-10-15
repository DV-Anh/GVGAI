package tracks.singlePlayer.ECAssignment3.controllers.betterRHEA;

public class HyperParamSet {

    /**
     *     private int INDIVIDUAL_DEPTH = 20;        0
     *     private int POPULATION_SIZE = 2;          1
     *     private int TOURNAMENT_SIZE = 1;          2
     *     private double CROSSOVER_RATE = 0.50;     3
     *     private double MUTATION_RATE = 0.10;      4
     *     private double WIN_BONUS=0.1;             5
     *     private double LOSE_PENALTY=1000000;      6
     */

    public final int INDIVIDUAL_DEPTH;    // 0 - 1.0 , gamma / 1000, 0 - 1001
    public final int POPULATION_SIZE;  // 1 - 20
    public final int TOURNAMENT_SIZE;   // 2 - 20
    public final int CROSSOVER_RATE,MUTATION_RATE,WIN_BONUS,LOSE_PENALTY;

    public HyperParamSet(int INDIVIDUAL_DEPTH, int POPULATION_SIZE, int TOURNAMENT_SIZE, int CROSSOVER_RATE,int MUTATION_RATE, int WIN_BONUS,int LOSE_PENALTY) {
        this.INDIVIDUAL_DEPTH = INDIVIDUAL_DEPTH;
        this.POPULATION_SIZE = POPULATION_SIZE;
        this.TOURNAMENT_SIZE = TOURNAMENT_SIZE;
        this.CROSSOVER_RATE = CROSSOVER_RATE;
        this.MUTATION_RATE =MUTATION_RATE;
        this.WIN_BONUS=WIN_BONUS;
        this.LOSE_PENALTY=LOSE_PENALTY;
    }


    @Override
    public String toString() {
        return "HyperParamSet{" +
                "INDIVIDUAL_DEPTH=" + INDIVIDUAL_DEPTH +
                ", POPULATION_SIZE=" + POPULATION_SIZE +
                ", TOURNAMENT_SIZE=" + TOURNAMENT_SIZE +
                ", CROSSOVER_RATE=" + CROSSOVER_RATE +
                ", MUTATION_RATE=" + MUTATION_RATE +
                ", WIN_BONUS=" + WIN_BONUS +
                ", LOSE_PENALTY=" + LOSE_PENALTY +
                '}';
    }
}
