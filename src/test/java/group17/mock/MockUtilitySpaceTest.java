package group17.mock;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import genius.core.Domain;
import group17.TestFramework;

public class MockUtilitySpaceTest extends TestFramework {

	private MockUtilitySpace space;
	
	@Before
	public void setup() {
		final MockDomain domain = this.createMockDomainWithIssues();
		this.space = new MockUtilitySpace(domain);
	}
	
	@Test
	public void testGetDomain() {
		final Domain domain = this.space.getDomain();
		assertTrue(domain instanceof MockDomain);
	}
	
	@Test
	public void testGetUtility() {
		final Double expected = Double.valueOf(0.5);
		final MockBid bid = this.createBidWithUtility(MENU_OPTION_2, SIZE_2, expected);
		final Double actual = this.space.getUtility(bid);
		assertEquals(expected, actual);
	}
}
