package group17.opponent.utility;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.MathContext;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Agent17OpponentIssueTest {
		
	private static final MathContext CONTEXT = new MathContext(3); // Round to 3DP when checking results. :-)
	private static final Logger LOG = LoggerFactory.getLogger(Agent17OpponentIssueTest.class);
	private static final int PRIOR_BIDS = 10;
	
	private Agent17OpponentIssue firstIssue; 
	private Agent17OpponentIssue secondIssue;

	@Before
	public void setup() {
		this.firstIssue = new Agent17OpponentIssue(1, "Menu Item", "Fish", "Chips", "Sausage", "Pie");
		this.secondIssue = new Agent17OpponentIssue(2, "Item Size", "Small", "Medium", "Large");
	}
	
	@Test
	public void testUpdatePreferenceValues() {
		this.incrementValueCountBy(this.firstIssue, "Fish", 1);
		this.incrementValueCountBy(this.firstIssue, "Chips", 3);
		this.incrementValueCountBy(this.firstIssue, "Sausage", 4);
		this.incrementValueCountBy(this.firstIssue, "Pie", 2);
		this.firstIssue.updatePreferenceValues(PRIOR_BIDS, PRIOR_BIDS);
		assertTrue(this.firstIssue.getValue("Fish").getPreferenceValue() == 0.1); 
		assertTrue(this.firstIssue.getValue("Chips").getPreferenceValue() == 0.3);
		assertTrue(this.firstIssue.getValue("Sausage").getPreferenceValue() == 0.4);
		assertTrue(this.firstIssue.getValue("Pie").getPreferenceValue() == 0.2);
	}
	
	@Test
	public void testUpdateRecentPreferenceValues() {
		this.incrementValueCountBy(this.firstIssue, "Fish", 1);
		this.incrementValueCountBy(this.firstIssue, "Chips", 2);
		this.incrementValueCountBy(this.firstIssue, "Sausage", 5);
		this.incrementValueCountBy(this.firstIssue, "Pie", 2);
		this.decrementValueRecentCountBy(this.firstIssue, "Sausage", 2);
		this.firstIssue.updatePreferenceValues(PRIOR_BIDS, PRIOR_BIDS - 2);
		assertTrue(this.firstIssue.getValue("Fish").getPreferenceValue() == 0.1); 
		assertTrue(this.firstIssue.getValue("Chips").getPreferenceValue() == 0.2);
		assertTrue(this.firstIssue.getValue("Sausage").getPreferenceValue() == 0.5);
		assertTrue(this.firstIssue.getValue("Pie").getPreferenceValue() == 0.2);
		assertTrue(this.firstIssue.getValue("Fish").getRecentPreferenceValue() == 0.125); 
		assertTrue(this.firstIssue.getValue("Chips").getRecentPreferenceValue() == 0.25);
		assertTrue(this.firstIssue.getValue("Sausage").getRecentPreferenceValue() == 0.375);
		assertTrue(this.firstIssue.getValue("Pie").getRecentPreferenceValue() == 0.25);
	}
	
	@Test
	public void testDecrementValueRecentCount() {
		this.incrementValueCountBy(this.firstIssue, "Fish", 1);
		this.incrementValueCountBy(this.firstIssue, "Chips", 3);
		this.incrementValueCountBy(this.firstIssue, "Sausage", 5);
		this.incrementValueCountBy(this.firstIssue, "Pie", 2);
		this.decrementValueRecentCountBy(this.firstIssue, "Sausage", 1);
		assertEquals(1, this.firstIssue.getValue("Fish").getCount());
		assertEquals(3, this.firstIssue.getValue("Chips").getCount());
		assertEquals(5, this.firstIssue.getValue("Sausage").getCount());
		assertEquals(2, this.firstIssue.getValue("Pie").getCount());
		assertEquals(1, this.firstIssue.getValue("Fish").getRecentCount());
		assertEquals(3, this.firstIssue.getValue("Chips").getRecentCount());
		assertEquals(4, this.firstIssue.getValue("Sausage").getRecentCount());
		assertEquals(2, this.firstIssue.getValue("Pie").getRecentCount());
	}
	
	/**
	 * Test unnormalized weight calculation as per the first example in Johnny Black 2.3.3.
	 */
	@Test
	public void testCalculateUnnormalizedWeightW1() {
		this.incrementValueCountBy(this.firstIssue, "Fish", 9);
		this.incrementValueCountBy(this.firstIssue, "Chips", 1);
		final double actual = this.firstIssue.calculateUnnormalizedWeight(PRIOR_BIDS);
		final double expected = 82.0 / 100.0;
		final double persisted = this.firstIssue.getUnnormalizedWeight();
		this.checkActualExpectedPersisted(actual, expected, persisted);
	}
	
	/**
	 * Test unnormalized weight calculation as per the first example in Johnny Black 2.3.3.
	 */
	@Test
	public void testCalculateUnnormalizedRecentWeightW1() {
		this.incrementValueCountBy(this.firstIssue, "Fish", 9);
		this.incrementValueCountBy(this.firstIssue, "Chips", 1);
		final double actual = this.firstIssue.calculateUnnormalizedRecentWeight(PRIOR_BIDS);
		final double expected = 82.0 / 100.0;
		final double persisted = this.firstIssue.getUnnormalizedRecentWeight();
		this.checkActualExpectedPersisted(actual, expected, persisted);
	}
	
	/**
	 * Test unnormalized weight calculation as per the first example in Johnny Black 2.3.3.
	 */
	@Test
	public void testCalculateUnnormalizedRecentWeightW2() {
		this.incrementValueCountBy(this.firstIssue, "Fish", 3);
		this.incrementValueCountBy(this.firstIssue, "Chips", 5);
		this.incrementValueCountBy(this.firstIssue, "Sausage", 2);
		final double actual = this.firstIssue.calculateUnnormalizedRecentWeight(PRIOR_BIDS);
		final double expected = 38.0 / 100.0;
		final double persisted = this.firstIssue.getUnnormalizedRecentWeight();
		this.checkActualExpectedPersisted(actual, expected, persisted);
	}
	
	/**
	 * Test unnormalized weight calculations combined.
	 */
	@Test
	public void testCalculateUnnormalizedWeightsCombined() {
		this.incrementValueCountBy(this.firstIssue, "Fish", 3);
		this.incrementValueCountBy(this.firstIssue, "Chips", 5);
		this.incrementValueCountBy(this.firstIssue, "Sausage", 2);
		this.decrementValueRecentCountBy(firstIssue, "Chips", 2);
		double actual = this.firstIssue.calculateUnnormalizedWeight(PRIOR_BIDS);
		double expected = 38.0 / 100.0;	
		double persisted = this.firstIssue.getUnnormalizedWeight();
		this.checkActualExpectedPersisted(actual, expected, persisted);
		actual = this.firstIssue.calculateUnnormalizedRecentWeight(PRIOR_BIDS - 2);
		expected = 0.34375;	
		persisted = this.firstIssue.getUnnormalizedRecentWeight();
		this.checkActualExpectedPersisted(actual, expected, persisted);
	}
	
	/**
	 * Test normalized weight calculation based on the example in Johnny Black 2.3.3.
	 */
	@Test
	public void testCalculateNormalizedWeight() {
		this.incrementValueCountBy(this.firstIssue, "Fish", 9);
		this.incrementValueCountBy(this.firstIssue, "Chips", 1);
		this.incrementValueCountBy(this.secondIssue, "Small", 3);
		this.incrementValueCountBy(this.secondIssue, "Medium", 5);
		this.incrementValueCountBy(this.secondIssue, "Large", 2);
		final double firstUnnormalized = this.firstIssue.calculateUnnormalizedWeight(PRIOR_BIDS);
		final double secondUnnormalized = this.secondIssue.calculateUnnormalizedWeight(PRIOR_BIDS);
		final double total = firstUnnormalized + secondUnnormalized;
		double actual = this.firstIssue.calculateNormalizedWeight(total);
		double expected = 82.0 / 120.0;
		double persisted = this.firstIssue.getNormalizedWeight();
		this.checkActualExpectedPersisted(actual, expected, persisted);
		actual = this.secondIssue.calculateNormalizedWeight(total);
		expected = 38.0 / 120.0;
		persisted = this.secondIssue.getNormalizedWeight();
		this.checkActualExpectedPersisted(actual, expected, persisted);
	}
	
	/**
	 * Test normalized recent weight calculation based on the example in Johnny Black 2.3.3.
	 */
	@Test
	public void testCalculateNormalizedRecentWeight() {
		this.incrementValueCountBy(this.firstIssue, "Fish", 9);
		this.incrementValueCountBy(this.firstIssue, "Chips", 1);
		this.incrementValueCountBy(this.secondIssue, "Small", 3);
		this.incrementValueCountBy(this.secondIssue, "Medium", 5);
		this.incrementValueCountBy(this.secondIssue, "Large", 2);
		final double firstUnnormalized = this.firstIssue.calculateUnnormalizedRecentWeight(PRIOR_BIDS);
		final double secondUnnormalized = this.secondIssue.calculateUnnormalizedRecentWeight(PRIOR_BIDS);
		final double total = firstUnnormalized + secondUnnormalized;
		double actual = this.firstIssue.calculateNormalizedRecentWeight(total);
		double expected = 82.0 / 120.0;
		double persisted = this.firstIssue.getNormalizedRecentWeight();
		this.checkActualExpectedPersisted(actual, expected, persisted);
		actual = this.secondIssue.calculateNormalizedRecentWeight(total);
		expected = 38.0 / 120.0;
		persisted = this.secondIssue.getNormalizedRecentWeight();
		this.checkActualExpectedPersisted(actual, expected, persisted);
	}
	
	private void incrementValueCountBy(Agent17OpponentIssue issue, final String valueName, final int increment) {
		for (int i = 0; i < increment; i++) {
			issue.incrementValueCount(valueName);
		}
	}
	
	private void decrementValueRecentCountBy(Agent17OpponentIssue issue, final String valueName, final int decrement) {
		for (int i = 0; i < decrement; i++) {
			issue.decrementValueRecentCount(valueName);
		}
	}
	
	/**
	 * Check actual, expected and persisted using {@link BigDecimal} to allow for rounding errors.
	 * @param actual Actual result
	 * @param expected Expected result
	 * @param persisted Persisted result
	 */
	private void checkActualExpectedPersisted(final double actual, final double expected, final double persisted) {
		LOG.info("Expected={}, Actual={}, Persisted={}", expected, actual, persisted);
		final BigDecimal actualDecimal = new BigDecimal(actual, CONTEXT);
		final BigDecimal expectedDecimal = new BigDecimal(expected, CONTEXT);
		final BigDecimal persistedDecimal = new BigDecimal(persisted, CONTEXT);
		assertEquals(expectedDecimal, actualDecimal);
		assertEquals(expectedDecimal, persistedDecimal);
	}
}
