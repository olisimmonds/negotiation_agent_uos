package group17.domain;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import genius.core.AgentID;
import genius.core.Bid;
import genius.core.Deadline;
import genius.core.parties.NegotiationInfo;
import genius.core.persistent.PersistentDataContainer;
import genius.core.timeline.TimeLineInfo;
import genius.core.uncertainty.User;
import genius.core.uncertainty.UserModel;
import genius.core.utility.AbstractUtilitySpace;
import group17.TestFramework;
import group17.mock.MockBidRanking;
import group17.mock.MockDomain;
import group17.mock.MockPersistentDataContainer;
import group17.mock.MockTimeLineInfo;
import group17.mock.MockUtilitySpace;

public class BidSelectorTest extends TestFramework {
	
	 private MockUtilitySpace space;
	 private UserModel userModel;
	
	 @Before
	 public void setup() {
		 final MockBidRanking ranking = this.createBidRanking(20, 0.05);
		 this.userModel = new UserModel(ranking);
		 final MockDomain domain = this.createMockDomainWithIssues();
		 this.space = new MockUtilitySpace(domain);
	 }
	
	 @Test
	 public void testBidSelectorWithUserModel() {
		 final NegotiationInfo info = this.createInfo(this.space, this.userModel);
		 final BidSelector selector = new BidSelector(info, 10, 0.5);
		 final List<Bid> bids = selector.getBids();
		 assertEquals(9, bids.size());
	 }
	 
	 @Test
	 public void testExpandListWithUserModel() {
		 final NegotiationInfo info = this.createInfo(this.space, this.userModel);
		 final BidSelector selector = new BidSelector(info, 10, 0.5);
		 selector.expandList(5, 0.4);
		 final List<Bid> bids = selector.getBids();
		 assertEquals(11, bids.size());
	 }
	 
	 @Test
	 public void testBidSelectorWithUserSpace() {
		 final NegotiationInfo info = this.createInfo(this.space, null);
		 final BidSelector selector = new BidSelector(info, 10, 0.5);
		 final List<Bid> bids = selector.getBids();
		 assertEquals(10, bids.size());
	 }
	 
	 @Test
	 public void testExpandListWithUserSpace() {
		 final NegotiationInfo info = this.createInfo(this.space, null);
		 final BidSelector selector = new BidSelector(info, 10, 0.5);
		 selector.expandList(5, 0.4);
		 final List<Bid> bids = selector.getBids();
		 assertEquals(12, bids.size());
	 }
	 
	 private NegotiationInfo createInfo(final AbstractUtilitySpace utilitySpace, final UserModel userModel) {
		 final User user = new User(null);
		 final Deadline deadline = new Deadline();
		 final TimeLineInfo timeline = new MockTimeLineInfo();
		 final AgentID agent = new AgentID("Unit Test");
		 final PersistentDataContainer container = new MockPersistentDataContainer();
		 return new NegotiationInfo(utilitySpace, userModel, user, deadline, timeline, 0L, agent, container);
	 }
}
