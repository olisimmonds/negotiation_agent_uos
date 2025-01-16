package group17.mock;

import java.io.IOException;
import java.util.Random;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.utility.AbstractUtilitySpace;
import genius.core.utility.UtilitySpace;
import genius.core.xml.SimpleElement;

public class MockUtilitySpace extends AbstractUtilitySpace {

	private static final long serialVersionUID = 1L;
	
	private final Random random = new Random();
	
	public MockUtilitySpace(final Domain domain) {
		super(domain);
	}

	@Override
	public double getUtility(Bid bid) {
		return (bid instanceof MockBid) ? this.getUtilityFromMockBid(bid) : this.randomUtility();
	}
	
	private double getUtilityFromMockBid(final Bid bid) {
		final MockBid mockBid = (MockBid) bid;
		return mockBid.getUtility();
	}
	
	private double randomUtility() {
		return this.random.nextDouble(1.0);
	}

	@Override
	public UtilitySpace copy() {
		// Not supported.
		return null;
	}

	@Override
	public String isComplete() {
		return "true";
	}

	@Override
	public SimpleElement toXML() throws IOException {
		// Not supported
		return null;
	}
}
