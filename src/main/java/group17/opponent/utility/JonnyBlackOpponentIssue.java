package group17.opponent.utility;

import java.util.List;

import genius.core.issue.Issue;

/**
 * Models the opponent's preference with respect to a particular issue.
 * @author Martin Ingram
 */
public class JonnyBlackOpponentIssue extends UtilityOpponentIssue<JonnyBlackOpponentValue> {
		
	/**
	 * Construct an Opponent Issue based on an {@link Issue} in the current domain.
	 * @param issue Issue
	 */
	public JonnyBlackOpponentIssue(final Issue issue) {
		super(issue);
	}
	
	protected JonnyBlackOpponentIssue(final int issueNo, final String issueName, final String... valueNames) {
		super(issueNo, issueName, valueNames);
	}

	/**
	 * Calculate total unnormalised weight using the "Jonny Black" algorithm from Lab 3.
	 * @param Number of prior bids - theoretically different to the total of the value counts.
	 * @return Unnormalized weight
	 */
	public double calculateUnnormalizedWeight(final int priorBids) {
		this.unnormalizedWeight = 0.0;
		for (JonnyBlackOpponentValue value : this.values.values()) {
			this.unnormalizedWeight += value.calculateWeight(priorBids);
		}
		return this.unnormalizedWeight;
	}
	
	/**
	 * Calculate unnormalised recent weight using the "Jonny Black" algorithm from Lab 3.
	 * @param Number of prior bids - theoretically different to the total of the value counts.
	 * @return Unnormalized weight
	 */
	public double calculateUnnormalizedRecentWeight(final int priorBids) {
		this.unnormalizedRecentWeight = 0.0;
		for (JonnyBlackOpponentValue value : this.values.values()) {
			this.unnormalizedRecentWeight += value.calculateRecentWeight(priorBids);
		}
		return this.unnormalizedRecentWeight;
	}

	@Override
	protected JonnyBlackOpponentValue createUtilityOpponentValue(String name) {
		return new JonnyBlackOpponentValue(name);
	}
	
	/**
	 * Update the relative preference values of each opponent value for this issue, based
	 * on the "Jonny Black" algorithm from Lab 3.
	 * @param priorBids The total number of offers received
	 * @param bidWindow The number of recent bids to consider
	 */
	public void updatePreferenceValues(final int priorBids, final int bidWindow) {
		List<JonnyBlackOpponentValue> sortedValues = this.sortValuesByCount();
		int rank = 1;
		for (final JonnyBlackOpponentValue value : sortedValues) {
			value.calculatePreferenceValue(rank++, this.numValues, priorBids);
		}
		sortedValues = this.sortRecentValuesByCount();
		rank = 1;
		for (final JonnyBlackOpponentValue value : sortedValues) {
			value.calculateRecentPreferenceValue(rank++, this.numValues, bidWindow);
		}
	}
	
	/**
	 * Get a list of values sorted descending by count.
	 * @return Sorted list of values
	 */
	protected List<JonnyBlackOpponentValue> sortValuesByCount() {
		return this.values.values()
			.stream()
            .sorted(($1, $2) -> Integer.compare($2.getCount(), $1.getCount()))
            	.toList();
	}
	
	/**
	 * Get a list of values sorted descending by recent count.
	 * @return Sorted list of values
	 */
	protected List<JonnyBlackOpponentValue> sortRecentValuesByCount() {
		return this.values.values()
			.stream()
            .sorted(($1, $2) -> Integer.compare($2.getRecentCount(), $1.getRecentCount()))
            	.toList();
	}
}
