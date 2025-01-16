package group17.user;

import agents.org.apache.commons.math.optimization.GoalType;
import agents.org.apache.commons.math.optimization.OptimizationException;
import agents.org.apache.commons.math.optimization.RealPointValuePair;
import agents.org.apache.commons.math.optimization.linear.*;
import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import genius.core.uncertainty.UserModel;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Implementation based on Automated Negotiations Under User Preference Uncertainty: A linear Programming Approach
 * https://link.springer.com/chapter/10.1007/978-3-030-17294-7_9
 */
public class LPSolver {

    private final UserModel userModel;
    private final Domain domain;
    private final List<Value> values;
    private final Map<Value, Integer> valueMapper;

    private final int totalSlackCoefficients;
    private final int totalValueCoefficients;
    private final int totalIssueCoefficients;


    public LPSolver(UserModel userModel) {
        this.userModel = userModel;
        this.domain = userModel.getDomain();
        this.totalSlackCoefficients = this.userModel.getBidRanking().getBidOrder().size()-1;
        this.totalIssueCoefficients = this.domain.getIssues().size();

        this.values = new ArrayList<>();
        this.valueMapper = new HashMap<>();
        int valCount = 0;
        for (Issue issue : this.domain.getIssues()) {
            for (Value value : ((IssueDiscrete) issue).getValues()) {
                this.values.add(value);
                valueMapper.put(value,valCount++);
            }
        }
        this.totalValueCoefficients = valCount;
    }

    public LPSolverResult solve() {
        AbstractLinearOptimizer optimizer = new SimplexSolver();

        Map<Value, Double> solvedValues = solveForValues(optimizer);
        if (solvedValues.isEmpty()) return new LPSolverResult(null, null, true);

        Map<Issue, Double> solvedIssues = solveForIssues(optimizer, solvedValues);
        if (solvedIssues.isEmpty()) return new LPSolverResult(solvedValues, null, true);

        return new LPSolverResult(solvedValues, solvedIssues, false);
    }

    private Map<Value, Double> solveForValues(AbstractLinearOptimizer optimizer) {
        LinearObjectiveFunction objectiveFunction = solveForValuesObj();
        List<LinearConstraint> constraints = new ArrayList<>();

        double[][] pairwiseComparisons = generateBidRankingPairwiseValueComparisons();

        for (int i = 0; i < this.totalSlackCoefficients; i++) {
            double[] constraintRow = buildCoefficientRow(this.valueCoefficientsWithSlack(), totalValueCoefficients+i);
            constraints.add(new LinearConstraint(constraintRow, Relationship.GEQ, 0));
        }

        for (int i = 0; i < this.totalValueCoefficients; i++) {
            double[] constraintRow = buildCoefficientRow(this.valueCoefficientsWithSlack(), i);
            constraints.add(new LinearConstraint(constraintRow, Relationship.GEQ, 0));
        }

        for (int i = 0; i < this.totalSlackCoefficients; i++) {
            double[] constraintRow = buildCoefficientRow(this.valueCoefficientsWithSlack(), totalValueCoefficients+i);
            double[] cmp = pairwiseComparisons[i];
            System.arraycopy(cmp, 0, constraintRow, 0, totalValueCoefficients);
            constraints.add(new LinearConstraint(constraintRow, Relationship.GEQ, 0));
        }

        double[] maxBidConstraintRow = buildBidConstraintRow(this.userModel.getBidRanking().getMaximalBid());
        constraints.add(new LinearConstraint(maxBidConstraintRow, Relationship.EQ, userModel.getBidRanking().getHighUtility()));

        double[] minBidConstraintRow = buildBidConstraintRow(this.userModel.getBidRanking().getMinimalBid());
        constraints.add(new LinearConstraint(minBidConstraintRow, Relationship.EQ, userModel.getBidRanking().getLowUtility()));

        try {
            RealPointValuePair result = optimizer.optimize(objectiveFunction, constraints, GoalType.MINIMIZE, true);

            HashMap<Value, Double> solvedValues = new HashMap<>();
            for (int i = 0; i < values.size(); i++) {
                solvedValues.put(values.get(i), result.getPoint()[i]);
            }

            return solvedValues;
        } catch (OptimizationException e) {
            e.printStackTrace();
        }

        return new HashMap<>();
    }

    private Map<Issue, Double> solveForIssues(AbstractLinearOptimizer optimizer, Map<Value, Double> solvedValues) {
        LinearObjectiveFunction objectiveFunction = solveForIssuesObj();
        List<LinearConstraint> constraints = new ArrayList<>();

        double[][] pairwiseComparisons = generateBidRankingPairwiseIssueComparisons(solvedValues);

        double[] sumCoefficients = buildCoefficientRow(this.totalIssueCoefficients, getRangeBetween(0, totalIssueCoefficients));
        constraints.add(new LinearConstraint(sumCoefficients, Relationship.EQ, 1));

        for (int i = 0; i < this.totalSlackCoefficients; i++) {
            double[] constraintRow = buildCoefficientRow(this.issueCoefficientsWithSlack(), totalIssueCoefficients+i);
            constraints.add(new LinearConstraint(constraintRow, Relationship.GEQ, 0));
        }

        for (int i = 0; i < this.totalIssueCoefficients; i++) {
            double[] constraintRow = buildCoefficientRow(this.issueCoefficientsWithSlack(), i);
            constraints.add(new LinearConstraint(constraintRow, Relationship.GEQ, 0));
        }

        for (int i = 0; i < this.totalSlackCoefficients; i++) {
            double[] constraintRow = buildCoefficientRow(this.issueCoefficientsWithSlack(), totalIssueCoefficients+i);
            double[] cmp = pairwiseComparisons[i];
            System.arraycopy(cmp, 0, constraintRow, 0, totalIssueCoefficients);
            constraints.add(new LinearConstraint(constraintRow, Relationship.GEQ, 0));
        }

        try {
            RealPointValuePair result = optimizer.optimize(objectiveFunction, constraints, GoalType.MINIMIZE, true);

            HashMap<Issue, Double> solvedIssues = new HashMap<>();
            for (Issue issue : this.domain.getIssues()) {
                solvedIssues.put(issue, result.getPoint()[issue.getNumber()-1]);
            }

            return solvedIssues;
        } catch (OptimizationException e) {
            e.printStackTrace();
        }

        return new HashMap<>();
    }

    private LinearObjectiveFunction solveForValuesObj() {
        double[] coefficients = buildCoefficientRow(this.valueCoefficientsWithSlack(),
                                                    getRangeBetween(totalValueCoefficients, totalSlackCoefficients));

        return new LinearObjectiveFunction(coefficients, 0);
    }

    private LinearObjectiveFunction solveForIssuesObj() {
        double[] coefficients = buildCoefficientRow(this.valueCoefficientsWithSlack(),
                                                    getRangeBetween(totalValueCoefficients, totalSlackCoefficients));

        return new LinearObjectiveFunction(coefficients, 0);
    }

    private double[][] generateBidRankingPairwiseValueComparisons() {
        List<Bid> bids = this.userModel.getBidRanking().getBidOrder();
        double[][] comparisons =
                new double[bids.size()-1][this.totalValueCoefficients];

        // for N bids, then will be N-1 comparisons
        for (int i = bids.size()-1, j = 0; i > 0; i--, j++) {
            double[] bid1 = buildBidConstraintRow(bids.get(i));
            double[] bid2 = buildBidConstraintRow(bids.get(i-1));

            comparisons[j] = new double[this.totalValueCoefficients];
            Arrays.setAll(comparisons[j], idx -> bid1[idx] - bid2[idx]);
        }

        return comparisons;
    }

    private double[][] generateBidRankingPairwiseIssueComparisons(Map<Value, Double> solvedValues) {
        List<Bid> bids = this.userModel.getBidRanking().getBidOrder();
        double[][] comparisons =
                new double[bids.size()-1][this.totalIssueCoefficients];

        for (int i = bids.size()-1, j = 0; i > 0; i--, j++) {
            Bid cur = bids.get(i);
            Bid prev = bids.get(i-1);

            for (Issue issue : cur.getIssues()) {
                comparisons[j][issue.getNumber()-1] = solvedValues.get(cur.getValue(issue.getNumber())) -
                                                      solvedValues.get(prev.getValue(issue.getNumber()));
            }
        }

        return comparisons;
    }

    private double[] buildBidConstraintRow(Bid bid) {
        double[] coefficients = new double[this.totalValueCoefficients];
        for (Issue issue : bid.getIssues()) {
            coefficients[valueMapper.get(bid.getValue(issue))] = 1;
        }
        return coefficients;
    }


    private double[] buildCoefficientRow(int size, List<Integer> toggledIndices) {
        double[] row = new double[size];
        for (int idx : toggledIndices) row[idx] = 1;
        return row;
    }

    private double[] buildCoefficientRow(int size, int... toggledIndices) {
        double[] row = new double[size];
        for (int idx : toggledIndices) row[idx] = 1;
        return row;
    }

    private int valueCoefficientsWithSlack() {
        return this.totalValueCoefficients + this.totalSlackCoefficients;
    }

    private int issueCoefficientsWithSlack() {
        return this.totalIssueCoefficients + this.totalSlackCoefficients;
    }

    private List<Integer> getRangeBetween(int start, int end) {
        return IntStream.range(start, end).boxed().collect(Collectors.toList());
    }

    public record LPSolverResult (Map<Value, Double> solvedValues, Map<Issue, Double> solvedIssues, boolean failed) {}
}
