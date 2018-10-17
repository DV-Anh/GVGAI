package tracks.singlePlayer.ECAssignment3.controllers.hyperRHEA;

import java.util.*;

public class Individual {
    public int num_actions;
    public double value=0;
    private Random randomGenerator = new Random();
    public ArrayList<Integer> actions = new ArrayList<>();

    public Individual(int num){
        this.num_actions=num;
    }

    // Create an individual that copies a parent
    // an either remove or add actions to get to the right size
    // then apply the set number of mutations
    public Individual(Individual parent, int size, int mutations){
        this.num_actions=parent.num_actions;
        if (parent.actions.size()>1) {
            this.actions=new ArrayList<>(parent.actions);
            while(this.actions.size()<size) this.actions.add(randomGenerator.nextInt(num_actions));
            while(this.actions.size()>size) this.actions.remove(randomGenerator.nextInt(this.actions.size()));
            for (int i=0; mutations>0 && i<size; i++){
                if (randomGenerator.nextDouble() < (double) mutations/(size-i)){
                    int new_action=this.actions.get(i);
                    while (new_action==this.actions.get(i)) new_action=randomGenerator.nextInt(num_actions);
                    this.actions.set(i,new_action);
                    mutations--;
                }
            }
        }
        else while (this.actions.size()<size) this.actions.add(randomGenerator.nextInt(num_actions));
    }

    // Remove the first instance of an action if it exists, else remove the first action
    // If individual no longer has any moves, add a random move
    public void transshift(int action){
        if (this.actions.size()>0) this.actions.remove(Math.max(this.actions.indexOf(action),0));
        if (this.actions.size()==0) this.actions.add(randomGenerator.nextInt(num_actions));
    }

    // Return first move an individual has or nil-action if no moves
    public int nextMove(){
        return (this.actions.size()>0)? this.actions.get(0) : 0;
    }
}
