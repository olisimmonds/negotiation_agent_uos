package group17;

import java.util.*;
import java.util.stream.Collectors;

import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import genius.core.issue.ValueDiscrete;
import genius.core.uncertainty.AdditiveUtilitySpaceFactory;
import group17.user.LPSolver;
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
import genius.core.utility.AbstractUtilitySpace;
import group17.domain.BidSelector;
import group17.domain.BidValue;
import group17.domain.HyperparameterProperties;
import group17.opponent.OpponentModel;
import group17.opponent.utility.Agent17OpponentModel;
import group17.opponent.utility.JonnyBlackOpponentModel;

/**
 * Negotiation Agent for COMP6203 Group 17.
 */
public class Agent17 extends AbstractNegotiationParty {
	
	private static final Logger LOG = LoggerFactory.getLogger(Agent17.class);

 	private static final long serialVersionUID = 1L;
	
	// Bids
	private Bid myLastOffer;
	private Bid lastOffer;
	private List<Bid> opponentBids = new ArrayList<>();
	
	// Models
	private OpponentModel jbOpponentModel;
	private OpponentModel opponentModel;
	
	private List<Bid> orderBids = new ArrayList<>();
	
	// Hyper-parameters
	private double boulwareBeta;
	private double finishTime;
	private double giveUpTime;
	private int maxListSize;
	private int recentBidWindow;
	protected double reservationValue;
	private double transitionTime;
	private double maxElicitationPenalty;
	
	// Variables
	private double minT;
	private boolean placedBid = false;	
	private int round = 0;
	
	/**
	 * Initialises a new instance of the agent.
	 */
	@Override
	public void init(NegotiationInfo info) 
	{
		super.init(info);
		this.hyperparameters();

		if (hasPreferenceUncertainty()) {
			System.out.println("Preference uncertainty is enabled.");

			elicitBids();

			// We start with our ordered list of possible bids
	    	orderBids.addAll(getUserModel().getBidRanking().getBidOrder());
		}
		else {
			this.generateBids(info);
		}
		this.jbOpponentModel = new JonnyBlackOpponentModel(this.getDomain(), this.recentBidWindow);
		this.opponentModel = new Agent17OpponentModel(this.getDomain(), this.recentBidWindow);
	}
		
	/**
	 * Load our hyperparameters from a properties file.
	 */
	private void hyperparameters() {
		final HyperparameterProperties hyperparameters = new HyperparameterProperties();
		hyperparameters.diagnostics();
		this.boulwareBeta = hyperparameters.getBoulwareBeta();
		this.finishTime = hyperparameters.getFinishTime();
		this.giveUpTime = hyperparameters.getGiveUpTime();
		this.maxListSize = hyperparameters.getMaxListSize();
		this.recentBidWindow = hyperparameters.getRecentBidWindow();
		this.transitionTime = hyperparameters.getTransitionTime();
		this.maxElicitationPenalty = hyperparameters.getMaxElicitationPenalty();
	}
	
    /*
     * Generate a list of candidate bids above our reservation value.
     * @param info Negotiation information
     */
	private void generateBids(final NegotiationInfo info) { 
    	final double reservation = utilitySpace.getReservationValue();
    	final BidSelector selector = new BidSelector(info, this.maxListSize, reservation);
    	final List<Bid> bids = selector.getBids();
    	LOG.info("Candidate bids = {}", bids.size());
    	orderBids.addAll(bids);
	}

	/**
     * Given a bid from the opponent the choose action function decides how to handle it. This factors in 
     * how much time is left and if the opponents utility can be improved.
     * Done: Not keen on the nested ifs with multiple return points here. It's a bug magnet, as illustrated
     *       by the wonky open/close braces :-) I'd split this into functional primitives.
     */
    @Override
    public Action chooseAction(List<Class<? extends Action>> possibleActions) {
    	
    	
        if (lastOffer != null && myLastOffer != null) {
        	// If we have preference uncertainty, I think we update our user model here

        	double threshold = minTarget();
        	double time = getTime();
        	double utilityLastOffer = getUtility(lastOffer);
        	double resValue = utilitySpace.getReservationValue();
        	Bid bestBidFromOpp = bestBidFromOpponent();
        	double utilityOfBestBidFromOpp = getUtility(bestBidFromOpp);
        	
        	if (goodOfferNoTime(threshold, time, utilityLastOffer)) {
        		return new Accept(this.getPartyId(), lastOffer);
        	}
        	
        	if (okayOfferNoTime(time, utilityOfBestBidFromOpp, resValue)) {
        		if (utilityOfBestBidFromOpp - utilityLastOffer > 0.1) {
        			return new Accept(this.getPartyId(), lastOffer);
        		}
        		return this.createOfferFromBid(bestBidFromOpp); 
        	}
        	
        	Bid potentialBid = biddingStrategy(threshold);
        	if (goodOfferButCouldBeBetter(threshold, utilityLastOffer, potentialBid) || badOfferButTime(time)) {
        		return this.createOfferFromBid(potentialBid);
        	}
        	
        	if (greatOffer(utilityLastOffer, threshold)) {
        		return new Accept(this.getPartyId(), lastOffer);
        	}
        	
        	// Otherwise, we simply end the negotiation.
        	return new EndNegotiation(getPartyId());
        }
    	
        return this.createOfferFromBid(biddingStrategy(minTarget()));	
    }
    
    /**
     * Create an offer from a bid and add it to the diagnostics.
     * @param bid Bid
     * @return Offer
     */
    private Offer createOfferFromBid(final Bid bid) {
    	this.generateAnalytics(bid);
    	this.placedBid = true;
    	return new Offer(this.getPartyId(), bid);
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
	 * Have we received an offer from another party?
	 * @return Offer received
	 */
	private boolean hadOffer() {
		return this.lastOffer != null;
	}
    
    /**
	 * If offer from opponent has utility better or equal to our threshold and we are running out of time we accept.
	 * Maybe this can be improved by offering the opponent the best bid he has given us. Maybe the time in which we 
	 * abandon the strategy of trying to increase the opponents utility can be dependent on a variable which says 
	 * how confident we are in our opponent model. For now I will just set this time variable to be a hyper-
	 * parameter which we can maximise later.
	 */
    private boolean goodOfferNoTime(double threshold, double time, double utilityLastOffer) {
        return utilityLastOffer >= threshold && time >= this.finishTime;
    }
    
    /**
	 * If we are running out of time we offer the opponent the best bid they ever offered us. 
	 * We may need to change this if our opponent changes their own preferences.
	 */
    private boolean okayOfferNoTime(double time, double utilityOfBestBidFromOpp, double resValue) {
        return time >= this.giveUpTime && utilityOfBestBidFromOpp >= resValue;
    }
    
    /**
	 * If there exists a bid which we believe has a higher utility for both us and our opponent then we counter offer 
	 * with this bid. 
	 * Maybe we would want to have a margin of error here. With complex domains there may be endless micro 
	 * improvements and we only want to offer counter offers if they improve our opponents and our utilities by 0.01 
	 * or something.
	 */
    private boolean goodOfferButCouldBeBetter(double threshold, double utilityLastOffer, Bid potentialBid) {
        return utilityLastOffer >= threshold && this.calculateOpponentUtility(potentialBid) >= this.calculateOpponentUtility(lastOffer)
				&& getUtility(potentialBid) >= utilityLastOffer;
    }
    
    /**
	 * If we are still early in the negotiation and not happy with the bid from our opponent we make a counter offer.
	 */
    private boolean badOfferButTime(double time) {
        return time < this.giveUpTime;
    }
    
    /**
	 * If we are happy with the bid from the opponent we accept.
	 */
    private boolean greatOffer(double threshold, double utilityLastOffer) {
        return utilityLastOffer >= threshold;
    }    
    
    /**
	 * Our bidding strategy for when we aren't constrained by time.
	 * The first strategy we will build will be one which try's to maximise our opponents utility from 
	 * bids which are above our utility threshold. In small domains we can test every possible bid but in 
	 * larger domains we will have to use some form of estimate. There are two methods to do this seen in
	 * the labs. Either we randomly generate bids or look at the ordered bids list. The latter seems 
	 * better to me so will use this but if we generated random bids in a more cleaver way then maybe 
	 * that approach would be best.
	 */
    private Bid biddingStrategy(double threshold) {
    	// Now we are interested in the sublist of bids which have utility above our threshold.
    	
    	List<Bid> bidsAboveThresholdUtility = orderBids.stream().filter(bid -> getUtility(bid)>threshold).collect(Collectors.toList());
    	
    	// If there is no bid in my range I will resort back to a strategy of placing the bid with the 
    	// highest possible utility.
		if (bidsAboveThresholdUtility.size() == 0) {
			myLastOffer = getMaxUtilityBid();
		}
		
		// Else I find the bid from this list which maximises my opponents utility. 
		else {
			
			// If the list of possible bids above our utility is too large then I consider a random subset.
			if (bidsAboveThresholdUtility.size() > this.maxListSize) {
				Collections.shuffle(bidsAboveThresholdUtility);
				bidsAboveThresholdUtility = bidsAboveThresholdUtility.subList(0, this.maxListSize);
			}
			
			myLastOffer = getMaxUtilityBid();
			double lastOfferUtility = this.calculateOpponentUtility(myLastOffer);
			
			for (final Bid bid : bidsAboveThresholdUtility) {
				
				double potentialUtility = this.calculateOpponentUtility(bid);
				if (potentialUtility>lastOfferUtility) {
					
					lastOfferUtility = potentialUtility;
					myLastOffer = bid;
				}
			}
		}
        return myLastOffer;
    }
    
    /**
	 * Function which finds which bid from the opponent we liked the most
	 */
    private Bid bestBidFromOpponent() { 	
    	// Initialise
    	Bid bestBid = lastOffer;
    	double utilityToBeat = 0;
    	double pottentialUtility = 0;
    	
    	// Loop through all bids seen from opponent
    	for (final Bid bid : opponentBids) {
    		
    		pottentialUtility = getUtility(bid);
    		// If we see a bid which is better than that which is stored then we update
    		if (pottentialUtility > utilityToBeat) {
    			utilityToBeat = pottentialUtility;
    			bestBid = bid;
    		}  		
    	}
    	return bestBid;
    }
    
    /**
	 * Gets the time, running from t = 0 (start) to t = 1 (deadline).
	 */
    private double getTime() {
		return getTimeLine().getTime();
	}
    
    /**
	 * Generates best possible bid.
	 */
    private Bid getMaxUtilityBid() {
	    try {
	        return utilitySpace.getMaxUtilityBid();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}
    
    /**
	 * Function to generate minimum utility for accepting/placing bids.
	 * We start using a Boulware strategy and transition to tit for tat as we gain more information
	 * about opponents utility space. The time we transition could be a hyper-parameter or be 
	 * calculated based on our confidence in our opponents utility space. When we move to tit for 
	 * tat we consider how far the opponents bid is from the estimated NE.
	 */
    private double minTarget() {
    	// Get time
    	double time = getTime();
    	this.round++;
    	// If we are before transition time then we do a Boulware strategy. 
    	if (time < this.transitionTime) {
    		return Boulware();
    	} 	
    	// Now its time to transition to Tit-for-Tat.
    	else {
    		return titForTat();
    	}		
	}
    
    /**
	 * Boulware
	 * The minU variable is currently our reservation value but could be more sophisticated and be 
	 * dependent on the Nash equilibrium.
	 */
    private double Boulware() {
    	double time = getTime();
    	double ft = Math.pow(time,(1/this.boulwareBeta));
		double minU = utilitySpace.getReservationValue();
		minT = minU + (1-ft)*(getUtility(getMaxUtilityBid())-minU);
		LOG.info("Round {}, Time = {}, FT = {}, Boulware target = {}", this.round, time, ft, minT);
		return minT;
    }
    
    /**
	 * Tit-for-tat
	 */
    private double titForTat() {
    	
    	Bid penultimateBid = opponentBids.get(opponentBids.size() - 2);
		double difOfOpponentsLastTwo = this.calculateOpponentUtility(penultimateBid) - this.calculateOpponentUtility(lastOffer);
		
		// It's possible that under our model of the opponent, we belive the opponent has actually
		// increased their utility from one bid to another. To avoid problems with this we set a 
		// min value of the tatValue to be 0.
		double tatValue = Math.max(difOfOpponentsLastTwo, 0);
		
		// When we are close to Nash we are happy to concede as quick as opponent. When we are far
		// we want to concede slower. We calculate what we think the nash is and then see how far 
		// away we are. This gives a value between 0 and 1.
		double distFromNE = optimalNash() - currentNash(lastOffer);
		minT -= (1-distFromNE)*tatValue;
		LOG.info("Round {}, Tit for Tat target = {}", this.round, minT);
		return minT;
    }
    

    /**
	 * Estimates optimal NE
	 */
	private double optimalNash() {
		double mySideOfNash = getUtility(getMaxUtilityBid())- utilitySpace.getReservationValue();
		double oppSideOfNash = this.calculateOpponentUtility(bestBidFromOpponent());
		return mySideOfNash*oppSideOfNash;
	}
	
	/**
	 * Estimates NE of current bid
	 */
	private double currentNash(Bid bid) {
		double mySideOfNash = getUtility(bid)- utilitySpace.getReservationValue();
		double oppSideOfNash = this.calculateOpponentUtility(bid);
		return mySideOfNash*oppSideOfNash;
	}

	/**
	 * Remembers the offers received by the opponent.
	 */
    @Override
    public void receiveMessage(AgentID sender, Action action) {
        super.receiveMessage(sender, action);

        if (action instanceof Offer) 
		{
			lastOffer = ((Offer) action).getBid();
			opponentModel.update(lastOffer, this.placedBid);
			// Store bids for tit_for_tat method. 
			// This can be improved by only storing the penultimate bid but for now I will
			// store all of them incase I need this later.
			opponentBids.add(lastOffer);
		}
    }

	private double maximumAllowedElicitations() {
		return maxElicitationPenalty / user.getElicitationCost();
	}

	private double getAllowedStartingBids() {
		long possibleBids = getDomain().getNumberOfPossibleBids();
		int numOfOrderedBids = userModel.getBidRanking().getBidOrder().size();

		return possibleBids <= 100 ?
				Math.max(Math.min(possibleBids*0.1-numOfOrderedBids, maximumAllowedElicitations()), 0) :
				Math.max(Math.min(10-numOfOrderedBids, maximumAllowedElicitations()),0);
	}

	private void elicitBids() {
		int allowedBids = (int) getAllowedStartingBids();

		for (int i = 0; i < allowedBids; ++i) {
			userModel = user.elicitRank(generateRandomBid(), userModel);
		}
	}
  
    /**
     * A human-readable description for this party.
     */
    @Override
	public String getDescription() {
		return "Group 17 Agent";
	}

    /**
	 * This stub can be expanded to deal with preference uncertainty in a more sophisticated way than the default behaviour.
	 */
	@Override
	public AbstractUtilitySpace estimateUtilitySpace() {
		LPSolver lpSolver = new LPSolver(userModel);
		LPSolver.LPSolverResult result = lpSolver.solve();
		if (result.failed()) return super.estimateUtilitySpace();

		AdditiveUtilitySpaceFactory factory = new AdditiveUtilitySpaceFactory(userModel.getDomain());

		HashMap<Value, Double> utils = (HashMap<Value, Double>) result.solvedValues();
		HashMap<Issue, Double> weights = (HashMap<Issue, Double>) result.solvedIssues();

		for (Issue i : getDomain().getIssues()) {
			factory.setWeight(i, weights.get(i));
			for (ValueDiscrete value : ((IssueDiscrete)i).getValues()) {
				factory.setUtility(i, value, utils.get(value));
			}
		}
		factory.normalizeWeights();
		return factory.getUtilitySpace();
	}
	
	private double calculateOpponentUtility(final Bid bid) {
		return this.getRecentOpponentUtility(bid);
	}
	
	// Options for calculating opponent utility.
	
	/**
	 * Opponent utility from across every offer received, using the Agent 17 model.
	 * @param bid Bid
	 * @return Utility
	 */
	private double getOpponentUtility(final Bid bid) {
		return this.opponentModel.calculateUtility(bid);
	}
	
	/**
	 * Opponent utility from recent offers only, using the Agent 17 model.
	 * @param bid Bid
	 * @return Utility
	 */
	private double getRecentOpponentUtility(final Bid bid) {
		return this.opponentModel.calculateRecentUtility(bid);
	}
	
	/**
	 * Mean opponent utility (both total and recent) from across every offer received, using the Agent 17 model.
	 * @param bid Bid
	 * @return Utility
	 */
	private double getMeanOpponentUtility(final Bid bid) {
		return this.opponentModel.calculateMeanUtility(bid);
	}
	
	/**
	 * Opponent utility from across every offer received, using the Jonny Black model.
	 * @param bid Bid
	 * @return Utility
	 */
	private double getOpponentUtilityFromJB(final Bid bid) {
		return this.jbOpponentModel.calculateUtility(bid);
	}
	
	/**
	 * Oopponent utility from from recent offers only, using the Jonny Black model.
	 * @param bid Bid
	 * @return Utility
	 */
	private double getOpponentRecentUtilityFromJB(final Bid bid) {
		return this.jbOpponentModel.calculateRecentUtility(bid);
	}
	
	/**
	 * Mean opponent utility (both total and recent) from across every offer received, using the Jonny Black model.
	 * @param bid Bid
	 * @return Utility
	 */
	private double getMeanOpponentUtilityFromJB(final Bid bid) {
		return this.jbOpponentModel.calculateMeanUtility(bid);
	}
	
	
	/**
	 * Mean opponent utility from across every offer received, using both models.
	 * @param bid Bid
	 * @return Utility
	 */
	private double getOpponentUtilityFromBothModels(final Bid bid) {
		final double jb = this.jbOpponentModel.calculateUtility(bid);
		final double a17 = this.opponentModel.calculateUtility(bid);
		return (jb + a17) / 2.0;
	}
	
	/**
	 * Oopponent utility from from recent offers only, using both models.
	 * @param bid Bid
	 * @return Utility
	 */
	private double getRecentOpponentUtilityFromBothModels(final Bid bid) {
		final double jb = this.jbOpponentModel.calculateRecentUtility(bid);
		final double a17 = this.opponentModel.calculateRecentUtility(bid);
		return (jb + a17) / 2.0;
	}
	
	/**
	 * Mean opponent utility (both total and recent) from across every offer received, using both models.
	 * @param bid Bid
	 * @return Utility
	 */
	private double getMeanOpponentUtilityFromBothModels(final Bid bid) {
		final double jb = this.jbOpponentModel.calculateMeanUtility(bid);
		final double a17 = this.opponentModel.calculateMeanUtility(bid);
		return (jb + a17) / 2.0;
	}
}