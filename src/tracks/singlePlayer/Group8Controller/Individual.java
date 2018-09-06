package tracks.singlePlayer.Group8Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Individual {
    int[] actions;
    int depth;
    int num_action;
    Random gen;
    double value;
    //depth: predict num of step  num_action: represent acton as 0,1,2,3...num_action-1
    public Individual(int depth, int num_action, Random gen)
    {
        actions=new int[num_action];
        for (int i = 0; i <num_action ; i++) {
            actions[i]=gen.nextInt(num_action);
        }
        this.depth=depth;
        this.num_action=num_action;
        this.gen=gen;
    }

    public Individual(Individual individual)
    // Clone an individual
    {
        actions=individual.actions.clone();
        num_action=individual.num_action;
       // value=individual.evaluate();
    }
    public Individual(int[] actions,int depth,int num_action,Random gen)
    {
        this.actions=actions;
        this.num_action=num_action;
        this.depth=depth;
        this.gen=gen;
    }
    public Individual(int depth,int num_action,Random gen, int type)
    {
        if(type==-1)
        {
            actions=new int[num_action];
            for (int i = 0; i <num_action ; i++) {
                actions[i]=-1;
            }
            this.num_action=num_action;
            this.depth=depth;
            this.gen=gen;
        }

    }
    public Individual OrdercrossOver(ArrayList<Individual> parent) {
        int size=parent.get(0).size();
        int index1=gen.nextInt(size);
        int index2=index1;
        while (index2==index1) index2=gen.nextInt(size);
        if (index1>index2) {
            int temp=index1;
            index1=index2;
            index2=temp;
        }
        Individual child = new Individual(depth,num_action,gen);

        for (int index=index1; index<=index2; index++)
            child.actions[index] = parent.get(0).actions[index];

        int pointer=index2+1;
        for (int index=index2+1; index<index2+size+1; index++) {
            if (!Arrays.asList(child.actions).contains(parent.get(1).actions[index%size])) {
                child.actions[pointer%size] = parent.get(1).actions[index%size];
                pointer++;
            }
        }
        return child;
    }



    public int size()
    {
        return num_action;
    }

    public void mutation(int percent)
    {
        for (int i=0; i<size(); i++) {

            // Only mutate the required percent of the population
            if (percent<=gen.nextInt(100)) continue;

            // Chooses the index at one end of the inversion
            // and the distance with wrap-around to the other end of the inversion
            int size=size();
            int position=gen.nextInt(size);
            int distance=1+gen.nextInt(size-2);
            invert( position, distance);
        }
    }
    public void invert( int position, int distance)
    // Inverts by swapping with wrap-around
    {
        int temp;
        for (int j=0; j<(distance+1)>>1; j++) {
            temp=actions[(position+j)%size()];
            actions[(position+j)%size()]=actions[(position+distance-j)%size()];
            actions[(position+distance-j)%size()]=temp;
        }
    }
    public  void printIndl(Individual individual)
    {
        for (int i : individual.actions)
        {
            System.out.print(i+" ");
        }
        System.out.println();

    }

    public Individual copy () {
        Individual a = new Individual(actions,this.depth,this.num_action, this.gen);
        a.value = this.value;
        return a;
    }



//    public static void main (String arg[])
//    {
//        Random gen=new Random();
//        ArrayList<Individual> pop=new ArrayList<Individual>();
//        for (int i = 0; i < 2; i++) {
//            Individual tmp=new Individual(10,10,gen);
//            pop.add(tmp);
////            tmp.printIndl(tmp);
//            tmp.mutation(80);
//            tmp.printIndl(tmp);
//            System.out.println(tmp.value);
//        }
//    }
}
