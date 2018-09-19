/* package tracks.singlePlayer.deprecated.sampleGA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Population {

    public List<Individual> individuals;

    public Population() {

        individuals = new ArrayList<Individual>();
    }

    public void generateRandomIndividuals(Integer max_population) {
        for(int i =0; i < max_population; i++) {

            Individual ind = new Individual();
            ind.calculateFitness();
            individuals.add(ind);

        }
    }

    public void performElitismSurvival(Integer max_population) {
        Collections.sort(individuals, (s1, s2) ->
            Double.compare(s2.getFitness(), s1.getFitness()));


        individuals = individuals.subList(0,max_population);


    }

    public Individual getBestIndividualByFitness() {


        Collections.sort(individuals, (s1, s2) ->
            Double.compare(s2.getFitness(), s1.getFitness()));



        return individuals.get(0);

    }


} */
