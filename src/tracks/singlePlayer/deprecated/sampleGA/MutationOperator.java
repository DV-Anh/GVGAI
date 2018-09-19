/* package tracks.singlePlayer.deprecated.sampleGA;

import java.util.ArrayList;
import java.util.List;

public class MutationOperator {

    public MutationOperator(){

    }

    public List<Individual> mutateByInverseAndModulus(List<Individual> individualList) {

        List<Individual> mutatedIndividual = new ArrayList<Individual>();

        for(int counter = 0; counter < individualList.size(); counter++) {

            GAParameterSetting setting_temp = new GAParameterSetting(individualList.get(counter).setting);

            setting_temp.USED_DEFAULT_MUT = false;
            setting_temp.MUT = 1.0 -  setting_temp.MUT;
            setting_temp.GAMMA = 1.0 - setting_temp.GAMMA;
            setting_temp.SIMULATION_DEPTH = (setting_temp.SIMULATION_DEPTH + 7)%14 + 1;
            setting_temp.POPULATION_SIZE = (setting_temp.POPULATION_SIZE + 10)%15 + 5;
            setting_temp.RECPROB = 1.0 - setting_temp.RECPROB;

            Individual i = new Individual(setting_temp);
            mutatedIndividual.add(i);

        }

        return mutatedIndividual;

    }

}
*/
