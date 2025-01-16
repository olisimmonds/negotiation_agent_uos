package group17.mock;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import group17.TestFramework;

/**
 * Unit tests for {@link MockDomain}.
 * @author Martin Ingram
 */
public class MockDomainTest extends TestFramework {
	
	private MockDomain domain;
	
	@Before
	public void setup() {
		this.domain = new MockDomain();
	}
	
	/**
	 * Genius populates lists and arrays from index 1 because reasons?
	 */
	@Test
	public void testInitialDomain() {
		final List<Issue> issues = this.domain.getIssues();
		assertEquals(1, issues.size());
		assertNull(issues.get(0));
	}

	/**
	 * Test that issues that are added appear in the list of issues.
	 * Note that Genius doesn't implement equals for {@link Issue} objects, 
	 * so this relies on unique object identity.
	 */
	@Test
	public void testAddIssue() {
		final IssueDiscrete menuIssue = this.createMenuIssue(1);
		this.domain.addIssue(menuIssue);
		final IssueDiscrete sizeIssue = this.createSizeIssue(2);
		this.domain.addIssue(sizeIssue);
		final List<Issue> issues = this.domain.getIssues();
		assertEquals(3, issues.size());
		assertTrue(issues.contains(menuIssue));
		assertTrue(issues.contains(sizeIssue));
	}
}
