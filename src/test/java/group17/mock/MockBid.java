package group17.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.Value;

/**
 * Integration Test implementation of {@link Bid}.
 * Constructing a valid Bid is complicated. This implementation forces all of the methods we 
 * rely on to return the correct values.
 * @author Martin Ingram
 */
public class MockBid extends Bid {
	
	private static final long serialVersionUID = 1L;
	private static final Random RANDOM = new Random();
	
	private Map<String, MockIssueValue> issueValueMap = new HashMap<>();
	private List<Issue> issues = new ArrayList<>();
	private double utility;

	/**
	 * Create a {@link MockBid} with a random utility.
	 * @param issueValues Issue Values
	 * @return Mock bid with a random utility
	 */
	public static MockBid createBidWithRandomUtility(final List<MockIssueValue> issueValues) {
		final double utility = RANDOM.nextDouble(1.0);
		return new MockBid(issueValues, utility);
	}
	
	/**
	 * Create a {@link MockBid} with an assigned utility.
	 * @param issueValues Issue Values
	 * @return Mock bid with a random utility
	 */
	public static MockBid createBidWithUtility(final List<MockIssueValue> issueValues, final double utility) {
		return new MockBid(issueValues, utility);
	}
	
	/**
	 * Create a mock bid.
	 * @param issueValues Issue Values
	 * @param utility Utility
	 */
	public MockBid(final List<MockIssueValue> issueValues, final double utility) {
		super((Domain) null); // This is safe as this constructor doesn't check for nulls.
		this.utility = utility;
		for (final MockIssueValue issueValue : issueValues) {
			final Issue issue = issueValue.getIssue();
			this.issues.add(issue);
			final String issueName = issue.getName();
			issueValueMap.put(issueName, issueValue);
		}
	}
	
	@Override
	public List<Issue> getIssues() {
		return this.issues;
	}
	
	public double getUtility() {
		return this.utility;
	}
	
	@Override
	public Value getValue(Issue issue) {
		final String issueName = issue.getName();
		final MockIssueValue issueValue = this.issueValueMap.get(issueName);
		return issueValue.getValue();
	}
	
	@Override
	public boolean equals(Object o) {
		boolean result = super.equals(o);
		if (o instanceof MockBid) {
			final MockBid other = (MockBid) o;
			result = result && (other.utility == this.utility);
		}
		return result;
	}
	
	@Override
	public String toString() {
		return new StringBuilder("MockBid ")
		   .append(this.issueValueMap.values())
		   .append(" ")
		   .append(this.utility)
		   .toString();
	}
}
