package group17.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import genius.core.parties.NegotiationInfo;
import genius.core.uncertainty.BidRanking;
import genius.core.uncertainty.UserModel;
import genius.core.utility.AbstractUtilitySpace;

/**
 * A bid selector which maintains a list of bids over a given utility threshold and 
 */
public class BidSelector {
	
	public static final int MAX_ATTEMPTS = 1000;
	
	private final Random random;
	private final AbstractUtilitySpace space;
	private final boolean uncertainty;
	
	private List<Bid> bids = new ArrayList<>();
	private int bidLimit;
	private Domain domain;
	private List<Issue> issues = new ArrayList<>();
	private double minimumUtility;
	private double previousMinimumUtility = 1.0;
	private BidRanking ranking;
	
	/**
	 * Create a Bid Selector from the negotiation information.
	 * @param info Negotiation information
	 * @param bidLimit
	 * @param minimumUtility
	 */
	public BidSelector(final NegotiationInfo info, final int bidLimit, final double minimumUtility) {
		this.random = new Random(info.getRandomSeed());
		this.bidLimit = bidLimit;
		this.minimumUtility = minimumUtility;
		this.space = info.getUtilitySpace();
		final UserModel userModel = info.getUserModel();
		this.uncertainty = (userModel != null);
		if (this.uncertainty) {
			this.getInitialBidsFromUserModel(userModel);
		} else {
			this.getInitialBidsFromUtilitySpace();
		}
	}

	/**
     * Get the full list of {@link Bid} objects.
     * @return List of Bids
     */
	public List<Bid> getBids() {
		return this.bids;
	}
	
	/**
	 * Get a random bid from the current list.
	 * @return A random bid or null if the list is empty.
	 */
	public Bid getRandomBid() {
		Bid selected = null;
		if (this.bids != null && this.bids.size() > 0) {
			final int numBids = this.bids.size();
			final int index = this.random.nextInt(numBids);
			selected = this.bids.get(index);
		}
		return selected;
	}
	
	/**
	 * Expand the list.
	 * @param numBids The number of extra bids to add
	 * @param minimumUtility The new minimum utility
	 */
	public void expandList(final int numBids, final double minimumUtility) {
		this.bidLimit += numBids;
		this.previousMinimumUtility = this.minimumUtility;
		this.minimumUtility = minimumUtility;
		if (this.uncertainty) {
			this.addBidsFromRanking();
		} else {
			this.addBidsFromDomain();
		}
	}
	
	public String toString() {
		return new StringBuilder("BidSelector: bids=")
			.append(this.bids.size())
			.append(", limit=")
			.append(this.bidLimit)
			.append(", uncertainty=")
			.append(this.uncertainty)
			.append(", minimumUtility=")
			.append(this.minimumUtility)
			.toString();
	}
	
	/**
	 * Set the list of bids from the user model.
	 * @param userModel User model
	 */
	private void getInitialBidsFromUserModel(final UserModel userModel) {
		this.ranking = userModel.getBidRanking();
		this.addBidsFromRanking();
	}
	
	/**
	 * Find all bids in the bid ranking between the current minimum utility and the previous minimum utility
	 * and add them to our list.
	 * @param previousMinimumUtility Previous minimum utility
	 */
	private void addBidsFromRanking() {
		if (this.ranking != null) {
			final Set<Bid> bidSet = this.createBidSetFromBidList();
			for (final Bid bid : this.ranking.getBidOrder()) {
				final double utility = this.getUtility(bid);
				if (utility >= this.minimumUtility && utility <= this.previousMinimumUtility) {
					bidSet.add(bid);
				}
				if (bidSet.size() >= this.bidLimit) {
					break;
				}
			}
			this.createNewBidListFrom(bidSet);
		}
	}
	
	/**
	 * Set the list of bids from the utility space.
	 */
	private void getInitialBidsFromUtilitySpace() {
		this.domain = this.space.getDomain();
		if (domain != null) {
			this.issues = domain.getIssues();
			this.addBidsFromDomain();
		}
	}
	
	/**
	 * Add random bids above the current minimum utility to the list until either we hit the maximum number of bids
	 * required OR the number of maximum attempts have been reached.
	 */
	private void addBidsFromDomain() {
		if (this.issues != null) {
			final Set<Bid> bidSet = this.createBidSetFromBidList();
			for (int i = 0; i < MAX_ATTEMPTS; i++) {
				final Bid bid = this.generateRandomBid();
				final double utility = this.getUtility(bid);
				if (utility >= this.minimumUtility && utility <= this.previousMinimumUtility) {
					bidSet.add(bid);
				}
				if (bidSet.size() >= this.bidLimit) {
					break;
				}
			}
			this.createNewBidListFrom(bidSet);
		}		
	}
		
	/**
	 * Generate a random {@link Bid} based on the same algorithm used by {@link AbstractNegotiationParty}.
	 * @return A random bid
	 */
	private Bid generateRandomBid() {
		final HashMap<Integer, Value> values = new HashMap<>();
		for (final Issue issue : this.issues) {
			if (issue != null) {
				final int number = issue.getNumber();
				final Value value = this.randomValueFrom(issue);
				values.put(number, value);
			}
		}
		return new Bid(this.domain, values);
	}

	/**
	 * Pick a random value from the supplied Issue.
	 * Note that we only handle {@link DiscreteIssue} issues here.
	 * @param issue Discrete issue
	 * @return Random value
	 */
	private Value randomValueFrom(final Issue issue) {
		final IssueDiscrete discrete = (IssueDiscrete) issue;
		final int numValues = discrete.getNumberOfValues();
		final int index = this.random.nextInt(numValues);
		return discrete.getValue(index);
	}

	/**
	 * Get the utility for a bid.
	 * @param bid Bid
	 * @return Bid utility, or zero if anything goes wrong.
	 */
	private double getUtility(final Bid bid) {
		double utility = 0.0;
		try {
			utility = this.space.getUtility(bid);
		} catch (Exception e) {
			// Do nothing here.
		}
		return utility;
	}
	
	/**
	 * Create a new list of bids from the supplied set.
	 * @param bidSet New set of bids
	 */
	private void createNewBidListFrom(final Set<Bid> bidSet) {
		this.bids = new ArrayList<>();
		this.bids.addAll(bidSet);
	}
	
	/**
	 * Transform the current list of bids into a set.
	 * @return Bid set
	 */
	private Set<Bid> createBidSetFromBidList() {
		final Set<Bid> bidSet = new HashSet<>();
		bidSet.addAll(this.bids);
		return bidSet;
	}
}
