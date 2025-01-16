package group17;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import genius.core.Bid;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.IssueInteger;
import genius.core.issue.IssueReal;
import group17.mock.MockBid;
import group17.mock.MockBidRanking;
import group17.mock.MockDomain;
import group17.mock.MockIssueValue;

/**
 * Test framework that sets up features used by every test.
 * @author Martin Ingram
 */
public abstract class TestFramework {
	
	protected static final String MENU_ITEM = "Menu Item";
	protected static final String MENU_OPTION_1 = "Fish";
	protected static final String MENU_OPTION_2 = "Chips";
	protected static final String MENU_OPTION_3 = "Sausage";
	protected static final String MENU_OPTION_4 = "Pie";
	protected static final String[] MENU_OPTIONS = { MENU_OPTION_1, MENU_OPTION_2, MENU_OPTION_3, MENU_OPTION_4 };
	
	protected static final String ITEM_SIZE = "Item Size";
	protected static final String SIZE_1 = "Small";
	protected static final String SIZE_2 = "Medium";
	protected static final String SIZE_3 = "Large";
	protected static final String[] SIZE_OPTIONS = { SIZE_1, SIZE_2, SIZE_3 };
	
	protected static final String INTEGER_PREFIX = "Integer ";
	protected static final String REAL_PREFIX = "Real ";
	
	protected static final Random RANDOM = new Random();
	
	/**
	 * Create an issue based on takeaway menu items.
	 * @param issueNo Issue number
	 * @return Menu item issue
	 */
	protected IssueDiscrete createMenuIssue(final int issueNo) {
		return new IssueDiscrete(MENU_ITEM, issueNo, MENU_OPTIONS);
	}
	
	/**
	 * Create an issue based on takeaway menu sizes.
	 * @param issueNo Issue number
	 * @return Size issue
	 */
	protected IssueDiscrete createSizeIssue(final int issueNo) {
		return new IssueDiscrete(ITEM_SIZE, issueNo, SIZE_OPTIONS);
	}
	
	/**
	 * Create an integer based issue.
	 * @param issueNo Issue number
	 * @return Integer issue
	 */	
	protected IssueInteger createIssueInteger(final int issueNo) {
		return new IssueInteger(INTEGER_PREFIX + issueNo, issueNo, 1, 10);
	}
	
	/**
	 * Create an real based issue.
	 * @param issueNo Issue number
	 * @return Real issue
	 */	
	protected IssueReal createIssueReal(final int issueNo) {
		return new IssueReal(REAL_PREFIX + issueNo, issueNo, 1.0, 10.0);
	}
	
	/**
	 * Create a {@link MockBid} with random utility.
	 * @param menuOption Menu option
	 * @param sizeOption Size option
	 * @return Mock bid with random utility.
	 */
	protected MockBid createBidWithRandomUtility(final String menuOption, final String sizeOption) {
		final List<MockIssueValue> issueValues = this.createIssueValues(menuOption, sizeOption);
		return MockBid.createBidWithRandomUtility(issueValues);
	}
	
	
	/**
	 * Create a default {@link MockDomain} with both issues.
	 * @return Mock Domain
	 */
	protected MockDomain createMockDomainWithIssues() {
		final MockDomain domain = new MockDomain();
		final IssueDiscrete menuIssue = this.createMenuIssue(1);
		domain.addIssue(menuIssue);
		final IssueDiscrete sizeIssue = this.createSizeIssue(2);
		domain.addIssue(sizeIssue);	
		return domain;
	}
	
	/**
	 * Create a {@link MockBid} with an assigned utility.
	 * @param menuOption Menu option
	 * @param sizeOption Size option
	 * @return Mock bid with assigned utility.
	 */
	protected MockBid createBidWithUtility(final String menuOption, final String sizeOption, final double utility) {
		final List<MockIssueValue> issueValues = this.createIssueValues(menuOption, sizeOption);
		return MockBid.createBidWithUtility(issueValues, utility);
	}
	
	/**
	 * Create a mock {@link BidRanking}.
	 * @param numBids Number of bids in the ranking
	 * @param utilityIncrement Utility increment for each bid
	 * @return Mock bid ranking
	 */
	protected MockBidRanking createBidRanking(final int numBids, final double utilityIncrement) {
		final List<Bid> bids = new ArrayList<>();
		double utility = 0.0;
		for (int i = 0; i < numBids; i++) {
			final MockBid bid = this.createBidWithUtility(this.randomMenuItem(), this.randomSize(), utility);
			bids.add(bid);
			utility += utilityIncrement;
		}
		return new MockBidRanking(bids);
	}
	
	private List<MockIssueValue> createIssueValues(final String menuOption, final String sizeOption) {
		final List<MockIssueValue> issueValues = new ArrayList<>();
		final IssueDiscrete firstIssue = this.createMenuIssue(1);
		final MockIssueValue firstIssueValue = new MockIssueValue(firstIssue, menuOption);
		issueValues.add(firstIssueValue);
		final IssueDiscrete secondIssue = this.createSizeIssue(2);	
		final MockIssueValue secondIssueValue = new MockIssueValue(secondIssue, sizeOption);
		issueValues.add(secondIssueValue);
		return issueValues;
	}
	
	private String randomMenuItem() {
		final int selection = RANDOM.nextInt(4);
		return MENU_OPTIONS[selection];
	}
	
	private String randomSize() {
		final int selection = RANDOM.nextInt(3);
		return SIZE_OPTIONS[selection];
	}
}
