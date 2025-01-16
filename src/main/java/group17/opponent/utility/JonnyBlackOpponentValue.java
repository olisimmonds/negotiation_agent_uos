package group17.opponent.utility;

/**
 * Models the opponent's preference with respect to a particular value.
 * @author Martin Ingram
 */
public class JonnyBlackOpponentValue extends UtilityOpponentValue {
	
	public JonnyBlackOpponentValue(final String name) {
		super(name);
	}

	@Override
	public double calculatePreferenceValue(final int rank, final int numValues, final int priorBids) {
		this.preferenceValue =  this.preferenceFormula(rank, numValues, priorBids);
		return this.preferenceValue;
	}
	
	@Override
	public double calculateRecentPreferenceValue(int rank, int numValues, int bidWindow) {
		this.recentPreferenceValue = this.preferenceFormula(rank, numValues, bidWindow);
		return this.recentPreferenceValue;
	}
	
	/**
	 * Calculate a preference value for this opponent value, based on the "Jonny Black" algorithm in Lab 3.
	 * @param rank Relative rank (1 to n) of this opponent value
	 * @param numValues Number of values for the associated issue (n)
	 * @param priorBids The number of prior bids.
	 * @return The calculated preference value
	 */
	private double preferenceFormula(final int rank, final int numValues, final int priorBids) {
		final double numerator = (numValues - rank + 1);
		final double denominator = numValues;
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
	 * @param count The count to use (recent or total)
	 * @param priorBids Number of prior bids.
	 * @return Unnormalised value weight
	 */
	private double weightFormula(final int count, final int numBids) {
		final double numerator = count * count;
		final double denominator = numBids * numBids;
		return numerator / denominator;
	}
}
