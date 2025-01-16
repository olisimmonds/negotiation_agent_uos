package group17.mock;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.ValueDiscrete;
import group17.TestFramework;

/**
 * Unit tests for {@link MockBid}.
 */
public class MockBidTest extends TestFramework {
	
	private MockBid bid;
	private IssueDiscrete firstIssue;
	private IssueDiscrete secondIssue;
	
	@Before
	public void setup() {
		this.firstIssue = this.createMenuIssue(1);
		this.secondIssue = this.createSizeIssue(2);	
		this.bid = this.createBidWithRandomUtility(MENU_OPTION_3, SIZE_3);
	}
	
	@Test
	public void testGetIssues() {
		final List<Issue> issues = this.bid.getIssues();
		assertEquals(2, issues.size());
		assertEquals(this.firstIssue.getName(), issues.get(0).getName());
		assertEquals(this.secondIssue.getName(), issues.get(1).getName());
	}
	
	@Test
	public void testGetValue() {
		ValueDiscrete value = (ValueDiscrete) this.bid.getValue(this.firstIssue);
		assertEquals(MENU_OPTION_3, value.getValue());
		value = (ValueDiscrete) this.bid.getValue(this.secondIssue);
		assertEquals(SIZE_3, value.getValue());
	}
	
	@Test
	public void testEqualsWithSameUtility() {
		final double utility = this.bid.getUtility();
		final MockBid other = this.createBidWithUtility(MENU_OPTION_3, SIZE_3, utility);
		assertEquals(bid, other);
	}
	
	@Test
	public void testEqualsWithDifferentUtility() {
		final double utility = this.bid.getUtility() - 0.01;
		final MockBid other = this.createBidWithUtility(MENU_OPTION_3, SIZE_3, utility);
		assertNotEquals(bid, other);
	}
}
