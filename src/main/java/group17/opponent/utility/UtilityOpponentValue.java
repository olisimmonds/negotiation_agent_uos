/**
 * 
 */
package group17.opponent.utility;

import group17.opponent.OpponentValue;

/**
 * Generic {@link OpponentValue} that handles weights, preference values and utilities.
 */
public abstract class UtilityOpponentValue extends OpponentValue {
	
	protected double preferenceValue = 0.0;
	protected int recentCount = 0;
	protected double recentPreferenceValue = 0.0;
	protected double recentWeight = 0.0;
	protected double weight = 0.0;

	public UtilityOpponentValue(String name) {
		super(name);
	}
	
	public double getPreferenceValue() {
		return this.preferenceValue;
	}
	
	public int getRecentCount() {
		return recentCount;
	}

	public double getRecentPreferenceValue() {
		return recentPreferenceValue;
	}

	public double getRecentWeight() {
		return recentWeight;
	}

	public double getWeight() {
		return this.weight;
	}
	
	public void decrementRecentCount() {
		this.recentCount--;
	}
	
	@Override
	public void incrementCount() {
		super.incrementCount();
		this.recentCount++;
	}
	
	/**
	 * Calculate the preference value for this opponent value.
	 * @param rank Relative rank (1 to n) of this opponent value
	 * @param numValues Number of values for the associated issue (n)
	 * @param priorBids The number of offers recieved
	 * @return The calculated preference value
	 */
	public abstract double calculatePreferenceValue(final int rank, final int numValues, final int priorBids);
	
	/**
	 * Calculate the recent preference value for this opponent value.
	 * @param rank Relative rank (1 to n) of this opponent value
	 * @param numValues Number of values for the associated issue (n)
	 * @param bidWindow The number of recent offers to consider
	 * @return The calculated preference value
	 */
	public abstract double calculateRecentPreferenceValue(final int rank, final int numValues, final int bidWindow);
	
	/**
	 * Calculate the current unnormalised value weight, based on the "Jonny Black" algorithm in Lab 3.
	 * @param priorBids Number of prior bids.
	 * @return Unnormalised value weight
	 */
	public abstract double calculateWeight(final int priorBids);
	
	/**
	 * Calculate the recent unnormalised value weight, based on the "Jonny Black" algorithm in Lab 3.
	 * @param bidWindow The number of recent bids to consider.
	 * @return Unnormalised value weight
	 */
	public abstract double calculateRecentWeight(final int bidWindow);

	@Override
	public String toString() {
		return new StringBuilder(super.toString())
			.append(", preferenceValue=")
			.append(this.preferenceValue)
			.append(", weight=")
			.append(this.weight)
			.append(", recentCount=")
			.append(this.recentCount)
			.append(", recentPreferenceValue=")
			.append(this.preferenceValue)
			.append(", recentWeight=")
			.append(this.weight)
			.toString();
	}
}
