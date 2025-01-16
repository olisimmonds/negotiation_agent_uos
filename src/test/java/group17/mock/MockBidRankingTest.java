package group17.mock;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import genius.core.Bid;
import group17.TestFramework;

public class MockBidRankingTest extends TestFramework {
	
	private static final Logger LOG = LoggerFactory.getLogger(MockBidRankingTest.class);
	
	private MockBidRanking bidRanking;
	private MockBid largePie;	
	private MockBid mediumChips;
	private MockBid mediumSausage;
	private MockBid smallFish;
	
	@Before
	public void setup() {
		List<Bid> mockBids = new ArrayList<>();
		this.smallFish = this.createBidWithUtility(MENU_OPTION_1, SIZE_1, 0.5);
		mockBids.add(this.smallFish);
		this.mediumChips = this.createBidWithUtility(MENU_OPTION_2, SIZE_2, 0.1);
		mockBids.add(this.mediumChips);
		this.mediumSausage = this.createBidWithUtility(MENU_OPTION_3, SIZE_2, 0.9);
		mockBids.add(this.mediumSausage);
		this.largePie = this.createBidWithUtility(MENU_OPTION_4, SIZE_3, 0.2);
		mockBids.add(this.largePie);
		this.bidRanking = new MockBidRanking(mockBids);
	}

	@Test
	public void testGetHighUtility() {
		LOG.info("Bids={}", this.bidRanking.getBidOrder());
		assertEquals(Double.valueOf(0.9), this.bidRanking.getHighUtility());
	}
	
	@Test
	public void testGetLowUtility() {
		assertEquals(Double.valueOf(0.1), this.bidRanking.getLowUtility());
	}
	
	@Test
	public void testGetBidOrder() {
		List<Bid> bids = this.bidRanking.getBidOrder();
		assertEquals(4, bids.size());
		assertEquals(this.mediumChips, bids.get(0));
		assertEquals(this.largePie, bids.get(1));
		assertEquals(this.smallFish, bids.get(2));
		assertEquals(this.mediumSausage, bids.get(3));
	}
	
	@Test
	public void testGetIterator() {
		final Iterator<Bid> iterator = this.bidRanking.iterator();
		int count = 0;
		while (iterator.hasNext()) {
			final Bid bid = iterator.next();
			assertTrue(bid instanceof MockBid);
			count++;
		}
		assertEquals(4, count);
	}
}