package group17.mock;

import genius.core.analysis.pareto.IssueValue;
import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import genius.core.issue.ValueDiscrete;

/**
 * Integration test implementation of {@link IssueValue}.
 * Note that this returns utilities of zero.
 */
public class MockIssueValue implements IssueValue {
	
	private IssueDiscrete issue;
	private ValueDiscrete value;
	
	public MockIssueValue(final IssueDiscrete issue, final String valueName) {
		this.issue = issue;
		final int index = issue.getValueIndex(valueName);
		this.value = issue.getValue(index);
	}

	@Override
	public Issue getIssue() {
		return this.issue;
	}

	@Override
	public Value getValue() {
		return this.value;
	}

	@Override
	public Double getUtilityA() {
		return 0.0;
	}

	@Override
	public Double getUtilityB() {
		return 0.0;
	}
	
	@Override
	public String toString() {
		return new StringBuilder(this.issue.getName())
			.append(", ")
			.append(this.value.getValue())
			.toString();
	}
}
