package group17.opponent;

import java.util.ArrayList;
import java.util.List;

import genius.core.Bid;
import genius.core.actions.Offer;

/**
 * An abstract {@link OpponentModel} that provides some common, reusable functionality.
 */
public abstract class AbstractOpponentModel implements OpponentModel {

	protected final List<Bid> bids = new ArrayList<>();
		
	@Override
	public void update(final Offer offer, final boolean placedBid) {
		if (offer != null) {
			final Bid bid = offer.getBid();
			this.update(bid, placedBid);
		}
	}

	@Override
	public void update(final Bid bid, final boolean placedBid) {
		if (bid != null) {
			this.bids.add(bid);
		}
	}

	@Override
	public double calculateUtility(final Offer offer) {
		return (offer != null) ? this.calculateUtility(offer.getBid()) : EstimatedUtility.NO_UTILITY;
	}
	
	@Override
	public double calculateUtility(final Bid bid) {
		return (bid != null) ? this.calculateUtilityForBid(bid) : EstimatedUtility.NO_UTILITY;
	}
	
	@Override
	public double calculateMeanUtility(final Offer offer) {
		final double utility = this.calculateUtility(offer);
		final double recent = this.calculateRecentUtility(offer);
		return (utility + recent) / 2.0;
	}
	
	@Override
	public double calculateMeanUtility(final Bid bid) {
		final double utility = this.calculateUtility(bid);
		final double recent = this.calculateRecentUtility(bid);
		return (utility + recent) / 2.0;
	}
	
	@Override
	public double calculateRecentUtility(final Offer offer) {
		return (offer != null) ? this.calculateRecentUtility(offer.getBid()) : EstimatedUtility.NO_UTILITY;
	}
	
	@Override
	public double calculateRecentUtility(final Bid bid) {
		return (bid != null) ? this.calculateRecentUtilityForBid(bid) : EstimatedUtility.NO_UTILITY;
	}
	
	/**
	 * Calculate (estimate) opponent utility for an {@link Bid} - no null checks required.
	 * @param bid A not-null bid
	 * @return Opponent utility
	 */
	protected abstract double calculateUtilityForBid(final Bid bid);
	
	/**
	 * Calculate (estimate) recent opponent utility for an {@link Bid} - no null checks required.
	 * @param bid A not-null bid
	 * @return Opponent utility
	 */
	protected abstract double calculateRecentUtilityForBid(final Bid bid);

	
	@Override
	public EstimatedUtility calculateEstimatedUtility(final Offer offer) {
		return (offer != null) ? this.calculateEstimatedUtility(offer.getBid()) : EstimatedUtility.NO_MEANINGFUL_ESTIMATE;
	}
	
	@Override
	public EstimatedUtility calculateEstimatedUtility(final Bid bid) {
		return (bid != null) ? this.calculateEstimatedUtilityForBid(bid) : EstimatedUtility.NO_MEANINGFUL_ESTIMATE;
	}
	
	/**
	 * Calculate estimated opponent utility for an {@link Bid}, including confidence factor - no null checks required.
	 * @param bid Bid A not-null bid
	 * @return Estimated utility
	 */	
	protected abstract EstimatedUtility calculateEstimatedUtilityForBid(final Bid bid);

	@Override
	public List<Bid> getAllPreviousBids() {
		return this.bids;
	}
}
