package group17.mock;

import genius.core.issue.IssueDiscrete;
import genius.core.issue.ValueDiscrete;
import group17.TestFramework;

import static org.junit.Assert.*;

import org.junit.Test;

import genius.core.analysis.pareto.IssueValue;

/**
 * Test cases for {@link MockIssueValue}.
 */
public class MockIssueValueTest extends TestFramework {
	
	@Test
	public void testGetValue() {
		final IssueDiscrete issue = this.createMenuIssue(1);
		final IssueValue issueValue = new MockIssueValue(issue, MENU_OPTION_3);
		final ValueDiscrete value = (ValueDiscrete) issueValue.getValue();
		assertEquals(MENU_OPTION_3, value.getValue());
	}
}
