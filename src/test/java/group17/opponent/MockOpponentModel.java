package group17.opponent;

import genius.core.Bid;
import genius.core.actions.Offer;

/**
 * A simple mock implementation of {@link OpponentModel} for testing.
 * @author Martin Ingram (mi2n23)
 */
public class MockOpponentModel extends AbstractOpponentModel {

	public static final double MOCK_UTILITY = 1.0;
	public static final double MOCK_CONFIDENCE = 1.0;
	public static final EstimatedUtility MOCK_ESTIMATE = new EstimatedUtility(MOCK_UTILITY, MOCK_CONFIDENCE);

	@Override
	protected double calculateUtilityForBid(Bid bid) {
		return MOCK_UTILITY;
	}

	@Override
	protected EstimatedUtility calculateEstimatedUtilityForBid(Bid bid) {
		return MOCK_ESTIMATE;
	}

	@Override
	protected double calculateRecentUtilityForBid(Bid bid) {
		return MOCK_UTILITY;
	}

	@Override
	public double calculateMeanUtility(Offer offer) {
		return MOCK_UTILITY;
	}

	@Override
	public double calculateMeanUtility(Bid bid) {
		return MOCK_UTILITY;
	}
}
