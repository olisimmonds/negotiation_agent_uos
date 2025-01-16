package group17.mock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import genius.core.Bid;
import genius.core.uncertainty.BidRanking;

public class MockBidRanking extends BidRanking {

	private double highUtility = 0.0;
	private double lowUtility = 0.0;
	private List<Bid> sortedMockBids;
	
	public MockBidRanking(final List<Bid> bids) {
		super(bids, 0.0, 0.0);
		this.sortedMockBids = this.filterAndSort(bids);
	}
	
	@Override
	public List<Bid> getBidOrder() {
		return this.sortedMockBids;
	}
	
	@Override
	public Double getHighUtility() {
		return this.highUtility;
	}
	
	@Override
	public Double getLowUtility() {
		return this.lowUtility;
	}
	
	@Override
	public Iterator<Bid> iterator() {
		return this.sortedMockBids.iterator();
	}
	
	@Override
	public String toString() {
		return this.sortedMockBids.toString();
	}
	
	private List<Bid> filterAndSort(List<Bid> bids) {
		final List<MockBid> filtered = this.filter(bids);
		final List<MockBid> sorted = this.sort(filtered);
		this.setHighAndLowUtilities(sorted);
		return this.transformToBids(sorted);
	}
	
	private List<MockBid> filter(final List<Bid> bids) {
		final List<MockBid> filtered = new ArrayList<>();
		for (final Bid bid : bids) {
			if (bid instanceof MockBid) {
				final MockBid mockBid = (MockBid) bid;
				filtered.add(mockBid);
			}
		}
		return filtered;
	}
	
	private List<MockBid> sort(final List<MockBid> unsorted) {
		return unsorted.stream()
		    .sorted(($1, $2) -> Double.compare($1.getUtility(), $2.getUtility()))
			.toList();
	}
	
	private void setHighAndLowUtilities(final List<MockBid> sorted) {
		if (!sorted.isEmpty()) {
			final int lastIndex = sorted.size() - 1;
			this.lowUtility = sorted.get(0).getUtility();
			this.highUtility = sorted.get(lastIndex).getUtility();
		}
	}
	
	private List<Bid> transformToBids(List<MockBid> sorted) {
		final List<Bid> bids = new ArrayList<>();
		for (final MockBid mockBid : sorted) {
			bids.add(mockBid);
		}
		return bids;
	}
}
