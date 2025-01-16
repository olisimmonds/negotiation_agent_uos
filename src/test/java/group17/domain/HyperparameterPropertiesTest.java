/**
 * 
 */
package group17.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link HyperparameterProperties}.
 */
public class HyperparameterPropertiesTest {
	
	private static final String TEST_FILE_NAME = "hyperparameter.test.properties";

	private HyperparameterProperties hyperparameters;
	
	@Before
	public void setup() {
		this.hyperparameters = new HyperparameterProperties(TEST_FILE_NAME);
	}

	/**
	 * Check that values are loaded from the test version of the properties file.
	 */
	@Test
	public void testValues() {
		this.hyperparameters.diagnostics();
		assertEquals(Double.valueOf(0.4), Double.valueOf(this.hyperparameters.getBoulwareBeta()));
		assertEquals(Double.valueOf(0.99), Double.valueOf(this.hyperparameters.getFinishTime()));
		assertEquals(Double.valueOf(0.95), Double.valueOf(this.hyperparameters.getGiveUpTime()));
		assertEquals(10, this.hyperparameters.getRecentBidWindow());
		assertEquals(Double.valueOf(0.5), Double.valueOf(this.hyperparameters.getTransitionTime()));
		assertEquals(1000, this.hyperparameters.getMaxListSize());
		assertEquals(Double.valueOf(0.05), Double.valueOf(this.hyperparameters.getMaxElicitationPenalty()));
	}
	
	@Test
	public void testToCSV() {
		assertEquals("boulwareBeta,finishTime,giveUpTime,maxListSize,recentBidWindow,transitionTime,maxElicitationPenalty", 
			this.hyperparameters.toCSVLabels());
		assertEquals("0.4,0.99,0.95,1000,10,0.5,0.05", this.hyperparameters.toCSV());
	}
}
