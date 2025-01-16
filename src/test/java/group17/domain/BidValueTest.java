package group17.domain;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.SortedSet;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import group17.TestFramework;
import group17.mock.MockBid;

/**
 * Unit tests for {@link BidValue}.
 * @author Martin Ingram
 */
public class BidValueTest extends TestFramework {
	
	private static Logger LOG = LoggerFactory.getLogger(BidValueTest.class);
	
	private MockBid bid;
	private BidValue bidValue;
	
	@Before
	public void setup() {
		this.bid = this.createBidWithRandomUtility(MENU_OPTION_1, SIZE_1);
		this.bidValue = new BidValue(this.bid);
	}
	
	@Test
	public void testIncludesIssueValue() {
		assertTrue(this.bidValue.includesIssueValue(MENU_ITEM, MENU_OPTION_1));
		assertTrue(this.bidValue.includesIssueValue(ITEM_SIZE, SIZE_1));
		assertFalse(this.bidValue.includesIssueValue(MENU_ITEM, MENU_OPTION_2));
		assertFalse(this.bidValue.includesIssueValue(ITEM_SIZE, SIZE_3));
		assertFalse(this.bidValue.includesIssueValue("Random Issue", "Random Value"));
		assertFalse(this.bidValue.includesIssueValue(null, MENU_OPTION_1));
		assertFalse(this.bidValue.includesIssueValue(MENU_ITEM, null));
	}
	
	@Test
	public void testEqualsAndHashCodeForIdenticalBids() {
		final BidValue other = new BidValue(this.bid);
		assertEquals(this.bidValue, other);
		assertEquals(this.bidValue.hashCode(), other.hashCode());
	}
	
	@Test
	public void testEqualsAndHashCodeForTheSameBid() {
		final MockBid otherBid = this.createBidWithRandomUtility(MENU_OPTION_1, SIZE_1);
		final BidValue other = new BidValue(otherBid);
		assertEquals(this.bidValue, other);
		assertEquals(this.bidValue.hashCode(), other.hashCode());
	}
	
	@Test
	public void testEqualsAndHashCodeForDifferentBid() {
		final MockBid otherBid = this.createBidWithRandomUtility(MENU_OPTION_2, SIZE_1);
		final BidValue other = new BidValue(otherBid);
		assertNotEquals(this.bidValue, other);
		assertNotEquals(this.bidValue.hashCode(), other.hashCode());
	}
	
	@Test
	public void testEqualsWithNull() {
		final BidValue other = null;
		assertNotEquals(this.bidValue, other);
	}
	
	@Test
	public void testEqualsWithAnotherType() {
		final MockBid other = this.createBidWithRandomUtility(MENU_OPTION_2, SIZE_1);
		assertNotEquals(this.bidValue, other);
	}
	
	@Test
	public void testMatchesWithTheSameBid() {
		final BidValue other = new BidValue(this.bid);
		final Map<String, String> matches = this.bidValue.matches(other);
		assertEquals(2, matches.size());
		assertEquals(MENU_OPTION_1, matches.get(MENU_ITEM));
		assertEquals(SIZE_1, matches.get(ITEM_SIZE));
	}
	
	@Test
	public void testMatchesWithOneValueTheSame() {
		final MockBid otherBid = this.createBidWithRandomUtility(MENU_OPTION_1, SIZE_2);
		final BidValue other = new BidValue(otherBid);
		final Map<String, String> matches = this.bidValue.matches(other);
		assertEquals(1, matches.size());
		assertEquals(MENU_OPTION_1, matches.get(MENU_ITEM));
	}
	
	@Test
	public void testMatchesWithNoValuesTheSame() {
		final MockBid otherBid = this.createBidWithRandomUtility(MENU_OPTION_2, SIZE_2);
		final BidValue other = new BidValue(otherBid);
		final Map<String, String> matches = this.bidValue.matches(other);
		assertTrue(matches.isEmpty());
	}
	
	@Test
	public void testIssuesThatMatchWithTheSameBid() {
		final BidValue other = new BidValue(this.bid);
		final SortedSet<String> issues = this.bidValue.issuesThatMatch(other);
		assertEquals(2, issues.size());
		assertTrue(issues.contains(MENU_ITEM));
		assertTrue(issues.contains(ITEM_SIZE));
	}
	
	@Test
	public void testIssuesThatMatchWithOneValueTheSame() {
		final MockBid otherBid = this.createBidWithRandomUtility(MENU_OPTION_1, SIZE_2);
		final BidValue other = new BidValue(otherBid);
		final SortedSet<String> issues = this.bidValue.issuesThatMatch(other);
		assertEquals(1, issues.size());
		assertTrue(issues.contains(MENU_ITEM));
	}
	
	@Test
	public void testIssuesThatMatchWithNoValuesTheSame() {
		final MockBid otherBid = this.createBidWithRandomUtility(MENU_OPTION_2, SIZE_2);
		final BidValue other = new BidValue(otherBid);
		final SortedSet<String> issues = this.bidValue.issuesThatMatch(other);
		assertTrue(issues.isEmpty());
	}
	
	@Test
	public void testIssueMatchWithTheSameBid() {
		final BidValue other = new BidValue(this.bid);
		assertTrue(this.bidValue.issueMatch(MENU_ITEM, other));
		assertTrue(this.bidValue.issueMatch(ITEM_SIZE, other));
	}
	
	@Test
	public void testIssueMatchWithOneValueTheSame() {
		final MockBid otherBid = this.createBidWithRandomUtility(MENU_OPTION_1, SIZE_2);
		final BidValue other = new BidValue(otherBid);
		assertTrue(this.bidValue.issueMatch(MENU_ITEM, other));
		assertFalse(this.bidValue.issueMatch(ITEM_SIZE, other));
	}
	
	@Test
	public void testIssueMatchWithNoValuesTheSame() {
		final MockBid otherBid = this.createBidWithRandomUtility(MENU_OPTION_2, SIZE_2);
		final BidValue other = new BidValue(otherBid);
		assertFalse(this.bidValue.issueMatch(MENU_ITEM, other));
		assertFalse(this.bidValue.issueMatch(ITEM_SIZE, other));
	}
	
	@Test
	public void testToStringLabels() {
		final String expected = "Type," + this.quoted(ITEM_SIZE) + "," + this.quoted(MENU_ITEM) + ",Utility";
		final String labels = this.bidValue.toStringLabels();
		LOG.info("{}", labels);
		assertEquals(expected, labels);
	}
	
	@Test
	public void testToStringWithGenericBid() {
		final String expected = BidValue.UNKNOWN + "," + this.quoted(SIZE_1) + "," + this.quoted(MENU_OPTION_1) + ",0.0";
		final String string = this.bidValue.toString();
		LOG.info("{}", string);		
		assertEquals(expected, string);
	}
	
	@Test
	public void testToStringWithAgentBid() {
		final double utility = this.bid.getUtility();
		final BidValue agent = BidValue.createAgentBidValue(this.bid, utility);
		final String expected = BidValue.AGENT + "," + this.quoted(SIZE_1) + "," + this.quoted(MENU_OPTION_1) + "," + utility;
		final String string = agent.toString();
		LOG.info("{}", string);		
		assertEquals(expected, string);
	}
	
	@Test
	public void testToStringWithOpponentBid() {
		final double utility = this.bid.getUtility();
		final BidValue agent = BidValue.createOpponentBidValue(this.bid, utility);
		final String expected = BidValue.OPPONENT + "," + this.quoted(SIZE_1) + "," + this.quoted(MENU_OPTION_1) + "," + utility;
		final String string = agent.toString();
		LOG.info("{}", string);		
		assertEquals(expected, string);
	}
	
	private String quoted(final String string) {
		return "\"" + string + "\"";
	}
}
