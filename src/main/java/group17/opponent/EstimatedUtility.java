package group17.opponent;

import java.math.BigDecimal;
import java.math.MathContext;

public class EstimatedUtility {
	
	private static final MathContext CONTEXT = new MathContext(4);
	
	public static final double NO_UTILITY = 0.0;
	public static final double NO_CONFIDENCE = 0.0;
	public static final EstimatedUtility NO_MEANINGFUL_ESTIMATE = new EstimatedUtility(NO_UTILITY, NO_CONFIDENCE);
	
	private final double utility;
	private final double confidenceLevel;
	
	/**
	 * Standard constructor.
	 * @param utility Estimated utility value
	 * @param confidenceLevel Estimated confidence level
	 */
	public EstimatedUtility(final double utility, final double confidenceLevel) {
		this.utility = utility;
		this.confidenceLevel = confidenceLevel;
	}

	/**
	 * Get the estimated utility value
	 * @return Estimated utility
	 */
	public double getUtility() {
		return this.utility;
	}
	
	/**
	 * Get the estimated utility value as a decimal rounded to four places.
	 * This is useful for unit testing.
	 * @return Estimated utility
	 */
	public BigDecimal getUtilityAsDecimal() {
		return new BigDecimal(this.utility, CONTEXT);
	}

	/**
	 * Get the confidence level associated with this estimate.
	 * @return Confidence level
	 */
	public double getConfidenceLevel() {
		return this.confidenceLevel;
	}
	
	/**
	 * Get the confidence level associated with this estimate.
	 * @return Confidence level
	 */
	public BigDecimal getConfidenceLevelAsDecimal() {
		return new BigDecimal(this.confidenceLevel, CONTEXT);
	}
	
	/**
	 * Do we have sufficient data in the model to make this estimate meaningful?
	 * @return true = yes we do
	 */
	public boolean isMeaningful() {
		return !this.isNotMeaningful();
	}
	
	/**
	 * Do we have sufficient data in the model to make this estimate meaningful?
	 * @return true = no we don't
	 */
	public boolean isNotMeaningful() {
		return this.equals(NO_MEANINGFUL_ESTIMATE);
	}
	
	@Override
	public boolean equals(Object o) {
		boolean result = false;
		if (o instanceof EstimatedUtility) {
			final EstimatedUtility other = (EstimatedUtility) o;
			result = (this.utility == other.utility) && (this.confidenceLevel == other.confidenceLevel);
		}
		return result;
	}
	
	@Override
	public int hashCode() {
		return Double.valueOf(this.confidenceLevel + (1023 * this.utility)).hashCode();
	}
}
