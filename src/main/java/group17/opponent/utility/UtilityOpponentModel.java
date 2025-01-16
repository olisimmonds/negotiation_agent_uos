package group17.opponent.utility;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.Value;
import group17.domain.BidValue;
import group17.domain.DomainTranslator;
import group17.opponent.AbstractOpponentModel;
import group17.opponent.EstimatedUtility;
import group17.opponent.OpponentModel;

/**
 * Generic {@link OpponentModel} that handles preference values, weights and utilities.
 */
public abstract class UtilityOpponentModel<UOI extends UtilityOpponentIssue<?>, UOV extends UtilityOpponentValue> 
    extends AbstractOpponentModel {

	// TODO Remove or replace all logging before release!
	private static final Logger LOG = LoggerFactory.getLogger(UtilityOpponentModel.class);
	
	private static final double DEFAULT_CONFIDENCE_LEVEL = 1.0;
	
	private Map<String, UOI> issues = new HashMap<>();
	private int bidUpdates = 0;
	private int recentBidWindow = 10; // Safe default
	private double totalUnnormalized = 0.0;
	private double recentUnnormalized = 0.0;
	
	/**
	 * Create an initial {@link OpponentModel} from the domain.
	 * @param domain Negotiation domain
	 */
	public UtilityOpponentModel(final Domain domain) {
		if (domain != null) {
			this.createIssueMap(domain);
		} else {
			LOG.error("No issues processed: the domain was null.");
		}
	}
	
	/**
	 * Create an initial {@link OpponentModel} from the domain.
	 * @param domain Negotiation domain
	 * @param recentBidWindow Recent bid window
	 */
	public UtilityOpponentModel(final Domain domain, final int recentBidWindow) {
		this(domain);
		this.recentBidWindow = recentBidWindow;
	}

	@Override
	public void update(final Bid bid, final boolean placedBid) {
		super.update(bid, placedBid);
		this.bidUpdates++;
		this.updateIssuesFromBid(bid);
		this.removeOldBidFromRecentCounts();
		this.recalculateModelEstimates();
		this.generateAnalytics(bid, placedBid);
	}
	
	/**
	 * Dump a CSV representation of the bid to the log file.
	 * @param bid Bid
	 */
	private void generateAnalytics(final Bid bid, final boolean placedBid) {
		final double utility = this.calculateUtility(bid);
		final BidValue bidValue = BidValue.createOpponentBidValue(bid, utility);
		if (!placedBid && this.bidUpdates == 1) {
			LOG.debug("{}", bidValue.toStringLabels());
		}
		LOG.debug("{}", bidValue);
	}
	
	@Override
	public String toString() {
		return new StringBuilder("UtilityOpponentModel: bidUpdates=")
		    .append(this.bidUpdates)
		    .append(", issues=")
		    .append(this.issues.values())
		    .toString();
	}

	@Override
	protected double calculateUtilityForBid(final Bid bid) {
		double utility = 0.0;
		for (final Issue issue : bid.getIssues()) {
			final Value value = bid.getValue(issue);
			final String issueName = DomainTranslator.issueName(issue);
			final String valueName = DomainTranslator.valueName(value);
			utility += getIssueValueUtility(issueName, valueName); 
		}
		return utility;
	}
	
	@Override
	protected double calculateRecentUtilityForBid(final Bid bid) {
		double utility = 0.0;
		for (final Issue issue : bid.getIssues()) {
			final Value value = bid.getValue(issue);
			final String issueName = DomainTranslator.issueName(issue);
			final String valueName = DomainTranslator.valueName(value);
			utility += getRecentIssueValueUtility(issueName, valueName); 
		}
		return utility;
	}

	@Override
	protected EstimatedUtility calculateEstimatedUtilityForBid(final Bid bid) {
		final double utility = this.calculateUtilityForBid(bid);
		return new EstimatedUtility(utility, DEFAULT_CONFIDENCE_LEVEL);
	}
	
	/**
	 * Get the utility for a specific issue and value combination.
	 * @param issueName Issue name
	 * @param valueName Value name
	 * @return Utility
	 */
	protected double getIssueValueUtility(final String issueName, final String valueName) {
		final UOI issue = this.issues.get(issueName);
		final UtilityOpponentValue value = issue.getValue(valueName);
		final double issueWeight = issue.getNormalizedWeight();
		final double preferenceValue = value.getPreferenceValue();
		return issueWeight * preferenceValue;
	}
	
	/**
	 * Get the recent utility for a specific issue and value combination.
	 * @param issueName Issue name
	 * @param valueName Value name
	 * @return Utility
	 */
	protected double getRecentIssueValueUtility(final String issueName, final String valueName) {
		final UOI issue = this.issues.get(issueName);
		final UtilityOpponentValue value = issue.getValue(valueName);
		final double recentCount = value.getRecentCount();
		final double issueWeight = issue.getNormalizedRecentWeight();
		final double preferenceValue = value.getRecentPreferenceValue();
		final double utility = issueWeight * preferenceValue;
		return utility;
	}
	
	/**
	 * Recalculate the estimates in this model.
	 */
	protected void recalculateModelEstimates() {
		this.updatePreferencesAndUnnormalizedWeights();
		this.updateNormalizedWeights();
	}
	
	/**
	 * Set the number of bid updates that have occurred. Only used for testing purposes.
	 * @param bidUpdates Bid updates
	 */
	protected void setBidUpdates(final int bidUpdates) {
		this.bidUpdates = bidUpdates;
	}
	
	/**
	 * Set the number of recent bids to consider. Only used for testing purposes.
	 * @param bidUpdates Bid updates
	 */
	protected void setRecentBidWindow(final int recentBidWindow) {
		this.recentBidWindow = recentBidWindow;
	}
	
	/**
	 * Increment the count for a specific issue and value combination. 
	 * @param name Issue name
	 * @param value Issue value
	 */
	protected void updateIssue(final String name, final String value) {
		final UOI issue = this.issues.get(name);
		issue.incrementValueCount(value);
	}	
	
	/**
	 * Decrement the recent count for a specific issue and value combination. 
	 * @param name Issue name
	 * @param value Issue value
	 */
	protected void decrementIssue(final String name, final String value) {
		final UOI issue = this.issues.get(name);
		issue.decrementValueRecentCount(value);
	}	
	
	/**
	 * Get the current issue map. 
	 * This is only used for testing.
	 * @return The current issue map
	 */
	protected Map<String, UOI> getIssues() {
		return this.issues;
	}
	
	/**
	 * Find an old bid to remove from recent counts.
	 * @return Old bid
	 */
	protected Bid findOldBid() {
		Bid oldBid = null;
		final int size = this.bids.size();
		if (size > this.recentBidWindow) {
			final int index = size - this.recentBidWindow - 1;
			oldBid = this.bids.get(index);
		}
		return oldBid;
	}
	
	/**
	 * Create a map of issues and possible values (options) based on the domain.
	 * @param domain Domain
	 */
	private void createIssueMap(final Domain domain) {
		for (final Issue issue : domain.getIssues()) {
			if (issue != null) {
				final UOI opponentIssue = this.createUtilityOpponentIssue(issue);
				final String name = DomainTranslator.issueName(issue);
				this.issues.put(name, opponentIssue);
			}
		}	
	}
	
	protected abstract UOI createUtilityOpponentIssue(final Issue issue);

	/**
	 * Update the issues from a received bid.
	 * @param bid Bid
	 */
	private void updateIssuesFromBid(final Bid bid) {
		for (final Issue issue : bid.getIssues()) {
			final Value value = bid.getValue(issue);
			final String issueName = DomainTranslator.issueName(issue);
			final String valueName = DomainTranslator.valueName(value);
			this.updateIssue(issueName, valueName);
		}
	}
	
	/**
	 * Decrease the recent issue counts from an old bid that we want to now ignore.
	 * @param bid Old Bid
	 */
	protected void decreaseRecentIssueCountsFromOldBid(final Bid bid) {
		if (bid != null) {
			for (final Issue issue : bid.getIssues()) {
				final Value value = bid.getValue(issue);
				final String issueName = DomainTranslator.issueName(issue);
				final String valueName = DomainTranslator.valueName(value);
				this.decrementIssue(issueName, valueName);
			}
		}
	}
	
	protected void removeOldBidFromRecentCounts() {
		if (this.bids.size() > this.recentBidWindow) {
			final Bid oldBid = this.findOldBid();
			this.decreaseRecentIssueCountsFromOldBid(oldBid);
		}
	}
	
	/**
	 * Update the normalized weights for each issue.
	 */
	private void updateNormalizedWeights() {
		for (final UOI issue : this.issues.values()) {
			if (issue != null) {
				issue.calculateNormalizedWeight(this.totalUnnormalized);
				issue.calculateNormalizedRecentWeight(this.recentUnnormalized);
			}
		}
	}

	/**
	 * Update preferences and unnormalized weights for each issue.
	 */
	private void updatePreferencesAndUnnormalizedWeights() {
		this.totalUnnormalized = 0.0;
		this.recentUnnormalized = 0.0;
		for (final UOI issue : this.issues.values()) {
			if (issue != null) {
				issue.updatePreferenceValues(this.bidUpdates, this.recentBidWindow);
				this.totalUnnormalized += issue.calculateUnnormalizedWeight(this.bidUpdates);
				this.recentUnnormalized += issue.calculateUnnormalizedRecentWeight(this.recentBidWindow);
			}
		}
	}
}
