package group17.opponent;

import java.util.List;

import genius.core.Bid;
import genius.core.actions.Offer;

/**
 * Opponent Model interface contract.
 * @author Martin Ingram (mi2n23)
 */
public interface OpponentModel {
	
	/**
	 * Update this opponent model from the latest {@link Offer}.
	 * @param offer Latest offer
	 * @param placedBid Have we placed a bid?
	 */
	public void update(final Offer offer, final boolean placedBid);
	
	/**
	 * Update this opponent model from the latest {@link Bid}.
	 * @param bid Latest Bid
	 * @param placedBid Have we placed a bid yet?
	 */
	public void update(final Bid bid, final boolean placedBid);
	
	/**
	 * Calculate (estimate) opponent utility for an {@link Offer}.
	 * @param offer Offer
	 * @return Estimated utility
	 */
	public double calculateUtility(final Offer offer);
	
	/**
	 * Calculate (estimate) the mean opponent utility for an {@link Offer}, i.e.
	 * the average of the overall utility and the recent utility.
	 * @param offer Offer
	 * @return Estimated utility
	 */
	public double calculateMeanUtility(final Offer offer);
	
	/**
	 * Calculate (estimate) recent opponent utility for an {@link Offer}.
	 * @param offer Offer
	 * @return Estimated utility
	 */
	public double calculateRecentUtility(final Offer offer);
	
	/**
	 * Calculate estimated opponent utility for an {@link Offer}, including confidence factor.
	 * @param offer Offer
	 * @return Estimated utility
	 */
	public EstimatedUtility calculateEstimatedUtility(final Offer offer);
	
	/**
	 * Calculate (estimate) opponent utility for an {@link Bid}.
	 * @param bid Bid
	 * @return Estimated utility
	 */
	public double calculateUtility(final Bid bid);
	
	/**
	 * Calculate (estimate) recent opponent utility for an {@link Bid}.
	 * @param bid Bid
	 * @return Estimated utility
	 */
	public double calculateRecentUtility(final Bid bid);
	
	/**
	 * Calculate (estimate) the mean opponent utility for an {@link Offer}, i.e.
	 * the average of the overall utility and the recent utility.
	 * @param bid Bid
	 * @return Estimated utility
	 */
	public double calculateMeanUtility(final Bid bid);
	
	
	/**
	 * Calculate estimated opponent utility for an {@link Bid}, including confidence factor.
	 * @param bid Bid
	 * @return Estimated utility
	 */
	public EstimatedUtility calculateEstimatedUtility(final Bid bid);
	
	/**
	 * Get a full list of previous bids.
	 * @return List of {@link Bid} objects
	 */
	public List<Bid> getAllPreviousBids();
}
