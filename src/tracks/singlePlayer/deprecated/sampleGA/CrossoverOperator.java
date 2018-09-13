package tracks.singlePlayer.deprecated.sampleGA;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CrossoverOperator {

    public CrossoverOperator() {

    }

    public List<Individual> CrossoverByAverageRealValue(List<Individual> parent) {
        GAParameterSetting setting = new GAParameterSetting();

        setting.GAMMA = (parent.get(0).setting.GAMMA + parent.get(1).setting.GAMMA)/2;
        setting.RECPROB = (parent.get(0).setting.RECPROB + parent.get(1).setting.RECPROB)/2;
        setting.POPULATION_SIZE = (parent.get(0).setting.POPULATION_SIZE + parent.get(1).setting.POPULATION_SIZE)/2;
        setting.SIMULATION_DEPTH = (parent.get(0).setting.SIMULATION_DEPTH + parent.get(1).setting.SIMULATION_DEPTH)/2;
        setting.MUT = (parent.get(0).setting.MUT + parent.get(1).setting.MUT)/2;
        setting.USED_DEFAULT_MUT = false;

        Individual i = new Individual(setting);

        ArrayList<Individual> list_i = new ArrayList<Individual>();

        list_i.add(i);
        return list_i;

    }


    public List<Individual> CrossoverByUniformExchange(List<Individual> parent) {

        GAParameterSetting setting1 = new GAParameterSetting();
        GAParameterSetting setting2 = new GAParameterSetting();

        Random r = new Random();

        if(r.nextBoolean()) {
            setting1.GAMMA = parent.get(0).setting.GAMMA;
            setting2.GAMMA = parent.get(1).setting.GAMMA;

        }
        else {
            setting1.GAMMA = parent.get(1).setting.GAMMA;
            setting2.GAMMA = parent.get(0).setting.GAMMA;
        }

        if(r.nextBoolean()) {
            setting1.RECPROB  = parent.get(0).setting.RECPROB ;
            setting2.RECPROB  = parent.get(1).setting.RECPROB ;

        }
        else {
            setting1.RECPROB  = parent.get(1).setting.RECPROB ;
            setting2.RECPROB  = parent.get(0).setting.RECPROB ;
        }

        if(r.nextBoolean()) {
            setting1.POPULATION_SIZE  = parent.get(0).setting.POPULATION_SIZE ;
            setting2.POPULATION_SIZE  = parent.get(1).setting.POPULATION_SIZE ;

        }
        else {
            setting1.POPULATION_SIZE  = parent.get(1).setting.POPULATION_SIZE ;
            setting2.POPULATION_SIZE  = parent.get(0).setting.POPULATION_SIZE ;
        }

        if(r.nextBoolean()) {
            setting1.SIMULATION_DEPTH  = parent.get(0).setting.SIMULATION_DEPTH ;
            setting2.SIMULATION_DEPTH  = parent.get(1).setting.SIMULATION_DEPTH ;

        }
        else {
            setting1.SIMULATION_DEPTH  = parent.get(1).setting.SIMULATION_DEPTH ;
            setting2.SIMULATION_DEPTH  = parent.get(0).setting.SIMULATION_DEPTH ;
        }

        if(r.nextBoolean()) {
            setting1.MUT  = parent.get(0).setting.MUT ;
            setting1.USED_DEFAULT_MUT  = parent.get(0).setting.USED_DEFAULT_MUT ;
            setting2.MUT  = parent.get(1).setting.MUT ;
            setting2.USED_DEFAULT_MUT  = parent.get(1).setting.USED_DEFAULT_MUT ;
        }
        else {
            setting1.MUT  = parent.get(1).setting.MUT ;
            setting1.USED_DEFAULT_MUT  = parent.get(1).setting.USED_DEFAULT_MUT ;
            setting2.MUT  = parent.get(0).setting.MUT ;
            setting2.USED_DEFAULT_MUT  = parent.get(0).setting.USED_DEFAULT_MUT ;
        }

        setting1.refresh();
        setting2.refresh();


        Individual i = new Individual(setting1);
        Individual i2= new Individual(setting2);

        ArrayList<Individual> list_i = new ArrayList<Individual>();

        list_i.add(i);
        list_i.add(i2);
        return list_i;
    }
}
