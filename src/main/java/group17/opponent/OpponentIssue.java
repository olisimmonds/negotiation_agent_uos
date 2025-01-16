package group17.opponent;

import java.util.Collection;

import genius.core.issue.Issue;
import group17.domain.DomainTranslator;

public abstract class OpponentIssue {

	protected final int issueNo;
	protected final String name;
	protected final int numValues;
	
	/**
	 * Construct an Opponent Issue based on an {@link Issue} in the current domain.
	 * @param issue Issue
	 */
	public OpponentIssue(final Issue issue) {
		this.issueNo = issue.getNumber();
		this.name = DomainTranslator.issueName(issue);	
		this.numValues = this.createOpponentValues(issue);
	}
	
	/**
	 * Construct an issue from primitive values. 
	 * Used for testing only.
	 * @param issueNo Issue Number
	 * @param issueName Issue Name
	 * @param valueNames List of Value Names
	 */
	protected OpponentIssue(final int issueNo, final String issueName, final String... valueNames) {
		this.issueNo = issueNo;
		this.name = issueName;
		this.numValues = this.createOpponentValues(valueNames);
	}
	
	/**
	 * Create Opponent Values from an {@link Issue}.
	 * @param issue Issue
	 * @return Number of values created
	 */
	protected abstract int createOpponentValues(final Issue issue);
	
	/**
	 * Create Opponent Values from an array of names.
	 * @param valueNames names
	 * @return Number of values created
	 */
	protected abstract int createOpponentValues(final String... valueNames);
	
	public int getIssueNo() {
		return issueNo;
	}

	public String getName() {
		return name;
	}
	
	public int getNumValues() {
		return numValues;
	}
	
	/**
	 * Get the collection of opponent values.
	 * @return Opponent values
	 */
	protected abstract Collection<OpponentValue> getValues();
	
	@Override
	public String toString() {
		return new StringBuilder(this.name)
			.append(", values=")
			.append(this.getValues())
			.toString();
	}
}
