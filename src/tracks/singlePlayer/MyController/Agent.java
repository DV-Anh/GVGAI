package tracks.singlePlayer.MyController;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tracks.singlePlayer.tools.Heuristics.StateHeuristic;
import tracks.singlePlayer.tools.Heuristics.WinScoreHeuristic;

import java.util.*;

public class Agent extends AbstractPlayer {

    ArrayList<Types.ACTIONS> actions;
    private HashMap<Integer, Types.ACTIONS> actionsMapping;
    private Individual[] pop,nexPop;
    int num_action;
    int popSize=5;
    int depth=3;
    int num_indl=0;// need to debug
    int tournamentSize=3;
    int generateNum=3;
    Random gen;
    WinScoreHeuristic heuristic;
    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        initPop(stateObs);

        for (int j = 0; j < generateNum; j++) {
            nexPop=new Individual[popSize];
            for (int i = 0; i < popSize; i++) {
                Individual individual;
                individual=crossover(tournamentSelection(pop,tournamentSize,gen));
                individual.mutation(80);
                evaluate(individual,heuristic,stateObs,actionsMapping);
                nexPop[i]=individual;
            }
            Arrays.sort(nexPop, new Comparator<Individual>() {
                @Override
                public int compare(Individual o1, Individual o2) {
                    if(o1.value==o2.value) {
                        return 0;
                    }else if(o1.value>o2.value)
                    {return -1;} else return 1;
                }
            });
            pop=mergePop(pop,nexPop);
        }
        Arrays.sort(pop, new Comparator<Individual>() {
            @Override
            public int compare(Individual o1, Individual o2) {
                if(o1.value==o2.value) {
                    return 0;
                }else if(o1.value>o2.value)
                {return -1;} else return 1;
            }
        });
        System.out.println(pop[0].value);
        return actionsMapping.get(pop[0].actions[0]);
    }
    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer)
    {
        actions=stateObs.getAvailableActions();
        heuristic = new WinScoreHeuristic(stateObs);
        this.gen=new Random();
    }

    public Individual[] mergePop(Individual[] pop, Individual[] nexPop)
    {
        // merge population and survivor selection
        Individual[] watingSelection =new Individual[2*popSize];
        Individual[] selectedPop=new Individual[popSize];
        for (int i = 0; i <popSize; i++) {
            watingSelection[i]=pop[i].copy();
        }
        for (int i = popSize; i <2*popSize; i++) {
            watingSelection[i]=nexPop[i-popSize].copy();
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
            selectedPop[i]=watingSelection[i].copy();
        }
        return selectedPop;
    }

    private void initPop(StateObservation stateObs)
    {
        num_action = stateObs.getAvailableActions().size() + 1;
        actionsMapping = new HashMap<>();
        int k = 0;
        for (Types.ACTIONS action : stateObs.getAvailableActions()) {
            actionsMapping.put(k, action);
            k++;
        }
        actionsMapping.put(k, Types.ACTIONS.ACTION_NIL);
        pop=new Individual[popSize];
        nexPop = new Individual[popSize];
        for (int i = 0; i < popSize; i++) {

                pop[i] = new Individual(depth, num_action, gen);
                evaluate(pop[i],heuristic,stateObs,actionsMapping);
                num_indl++; //need to debug
        }
        Arrays.sort(pop, new Comparator<Individual>() {
            @Override
            public int compare(Individual o1, Individual o2) {
                if(o1.value==o2.value) {
                    return 0;
                }else if(o1.value>o2.value)
                {return -1;} else return 1;
            }
        });

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

    public Individual crossover (Individual[] parent) {
        Individual child = new Individual(depth,num_action,gen);
        int p = gen.nextInt(num_action - 3) + 1;
        for ( int i = 0; i < num_action; i++) {
            if (i < p)
                child.actions[i] = parent[0].actions[i];
            else
                child.actions[i] = parent[1].actions[i];
        }
        return child;
    }

    public void evaluate(Individual a,StateHeuristic heuristic, StateObservation state, Map<Integer, Types.ACTIONS> action_mapping) {


        StateObservation st = state.copy();
        int i;
        for (i = 0; i < depth; i++) {
            if (!st.isGameOver()) {
                st.advance(action_mapping.get(a.actions[i]));
            }
        }
        StateObservation first = st.copy();
        double value = heuristic.evaluateState(first);
        a.value = value;
    }

    public void EA()
    {
//        Population pop=new Population();
//        if (!terminate condition: beyond excute time)
//        {
//            pop.tournament();
//            pop.crossover();
//            pop.mutation();
//            pop.eltism();
//            pop=a new pop;
//        }


    }



}
