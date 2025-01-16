package group17.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import genius.core.Bid;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;

/**
 * A flattened (string) representation of a {@link Bid}, plus some simple analytics.
 * @author Martin Ingram
 */
public class BidValue {
	
	public static final String AGENT = "Agent";
	public static final String OPPONENT = "Opponent";
	public static final String UNKNOWN = "Bid";
	
	private double utility = 0.0;
	private String type = UNKNOWN;
	private Map<String, String> values = new HashMap<>();

	
	/**
	 * Create an Agent Bid Value with a specified utility.
	 * @param bid Bid
	 * @param utility Utility
	 * @return Agent Bid Value
	 */
	public static BidValue createAgentBidValue(final Bid bid, final double utility) {
		return new BidValue(bid, AGENT, utility);
	}
	
	/**
	 * Create an Opponent Bid Value with a specified utility.
	 * @param bid Bid
	 * @param utility Utility
	 * @return Opponent Bid Value
	 */
	public static BidValue createOpponentBidValue(final Bid bid, final double utility) {
		return new BidValue(bid, OPPONENT, utility);
	}
	
	/**
	 * Analyse a {@link Bid}.
	 * @param bid The bid to analyse.
	 */
	public BidValue(final Bid bid) {
		if (bid != null) {
			this.addIssueValuesToMap(bid);
		}
	}
	
	private BidValue(final Bid bid, final String type, final double utility) {
		this(bid);
		this.type = type;
		this.utility = utility;
	}
	
	protected double getUtility() {
		return this.utility;
	}
	
	/**
	 * Does this instance include the specified issue / value pair?
	 * @param issue Issue name
	 * @param value Value name
	 * @return true = yes
	 */
	public boolean includesIssueValue(final String issue, final String value) {
		return (issue != null && value != null && value.equals(this.values.get(issue)));
	}
	
	/**
	 * Does another Bid Value have a matching value for a given issue?
	 * @param issue Issue
	 * @param other The other Bid Value
	 * @return true = yes
	 */
	public boolean issueMatch(final String issue, BidValue other) {
		boolean result = (issue != null && other != null);
		if (result) {
			final String value = this.values.get(issue);
			result = other.includesIssueValue(issue, value);
		}
		return result;
	}

	/**
	 * Iterate through the list of issues in the bid and add any instances of {@link IssueDiscrete} to the map.
	 * @param bid Bid
	 */
	private void addIssueValuesToMap(final Bid bid) {
		final List<Issue> issues = bid.getIssues();
		if (issues != null) {
			for (final Issue issue : issues) {
				if (issue != null) {
					final Value value = bid.getValue(issue);
					final String issueName = DomainTranslator.issueName(issue);
					final String valueName = DomainTranslator.valueName(value);
					values.put(issueName, valueName);
				}
			}
		}
	}
	
	/**
	 * Find all issue / value pairs in common between two Bid Values.
	 * @param other The other Bid Value
	 * @return A map of all issue / value pairs that match
	 */
	public Map<String, String> matches(final BidValue other) {
		final HashMap<String, String> issueValues = new HashMap<>();
		if (other != null) {
			for (final String issue : this.values.keySet()) {
				final String value = this.values.get(issue);
				if (other.includesIssueValue(issue, value)) {
					issueValues.put(issue, value);
				}
			}		
		}
		return issueValues;
	}
	
	/**
	 * Which issues have the same values?
	 * @param other The other Bid Value
	 * @return A set of all issue names that match, sorted alphabetically.
	 */
	public SortedSet<String> issuesThatMatch(final BidValue other) {
		return new TreeSet<>(this.matches(other).keySet());
	}
	
	/**
	 * Bid values are equal if each issue / value pair matches.
	 * @param o Another object
	 */
	@Override
	public boolean equals(Object o) {
		boolean result = (o instanceof BidValue);
		if (result) {
			BidValue other = (BidValue) o;
			for (final String issue : this.values.keySet()) {
				final String value = this.values.get(issue);
				result = other.includesIssueValue(issue, value);
				if (!result) {
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * Hash code is based on the issue / value pairs.
	 */
	public int hashCode() {
		return this.values.hashCode();
	}
	
	public String toStringLabels() {
		final StringBuilder builder = new StringBuilder("Type");
		for (final String issue : this.sortedIssues()) {
			builder.append(",").append(this.quoted(issue));
		}
		builder.append(",").append("Utility");
		return builder.toString();	
	}
	
	/**
	 * Generates a CSV representation of this Bid Value.
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder(this.type);
		for (final String issue : this.sortedIssues()) {
			final String value = this.values.get(issue);
			builder.append(",").append(this.quoted(value));
		}
		builder.append(",").append(this.utility);
		return builder.toString();
	}
	
	private TreeSet<String> sortedIssues() {
		return new TreeSet<>(this.values.keySet());
	}
	
	/**
	 * Wrap a string in quotes so it's CSV compliant.
	 * @param string String
	 * @return Quoted string
	 */
	private String quoted(final String string) {
		return "\"" + string + "\"";
	}
}
