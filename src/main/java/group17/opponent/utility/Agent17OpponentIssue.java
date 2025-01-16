package group17.opponent.utility;

import genius.core.issue.Issue;

/**
 * Models the opponent's preference with respect to a particular issue.
 * @author Martin Ingram
 */
public class Agent17OpponentIssue extends UtilityOpponentIssue<Agent17OpponentValue> {
		
	/**
	 * Construct an Opponent Issue based on an {@link Issue} in the current domain.
	 * @param issue Issue
	 */
	public Agent17OpponentIssue(final Issue issue) {
		super(issue);
	}
	
	protected Agent17OpponentIssue(final int issueNo, final String issueName, final String... valueNames) {
		super(issueNo, issueName, valueNames);
	}

	/**
	 * Calculate unnormalised total weight using the "Jonny Black" algorithm from Lab 3.
	 * @param Number of prior bids - theoretically different to the total of the value counts.
	 * @return Unnormalized weight
	 */
	public double calculateUnnormalizedWeight(final int priorBids) {
		this.unnormalizedWeight = 0.0;
		for (Agent17OpponentValue value : this.values.values()) {
			this.unnormalizedWeight += value.calculateWeight(priorBids);
		}
		return this.unnormalizedWeight;
	}
	
	/**
	 * Calculate unnormalised recent weight using the "Jonny Black" algorithm from Lab 3.
	 * @param Number of prior bids - theoretically different to the total of the value counts.
	 * @return Recent unnormalized weight
	 */
	public double calculateUnnormalizedRecentWeight(final int priorBids) {
		this.unnormalizedRecentWeight = 0.0;
		for (Agent17OpponentValue value : this.values.values()) {
			this.unnormalizedRecentWeight += value.calculateRecentWeight(priorBids);
		}
		return this.unnormalizedRecentWeight;
	}

	@Override
	protected Agent17OpponentValue createUtilityOpponentValue(String name) {
		return new Agent17OpponentValue(name);
	}
	
	/**
	 * Update the relative preference values of each opponent value for this issue, based
	 * on the "Jonny Black" algorithm from Lab 3.
	 */
	public void updatePreferenceValues(final int priorBids, final int bidWindow) {
		for (final Agent17OpponentValue value : this.values.values()) {
			value.calculatePreferenceValue(0, this.numValues, priorBids);
			value.calculateRecentPreferenceValue(0, this.numValues, bidWindow);
		}
	}
}
