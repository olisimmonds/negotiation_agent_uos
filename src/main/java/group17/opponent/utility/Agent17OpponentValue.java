package group17.opponent.utility;

/**
 * Models the opponent's preference with respect to a particular value.
 * @author Martin Ingram
 */
public class Agent17OpponentValue extends UtilityOpponentValue {
	
	public Agent17OpponentValue(final String name) {
		super(name);
	}

	@Override
	public double calculatePreferenceValue(final int rank, final int numValues, final int priorBids) {
		this.preferenceValue = this.preferenceFormula(this.count, priorBids);
		return this.preferenceValue;
	}
	
	@Override
	public double calculateRecentPreferenceValue(int rank, int numValues, int bidWindow) {
		this.recentPreferenceValue = this.preferenceFormula(this.recentCount, bidWindow);
		return this.recentPreferenceValue;
	}
	
	/**
	 * Calculate the preference value for this opponent value, based linearly on the count.
	 * @param rank Relative rank (1 to n) of this opponent value
	 * @param numValues Number of values for the associated issue (n)
	 * @param priorBids The number of prior bids.
	 * @return The calculated preference value
	 */
	private double preferenceFormula(final int count, final int priorBids) {
		final double numerator = count;
		final double denominator = priorBids;
		return numerator / denominator;
	}
	
	@Override
	public double calculateWeight(final int priorBids) {
		this.weight = this.weightFormula(this.count, priorBids);
		return this.weight;
	}
	
	@Override
	public double calculateRecentWeight(int bidWindow) {
		this.recentWeight = this.weightFormula(this.recentCount, bidWindow);
		return this.recentWeight;
	}
	
	/**
	 * Calculate the current unnormalised value weight, based on the "Jonny Black" algorithm in Lab 3.
	 * @param priorBids Number of prior bids.
	 * @param count The count to use (total or recent)
	 * @return Unnormalised value weight
	 */
	private double weightFormula(final int count, final int priorBids) {
		final double numerator = count * count;
		final double denominator = priorBids * priorBids;
		return numerator / denominator;
	}
}
