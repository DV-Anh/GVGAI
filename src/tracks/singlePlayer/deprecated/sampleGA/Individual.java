package tracks.singlePlayer.deprecated.sampleGA;

public class Individual {

    public GAParameterSetting setting;
    public Double fitness = 0.0;

    public Individual(GAParameterSetting setting) {

        this.setting = setting;
        //calculateFitness();

    }

    public Individual() {
        GAParameterSetting setting = new GAParameterSetting();
        this.setting = setting;
        //calculateFitness();
    }

    public  Double getFitness(){
        return fitness;
    }

    public Double calculateFitness() {
        GAFitness f = new GAFitness(this.setting);
        this.fitness = f.getResult();

        //this.fitness = this.setting.MUT * this.setting.RECPROB * this.setting.POPULATION_SIZE;

        return this.fitness;
    }

}
