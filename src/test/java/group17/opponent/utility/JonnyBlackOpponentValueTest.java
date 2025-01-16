package group17.opponent.utility;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class JonnyBlackOpponentValueTest {

	private static final int PRIOR_BIDS = 10;

	private JonnyBlackOpponentValue value;
	
	@Before
	public void setup() {
		this.value = new JonnyBlackOpponentValue("Diamond");
	}
	
	@Test
	public void testCalculatePreferenceValueForMostPopular() {
		final double expected = 1.0;
		final double actual = this.value.calculatePreferenceValue(1, 3, PRIOR_BIDS);
		assertTrue(expected == actual);
	}
	@Test
	public void testCalculatePreferenceValueForSecondMostPopular() {
		final double expected = 2.0 / 3.0;
		final double actual = this.value.calculatePreferenceValue(2, 3, PRIOR_BIDS);
		assertTrue(expected == actual);
	}
	
	@Test
	public void testCalculatePreferenceValueForThirdMostPopular() {
		final double expected = 1.0 / 3.0;
		final double actual = this.value.calculatePreferenceValue(3, 3, PRIOR_BIDS);
		assertTrue(expected == actual);
	}
	
	@Test
	public void testCalculateRecentPreferenceValueForMostPopular() {
		final double expected = 1.0;
		final double actual = this.value.calculateRecentPreferenceValue(1, 3, PRIOR_BIDS);
		assertTrue(expected == actual);
	}
	@Test
	public void testCalculateRecentPreferenceValueForSecondMostPopular() {
		final double expected = 2.0 / 3.0;
		final double actual = this.value.calculateRecentPreferenceValue(2, 3, PRIOR_BIDS);
		assertTrue(expected == actual);
	}
	
	@Test
	public void testCalculateRecentPreferenceValueForThirdMostPopular() {
		final double expected = 1.0 / 3.0;
		final double actual = this.value.calculateRecentPreferenceValue(3, 3, PRIOR_BIDS);
		assertTrue(expected == actual);
	}
	
	@Test
	public void testCalculateWeightWithThreeInTen() {
		this.incrementCountBy(3);
		assertTrue(this.value.getCount() == 3);
		final double actual = this.value.calculateWeight(PRIOR_BIDS);
		final double expected = 9.0 / 100.00;
		assertTrue(actual == expected);
		assertTrue(this.value.getWeight() == expected);
	}
	
	@Test
	public void testCalculateWeightWithNineInTen() {
		this.incrementCountBy(9);
		assertTrue(this.value.getCount() == 9);
		final double actual = this.value.calculateWeight(PRIOR_BIDS);
		final double expected = 81.0 / 100.00;
		assertTrue(actual == expected);
		assertTrue(this.value.getWeight() == expected);
	}
	
	@Test
	public void testCalculateRecentWeightWithThreeInTen() {
		this.incrementCountBy(3);
		assertTrue(this.value.getRecentCount() == 3);
		final double actual = this.value.calculateRecentWeight(PRIOR_BIDS);
		final double expected = 9.0 / 100.00;
		assertTrue(actual == expected);
		assertTrue(this.value.getRecentWeight() == expected);
	}
	
	@Test
	public void testCalculateRecentWeightWithNineInTen() {
		this.incrementCountBy(9);
		assertTrue(this.value.getRecentCount() == 9);
		final double actual = this.value.calculateRecentWeight(PRIOR_BIDS);
		final double expected = 81.0 / 100.00;
		assertTrue(actual == expected);
		assertTrue(this.value.getRecentWeight() == expected);
	}
	
	@Test
	public void testDecrementRecentCount() {
		this.incrementCountBy(9);
		this.decrementRecentCountBy(3);
		assertEquals(9, this.value.getCount());
		assertEquals(6, this.value.getRecentCount());
	}
	
	private void incrementCountBy(int increment) {
		for (int i = 0; i < increment; i++) {
			this.value.incrementCount();
		}
	}
	
	private void decrementRecentCountBy(int increment) {
		for (int i = 0; i < increment; i++) {
			this.value.decrementRecentCount();
		}
	}
}
