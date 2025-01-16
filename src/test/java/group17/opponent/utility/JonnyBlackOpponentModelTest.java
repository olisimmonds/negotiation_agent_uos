package group17.opponent.utility;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import genius.core.issue.IssueDiscrete;
import group17.TestFramework;
import group17.mock.MockBid;
import group17.mock.MockDomain;
import group17.mock.MockIssueValue;

public class JonnyBlackOpponentModelTest extends TestFramework {
	
	private static final MathContext CONTEXT = new MathContext(3); // Round to 3DP when checking results. :-)
	private static final Logger LOG = LoggerFactory.getLogger(JonnyBlackOpponentModelTest.class);
	private static final int BID_COUNT = 10;
	private static final int RECENT_BIDS = 6;
		
	private MockDomain domain;
	private IssueDiscrete firstIssue;
	private IssueDiscrete secondIssue;
	
	private JonnyBlackOpponentModel opponentModel;

	@Before
	public void setup() {
		this.domain = new MockDomain();
		this.firstIssue = this.createMenuIssue(1);
		domain.addIssue(firstIssue);
		this.secondIssue = this.createSizeIssue(2);
		domain.addIssue(secondIssue);
		this.opponentModel = new JonnyBlackOpponentModel(domain);
	}
	
	@Test
	public void testCalculateUtilityForBid() {
		this.incrementIssueValueCountBy(MENU_ITEM, MENU_OPTION_1, 9);
		this.incrementIssueValueCountBy(MENU_ITEM, MENU_OPTION_2, 1);
		this.incrementIssueValueCountBy(ITEM_SIZE, SIZE_1, 3);
		this.incrementIssueValueCountBy(ITEM_SIZE, SIZE_2, 5);
		this.incrementIssueValueCountBy(ITEM_SIZE, SIZE_3, 2);
		this.opponentModel.setBidUpdates(BID_COUNT);
		this.opponentModel.recalculateModelEstimates();
		final MockBid bid = this.createBid();
		final double utility = this.opponentModel.calculateUtilityForBid(bid);
		this.checkExpectedAndActual(0.789, utility);
	}
	
	@Test
	public void testCalculateRecentUtilityForBidWhenWithinWindow() {
		this.incrementIssueValueCountBy(MENU_ITEM, MENU_OPTION_1, 9);
		this.incrementIssueValueCountBy(MENU_ITEM, MENU_OPTION_2, 1);
		this.incrementIssueValueCountBy(ITEM_SIZE, SIZE_1, 3);
		this.incrementIssueValueCountBy(ITEM_SIZE, SIZE_2, 5);
		this.incrementIssueValueCountBy(ITEM_SIZE, SIZE_3, 2);
		this.opponentModel.setBidUpdates(BID_COUNT);
		this.opponentModel.setRecentBidWindow(BID_COUNT);
		this.opponentModel.recalculateModelEstimates();
		final MockBid bid = this.createBid();
		final double utility = this.opponentModel.calculateRecentUtilityForBid(bid);
		this.checkExpectedAndActual(0.789, utility);
	}
	
	@Test
	public void testCalculateRecentUtilityForBidWhenOutsideWindow() {
		this.incrementIssueValueCountBy(MENU_ITEM, MENU_OPTION_1, 9);
		this.incrementIssueValueCountBy(MENU_ITEM, MENU_OPTION_2, 1);
		this.incrementIssueValueCountBy(ITEM_SIZE, SIZE_1, 3);
		this.incrementIssueValueCountBy(ITEM_SIZE, SIZE_2, 5);
		this.incrementIssueValueCountBy(ITEM_SIZE, SIZE_3, 2);
		// Now simulate removing some recent bids
		this.decrementRecentIssueValueCountBy(MENU_ITEM, MENU_OPTION_1, 4);
		this.decrementRecentIssueValueCountBy(ITEM_SIZE, SIZE_1, 2);
		this.decrementRecentIssueValueCountBy(ITEM_SIZE, SIZE_2, 2);
		this.opponentModel.setBidUpdates(BID_COUNT);
		this.opponentModel.setRecentBidWindow(RECENT_BIDS);
		this.opponentModel.recalculateModelEstimates();
		final MockBid bid = this.createBid();
		double utility = this.opponentModel.calculateUtilityForBid(bid);
		LOG.info("Calculated total utility = {}, expected = 0.789", utility);
		this.checkExpectedAndActual(0.789, utility);
		utility = this.opponentModel.calculateRecentUtilityForBid(bid);
		LOG.info("Calculated recent utility = {}, expected = 0.8833", utility);
		this.checkExpectedAndActual(0.8833, utility);
	}
	
	@Test
	public void testDecreaseRecentIssueCountsFromOldBid() {
		this.incrementIssueValueCountBy(MENU_ITEM, MENU_OPTION_1, 9);
		this.incrementIssueValueCountBy(MENU_ITEM, MENU_OPTION_2, 1);
		this.incrementIssueValueCountBy(ITEM_SIZE, SIZE_1, 3);
		this.incrementIssueValueCountBy(ITEM_SIZE, SIZE_2, 5);
		this.incrementIssueValueCountBy(ITEM_SIZE, SIZE_3, 2);
		final MockBid bid = this.createBid();
		this.opponentModel.decreaseRecentIssueCountsFromOldBid(bid);
		final Map<String, JonnyBlackOpponentIssue> issues = this.opponentModel.getIssues();
		final JonnyBlackOpponentIssue menuItems = issues.get(MENU_ITEM);
		final JonnyBlackOpponentIssue itemSizes = issues.get(ITEM_SIZE);
		final JonnyBlackOpponentValue menuOption1 = menuItems.getValue(MENU_OPTION_1);
		final JonnyBlackOpponentValue menuOption2 = menuItems.getValue(MENU_OPTION_2);
		final JonnyBlackOpponentValue itemSize1 = itemSizes.getValue(SIZE_1);
		final JonnyBlackOpponentValue itemSize2 = itemSizes.getValue(SIZE_2);
		final JonnyBlackOpponentValue itemSize3 = itemSizes.getValue(SIZE_3);
		assertEquals(9, menuOption1.getCount());
		assertEquals(1, menuOption2.getCount());
		assertEquals(3, itemSize1.getCount());
		assertEquals(5, itemSize2.getCount());
		assertEquals(2, itemSize3.getCount());
		assertEquals(8, menuOption1.getRecentCount());
		assertEquals(1, menuOption2.getRecentCount());
		assertEquals(3, itemSize1.getRecentCount());
		assertEquals(5, itemSize2.getRecentCount());
		assertEquals(1, itemSize3.getRecentCount());
	}
	
	@Test
	public void testFindOldBid() {
		this.opponentModel.setRecentBidWindow(10);
		final MockBid bid = this.createBid();
		final MockBid other = this.createOtherBid();
		this.opponentModel.update(bid, true);
		this.opponentModel.update(bid, true);
		for (int i = 0; i < 10; i++) {
			this.opponentModel.update(other, true);
		}
		assertEquals(12, this.opponentModel.getAllPreviousBids().size());
		MockBid oldBid = (MockBid) this.opponentModel.findOldBid();
		assertEquals(bid, oldBid);
	}
	
	private void incrementIssueValueCountBy(final String issueName, final String valueName, final int increment) {
		for (int i = 0; i < increment; i++) {
			this.opponentModel.updateIssue(issueName, valueName);
		}
	}
	
	private void decrementRecentIssueValueCountBy(final String issueName, final String valueName, final int decrement) {
		for (int i = 0; i < decrement; i++) {
			this.opponentModel.decrementIssue(issueName, valueName);
		}
	}
	
	private void checkExpectedAndActual(final double expected, final double actual) {
		final BigDecimal expectedDecimal = new BigDecimal(expected, CONTEXT);
		final BigDecimal actualDecimal = new BigDecimal(actual, CONTEXT);
		assertEquals(expectedDecimal, actualDecimal);
	}
	

	private MockBid createBid() {
		final List<MockIssueValue> values = new ArrayList<>();
		final MockIssueValue firstIssueValue = new MockIssueValue(this.firstIssue, MENU_OPTION_1);
		values.add(firstIssueValue);
		final MockIssueValue secondIssueValue = new MockIssueValue(this.secondIssue, SIZE_3);
		values.add(secondIssueValue);
		return MockBid.createBidWithRandomUtility(values);
	}
	
	private MockBid createOtherBid() {
		final List<MockIssueValue> values = new ArrayList<>();
		final MockIssueValue firstIssueValue = new MockIssueValue(this.firstIssue, MENU_OPTION_2);
		values.add(firstIssueValue);
		final MockIssueValue secondIssueValue = new MockIssueValue(this.secondIssue, SIZE_2);
		values.add(secondIssueValue);
		return MockBid.createBidWithRandomUtility(values);
	}
}