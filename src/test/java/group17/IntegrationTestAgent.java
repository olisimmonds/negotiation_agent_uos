package group17;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import genius.core.AgentID;
import genius.core.Bid;
import genius.core.actions.Accept;
import genius.core.actions.Action;
import genius.core.actions.EndNegotiation;
import genius.core.actions.Offer;
import genius.core.parties.AbstractNegotiationParty;
import genius.core.parties.NegotiationInfo;
import group17.domain.BidSelector;
import group17.domain.BidValue;
import group17.domain.HyperparameterProperties;
import group17.opponent.OpponentModel;
import group17.opponent.utility.JonnyBlackOpponentModel;

/**
 * A simple integration testing agent based on the agents created in the labs.
 * This includes extensive logging for diagnostic purposes.
 * @author Martin Ingram
 */
public class IntegrationTestAgent extends AbstractNegotiationParty {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestAgent.class);
	private static final double TIME_LIMIT = 0.9;
	
	private AgentID agent;
	private BidSelector bidSelector;
	private Bid lastOffer;
	private double lastOfferUtility = 0.0;
    private Bid maxUtilityBid;
    private double maxUtility = 0.0;
    private Bid minUtilityBid;
    private double minUtility = 0.0;
	private OpponentModel opponentModel;
	private double range = 0.0;
	private boolean placedBid = false;
	
	// Hyperparameters
	
	private double boulwareBeta;
	private double finishTime;
	private double giveUpTime;
	private int maxListSize;
	private int recentBidWindow;
	protected double reservationValue;
	private double transitionTime;
	
	// Core agent method implementations.
	
	/**
	 * Declare that this is the Group 17 Integration Test agent.
	 */
	@Override
	public String getDescription() {
		return "Group 17 Integration Test";
	}
	
	/**
	 * Initialise the {@link OpponentModel} and cache useful information about the negotiation 
	 * as part of agent initialisation.
	 * @param info Negotiation information
	 */
	@Override
	public void init(NegotiationInfo info) {
		super.init(info);
		this.cache(info);
		this.hyperparameters();
		this.opponentModel = new JonnyBlackOpponentModel(this.getDomain(), this.recentBidWindow);
		final double initialTarget = (this.maxUtility > 0.0) ? this.maxUtility : this.getTargetBasedOnNormalisedTime();
		this.bidSelector = new BidSelector(info, 10, initialTarget - 0.1);
	}
	
	/**
	 * If we receive an offer, cache the bid and the associated utility.
	 * @param sender The agent who sent the offer
	 * @param action The action sent
	 */
	@Override
	public void receiveMessage(AgentID sender, Action action) {
		if (action instanceof Offer) {
			final Offer offer = (Offer) action;
			this.lastOffer = offer.getBid();
			this.lastOfferUtility = this.getUtility(this.lastOffer);
			LOG.info("Received offer with utility {}: {}", this.lastOfferUtility, this.lastOffer);
			this.opponentModel.update(this.lastOffer, this.placedBid);
		}
	}
	
	/**
	 * Makes a random offer above the minimum utility target
	 * Accepts everything above the reservation value at the end of the negotiation; or breaks off otherwise. 
	 */
	@Override
	public Action chooseAction(List<Class<? extends Action>> possibleActions) {
		Action action = null;
		if (this.hadOffer() && this.outOfTime()) {
			action = this.chooseFinalAction();
		} else {
			action = this.randomOffer();
		}
		return action;
	}
	
	// Bespoke methods.
	
	/**
	 * Accept an offer.
	 * @param bid The bid to accept
	 * @return Accept action
	 */
	private Action accept(final Bid bid) {
		LOG.info("ACCEPTING THE LAST OFFER.");
		return new Accept(this.agent, bid);
	}
	
	/**
	 * Cache any useful information from the negotiation so that we don't have to waste time
	 * fetching it from the original source every time an offer comes in.
	 * @param info Negotiation information
	 */
	private void cache(final NegotiationInfo info) {
		this.agent = this.getPartyId();
		this.reservationValue = this.utilitySpace.getReservationValue();
		try {
			this.maxUtilityBid = this.utilitySpace.getMaxUtilityBid();
			this.maxUtility = this.getUtility(this.maxUtilityBid);
			this.minUtilityBid = this.utilitySpace.getMinUtilityBid();
			this.minUtility = this.getUtility(this.minUtilityBid);
			this.range = this.maxUtility - this.minUtility;
		} catch (Exception e) {
			LOG.error("Error: Failed to fetch max/min bid!", e);	
		}	
	}
	
	private void hyperparameters() {
		final HyperparameterProperties hyperparameters = new HyperparameterProperties();
		hyperparameters.diagnostics();
		this.boulwareBeta = hyperparameters.getBoulwareBeta();
		this.finishTime = hyperparameters.getFinishTime();
		this.giveUpTime = hyperparameters.getGiveUpTime();
		this.maxListSize = hyperparameters.getMaxListSize();
		this.recentBidWindow = hyperparameters.getRecentBidWindow();
		this.transitionTime = hyperparameters.getTransitionTime();
	}
	
	/**
	 * Determine the final action of the negotiation.
	 * @return Accept if above the reserve, otherwise reject.
	 */
	private Action chooseFinalAction() {
		Action action = null;
		if (this.lastOfferMeetsReservation()) {
			action = this.accept(this.lastOffer);
		} else {
			action = this.reject();
		}
		return action;
	}
	
	/**
	 * Calculate a new target utility based on elapsed time.
	 * @return New target utility
	 */
	private double getTargetBasedOnNormalisedTime() {
		final double delta = this.range * this.timeline.getTime();
		final double target = this.maxUtility - delta;
		LOG.info("Current target utility = {}", target);
		return target;
	}
	
	/**
	 * Have we received an offer from another party?
	 * @return Offer received
	 */
	private boolean hadOffer() {
		return this.lastOffer != null;
	}
	
	/**
	 * Did the last offer meet our reservation value?
	 * @return Offer meets reservation
	 */
	private boolean lastOfferMeetsReservation() {
		return this.lastOfferUtility >= this.reservationValue;
	}
	
	/**
	 * Generate a random offer above the current target.
	 * @return Random counter offer
	 */
	private Action randomOffer() {
		final double target = this.getTargetBasedOnNormalisedTime();
		this.bidSelector.expandList(10, target);
		final Bid random = this.bidSelector.getRandomBid();
		this.generateAnalytics(random);	
		this.placedBid = true;
		return new Offer(this.agent, random);
	}
	
	/**
	 * Dump a CSV representation of our bid to the log file.
	 * @param bid Our bid
	 */
	private void generateAnalytics(final Bid bid) {
		final double utility = this.getUtility(bid);
		final BidValue bidValue = BidValue.createAgentBidValue(bid, utility);
		if (!this.placedBid && !this.hadOffer()) {
			LOG.debug("{}", bidValue.toStringLabels());
		}
		LOG.debug("{}", bidValue);
		LOG.info("Placing random bid with utility {}: {}", utility, bid);	
	}
	
	/**
	 * Are we out of time?
	 * @return We're out of time
	 */
	private boolean outOfTime() {
		return this.timeline.getTime() >= TIME_LIMIT;
	}

	/**
	 * Reject the last offer.
	 * @return Reject (end negotiation) offer
	 */
	private Action reject() {
		LOG.info("REJECTING THE LAST OFFER.");
		return new EndNegotiation(this.agent);
	}
}
