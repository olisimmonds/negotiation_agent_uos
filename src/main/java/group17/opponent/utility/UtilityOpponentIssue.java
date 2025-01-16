/**
 * 
 */
package group17.opponent.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import group17.domain.DomainTranslator;
import group17.opponent.OpponentIssue;
import group17.opponent.OpponentValue;

/**
 * Generic {@link OpponentIssue} that handles preference values, weights and utilities.
 */
public abstract class UtilityOpponentIssue<UOV extends UtilityOpponentValue> extends OpponentIssue {
	
	protected double normalizedWeight = 0.0;
	protected double normalizedRecentWeight = 0.0;
	protected double unnormalizedWeight = 0.0;
	protected double unnormalizedRecentWeight = 0.0;
	
	protected Map<String, UOV> values;
		
	protected UtilityOpponentIssue(int issueNo, String issueName, String[] valueNames) {
		super(issueNo, issueName, valueNames);
	}
	
	public UtilityOpponentIssue(final Issue issue) {
		super(issue);
	}
	
	// These abstract methods define the algorithmic variants of each type of Opponent Model.
	
	public abstract void updatePreferenceValues(final int priorBids, final int bidWindow);

	protected abstract UOV createUtilityOpponentValue(final String name);
	
	public abstract double calculateUnnormalizedWeight(final int priorBids);
	
	public abstract double calculateUnnormalizedRecentWeight(final int priorBids);
	
    // All of these attributes are read only.
		
	public double getNormalizedWeight() {
		return this.normalizedWeight;
	}
	
	public double getNormalizedRecentWeight() {
		return normalizedRecentWeight;
	}
	
	public double getUnnormalizedWeight() {
		return this.unnormalizedWeight;
	}
	
	public double getUnnormalizedRecentWeight() {
		return unnormalizedRecentWeight;
	}

	public UOV getValue(final String valueName) {
		return this.values.get(valueName);
	}
	
	/**
	 * Reduce the recent count for a specified value, effectively removing an old bid.
	 * @param valueName Value name
	 */
	public void decrementValueRecentCount(final String valueName) {
		final UOV value = this.values.get(valueName);
		if (value != null) {
			value.decrementRecentCount();;
		} else {
			System.err.println("Attempting to deccrement value recent count, but could not find value with name " + valueName);
		}
	}
	
	/**
	 * Increment both the total and recent value counts for a specified value.
	 * @param valueName Value name
	 */
	public void incrementValueCount(final String valueName) {
		final UOV value = this.values.get(valueName);
		if (value != null) {
			value.incrementCount();
		} else {
			System.err.println("Attempting to increment value count, but could not find value with name " + valueName);
		}
	}
	
	/**
	 * (Re)calculate the overall normalized weight of this issue.
	 * @param totalUnnormalizedWeight Total unnormalized weight across all issues.
	 * @return Normalized overall weight of this issue
	 */
	public double calculateNormalizedWeight(final double totalUnnormalizedWeight) {
		this.normalizedWeight = this.unnormalizedWeight / totalUnnormalizedWeight;
		return this.normalizedWeight;
	}
	
	/**
	 * (Re)calculate the normalized recent weight of this issue.
	 * @param totalUnnormalizedWeight Total unnormalized recent weight across all issues.
	 * @return Normalized recent weight of this issue
	 */
	public double calculateNormalizedRecentWeight(final double totalUnnormalizedRecentWeight) {
		this.normalizedRecentWeight = this.unnormalizedRecentWeight / totalUnnormalizedRecentWeight;
		return this.normalizedRecentWeight;
	}
		
	@Override
	protected int createOpponentValues(final Issue issue) {
		this.values = new HashMap<>();
		if (issue instanceof IssueDiscrete) {
			final IssueDiscrete discrete = (IssueDiscrete) issue;
			for (final Value value : discrete.getValues()) {
				final String name = DomainTranslator.valueName(value);
				final UOV opponentValue = this.createUtilityOpponentValue(name);
				this.values.put(name, opponentValue);
			}
		}
		return this.values.size();
	}
	
	@Override
	protected int createOpponentValues(final String... valueNames) {
		this.values = new HashMap<>();
		for (final String valueName : valueNames) {
			UOV value = this.createUtilityOpponentValue(valueName);
			this.values.put(valueName, value);
		}
		return this.values.size();
	}
	
	@Override
	public Collection<OpponentValue> getValues() {
		final List<OpponentValue> opponentValues = new ArrayList<>();
		opponentValues.addAll(this.values.values());
		return opponentValues;
	}
	
	@Override
	public String toString() {
		return new StringBuilder(super.toString())
			.append(", normalizedWeight=")
			.append(this.normalizedWeight)
			.append(", normalizedRecentWeight=")
			.append(this.normalizedWeight)
			.append(", unnormalizedWeight=")
			.append(this.unnormalizedWeight)
			.append(", unnormalizedRecentWeight=")
			.append(this.unnormalizedRecentWeight)
			.toString();
	}
}
