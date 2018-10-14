package tracks.singlePlayer.ECAssignment3.controllers.betterRHEA;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.AbstractGenericSolution;

import java.util.HashMap;

public class Muliti_obj_Solution extends AbstractGenericSolution<Double,DoubleProblem> implements DoubleSolution {

    public Muliti_obj_Solution(DoubleProblem problem) {
        super(problem);
        initializeDoubleVariables();
        initializeObjectiveValues();
         }
    public Muliti_obj_Solution(Muliti_obj_Solution solution) {
        super(solution.problem);
        for (int i = 0; i < problem.getNumberOfVariables(); i++) {
            setVariableValue(i,solution.getVariableValue(i));
        }
        for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
            setObjective(i,solution.getObjective(i));
        }
        attributes=new HashMap<Object, Object>(solution.attributes);
    }



    @Override
    public String getVariableValueString(int index) {
        return getVariableValue(index).toString();
    }

    @Override
    public Muliti_obj_Solution copy() {
        return new Muliti_obj_Solution(this);
    }

    @Override
    public Double getLowerBound(int index) {
        return problem.getLowerBound(index);
    }

    @Override
    public Double getUpperBound(int index) {
        return problem.getUpperBound(index);
    }

    private void initializeDoubleVariables() {
        for (int i = 0 ; i < problem.getNumberOfVariables(); i++)
        {
            Double value = randomGenerator.nextDouble(getLowerBound(i), getUpperBound(i)) ;
            setVariableValue(i, value) ;
        }
    }




}
