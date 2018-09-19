package tracks.singlePlayer.ECAssignment2.controllers.Group8Controller;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.*;

public class Agent extends AbstractPlayer {

    ArrayList<Types.ACTIONS> actions;
    private HashMap<Integer, Types.ACTIONS> actionsMapping;
    private Individual[] pop,nexPop;
    int num_action;
    int popSize=4;
    int depth;
    int tournamentSize=2;
    int generateNum;
    Random gen;
   // WinScoreHeuristic heuristic;
    StateObservation stateObservation;
    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        initPop(stateObs);
        for (int j = 0; j < generateNum; j++) {
            nexPop=new Individual[popSize];
            for (int i = 0; i < popSize; ) {
                Individual individual1,individual2;
//                individual=crossover(tournamentSelection(pop,tournamentSize,gen));
                Individual[] parents=tournamentSelection(pop,tournamentSize,gen);
                individual1=orderCrossver(parents[0],parents[1]);
                individual1.mutation(80);
                individual2=orderCrossver(parents[1],parents[0]);
                individual2.mutation(80);
                evaluate(individual1,stateObs,actionsMapping);
                evaluate(individual2,stateObs,actionsMapping);
                nexPop[i]=individual1;
                nexPop[i+1]=individual2;
                i=i+2;
            }
//            Arrays.sort(nexPop, new Comparator<Individual>() {
//                @Override
//                public int compare(Individual o1, Individual o2) {
//                    if(o1.value==o2.value) {
//                        return 0;
//                    }else if(o1.value>o2.value)
//                    {return -1;} else return 1;
//                }
//            });
            pop=mergePop(pop,nexPop);
        }
//        Arrays.sort(pop, new Comparator<Individual>() {
//            @Override
//            public int compare(Individual o1, Individual o2) {
//                if(o1.value==o2.value) {
//                    return 0;
//                }else if(o1.value>o2.value)
//                {return -1;} else return 1;
//            }
//        });
//        System.out.println(pop[0].value);
        return actionsMapping.get(pop[0].actions[0]);
    }
    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer)
    {
        actions=stateObs.getAvailableActions();
//        System.out.println(actions.size());
        if (actions.size()>4)
        {
            generateNum=2;
            depth=3;

        }else {
            generateNum=2;
            depth=5;
        }
     //   heuristic = new WinScoreHeuristic(stateObs);
        this.stateObservation=stateObs;
        this.gen=new Random();
    }

    public Individual[] mergePop(Individual[] pop, Individual[] nexPop)
    {
        // merge population and survivor selection
        Individual[] watingSelection =new Individual[2*popSize];
        Individual[] selectedPop=new Individual[popSize];
        for (int i = 0; i <popSize; i++) {
//            watingSelection[i]=pop[i].copy();
            watingSelection[i]=pop[i];

        }
        for (int i = popSize; i <2*popSize; i++) {
//            watingSelection[i]=nexPop[i-popSize].copy();
            watingSelection[i]=nexPop[i-popSize];
        }
        Arrays.sort(watingSelection, new Comparator<Individual>() {
            @Override
            public int compare(Individual o1, Individual o2) {
                if(o1.value==o2.value) {
                    return 0;
                }else if(o1.value>o2.value)
                {return -1;} else return 1;
            }
        });
        for (int i = 0; i <popSize; i++) {
//            selectedPop[i]=watingSelection[i].copy();
            selectedPop[i]=watingSelection[i];

        }
        return selectedPop;
    }

    private void initPop(StateObservation stateObs)
    {
        num_action = stateObs.getAvailableActions().size();

        actionsMapping = new HashMap<>();
        int k = 0;
        for (Types.ACTIONS action : stateObs.getAvailableActions()) {
            actionsMapping.put(k, action);
            k++;
        }

        pop=new Individual[popSize];
        nexPop = new Individual[popSize];
        for (int i = 0; i < popSize; i++) {

                pop[i] = new Individual(depth, num_action, gen);
                evaluate(pop[i],stateObs,actionsMapping);
//                num_indl++;
        }
//        Arrays.sort(pop, new Comparator<Individual>() {
//            @Override
//            public int compare(Individual o1, Individual o2) {
//                if(o1.value==o2.value) {
//                    return 0;
//                }else if(o1.value>o2.value)
//                {return -1;} else return 1;
//            }
//        });

    }

    public Individual[] tournamentSelection(Individual[] pop, int tournamentSize, Random gen)
    {
        Individual[] parent=new Individual[2];
        for (int i = 0; i <2 ; i++) {
            Individual[] selectedIndl=new Individual[tournamentSize];
            for (int j = 0; j < tournamentSize; j++) {
                selectedIndl[j]=pop[gen.nextInt(popSize)].copy();
            }
            Arrays.sort(selectedIndl, new Comparator<Individual>() {
                @Override
                public int compare(Individual o1, Individual o2) {
                    if(o1.value==o2.value) {
                        return 0;
                    }else if(o1.value>o2.value)
                    {return -1;} else return 1;
                }
            });
            parent[i]=selectedIndl[0];
        }

        return parent;
    }


    public void evaluate(Individual a, StateObservation state, Map<Integer, Types.ACTIONS> action_mapping) {


        StateObservation st = state.copy();
        double currentScore=st.getGameScore();
        int i;
        for (i = 0; i < depth; i++) {
            if (!st.isGameOver()) {
                st.advance(action_mapping.get(a.actions[i%num_action]));
            }
        }
//        StateObservation first = st.copy();
//        double value = heuristic.evaluateState(first);
        // maybe need to take account in Time
        a.value = st.getGameScore()-currentScore;
    }



//    public Individual crossover (Individual[] parent) {
//        Individual child = new Individual(depth,num_action,gen);
////        int p = gen.nextInt(num_action - 3) + 1;
//        int p = gen.nextInt(num_action - 2)+1 ;
//        for ( int i = 0; i < num_action; i++) {
//            if (i < p)
//                child.actions[i] = parent[0].actions[i];
//            else
//                child.actions[i] = parent[1].actions[i];
//        }
//        return child;
//    }

    public Individual orderCrossver (Individual parent0,Individual parent1)
    {
//        System.out.println("parent0");
//        for (int i:parent0.actions) {
//            System.out.print(i+" ");
//
//        }
//        System.out.println("parent1");
//        for (int i:parent1.actions) {
//            System.out.print(i+" ");
//
//        }
        //child's actions are all -1
        Individual child=new Individual(depth,num_action,gen,-1);
        int ran1=gen.nextInt(num_action);
        int ran2=gen.nextInt(num_action);
//        System.out.println("num_action "+num_action);
        int index1=Math.min(ran1,ran2);
        int index2=Math.max(ran1,ran2);
//        System.out.println(index1+" "+index2);
        int[] parent0Act=parent0.actions.clone();
        int[] parent1Act=parent1.actions.clone();
        for(int i=index1;i<=index2;i++)
        {
            child.actions[i]=parent0Act[i];
        }

        for (int i = index1; i <=index2; i++) {
            for (int j=0;j<num_action;j++)
            {
                if(parent1Act[j]==child.actions[i])
                {
                    parent1Act[j]=-1;
                    break;
                }
            }

        }
        for (int i = 0; i <num_action ; i++) {
            for (int j = 0; j <num_action ; j++) {
                if(child.actions[i]==-1&&parent1Act[j]>-1)
                {
                    child.actions[i]=parent1Act[j];
                    parent1Act[j]=-2;
                    break;
                }

            }
        }
//        System.out.println("child");
//        for (int i:child.actions) {
//            System.out.print(i+" ");
//
//        }
//        evaluate(child,stateObservation,actionsMapping);
        return child;
    }

}
