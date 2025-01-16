package group17.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import genius.core.issue.IssueDiscrete;
import genius.core.issue.IssueInteger;
import genius.core.issue.IssueReal;
import genius.core.issue.ValueDiscrete;
import genius.core.issue.ValueInteger;
import genius.core.issue.ValueReal;
import group17.TestFramework;
import group17.domain.DomainTranslator;

/**
 * Test cases for {@link DomainTranslator}.
 */
public class DomainTranslatorTest extends TestFramework {
	
	@Test
	public void testGetIssueDiscreteName() {
		final IssueDiscrete issue = this.createMenuIssue(1);
		final String name = DomainTranslator.issueName(issue);
		assertEquals(MENU_ITEM, name);
	}
	
	@Test
	public void testGetIssueIntegerName() {
		final IssueInteger issue = this.createIssueInteger(1);
		final String name = DomainTranslator.issueName(issue);
		assertEquals("integer", name);
	}
	
	@Test
	public void testGetIssueRealName() {
		final IssueReal issue = this.createIssueReal(1);
		final String name = DomainTranslator.issueName(issue);
		assertEquals("real", name);
	}
	
	@Test
	public void testGetValueDiscreteName() {
		final ValueDiscrete value = new ValueDiscrete(MENU_OPTION_2);
		final String name = DomainTranslator.valueName(value);
		assertEquals(MENU_OPTION_2, name);
	}
	
	@Test
	public void testGetValueIntegerName() {
		final ValueInteger value = new ValueInteger(10);
		final String name = DomainTranslator.valueName(value);
		assertEquals("10", name);
	}
	
	@Test
	public void testGetValueRealName() {
		final ValueReal value = new ValueReal(10.0);
		final String name = DomainTranslator.valueName(value);
		assertEquals("10.0", name);
	}
}
