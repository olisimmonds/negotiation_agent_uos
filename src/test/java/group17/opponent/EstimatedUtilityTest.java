package group17.opponent;

import static org.junit.Assert.*;

import org.junit.Test;

import group17.opponent.EstimatedUtility;

public class EstimatedUtilityTest {

	public static final double UTILITY = 0.123;
	public static final double CONFIDENCE = 0.456;
	public static final double OTHER_UTILITY = 0.789;
	public static final double OTHER_CONFIDENCE = 0.654;
	
	final EstimatedUtility estimate = new EstimatedUtility(UTILITY, CONFIDENCE);
	
	@Test
	public void testEqualsAndHashCodeWhenTheSame() {		
		final EstimatedUtility other = new EstimatedUtility(UTILITY, CONFIDENCE);
		assertEquals(this.estimate, other);
		assertEquals(this.estimate.hashCode(), other.hashCode());
	}
	
	@Test
	public void testEqualsAndHashCodeWhenUtilityDifferent() {		
		final EstimatedUtility other = new EstimatedUtility(OTHER_UTILITY, CONFIDENCE);
		assertNotEquals(this.estimate, other);
		assertNotEquals(this.estimate.hashCode(), other.hashCode());
	}
	
	@Test
	public void testEqualsAndHashCodeWhenConfidenceDifferent() {		
		final EstimatedUtility other = new EstimatedUtility(UTILITY, OTHER_CONFIDENCE);
		assertNotEquals(this.estimate, other);
		assertNotEquals(this.estimate.hashCode(), other.hashCode());
	}
	
	@Test
	public void testEqualsAndHashCodeWhenBothDifferent() {		
		final EstimatedUtility other = new EstimatedUtility(OTHER_UTILITY, OTHER_CONFIDENCE);
		assertNotEquals(this.estimate, other);
		assertNotEquals(this.estimate.hashCode(), other.hashCode());
	}
	
	@Test
	public void testEqualsWithNull() {		
		assertNotEquals(this.estimate, null);
	}
	
	@Test
	public void testEqualsWithOtherType() {	
		final String other = "other";
		assertNotEquals(this.estimate, other);
	}
	
	@Test
	public void testNotMeaningful() {		
		final EstimatedUtility other = EstimatedUtility.NO_MEANINGFUL_ESTIMATE;
		assertTrue(other.isNotMeaningful());
	}
	
	@Test
	public void testMeaningful() {		
		assertTrue(this.estimate.isMeaningful());
	}
}
