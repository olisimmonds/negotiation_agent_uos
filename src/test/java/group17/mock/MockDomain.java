package group17.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import genius.core.Bid;
import genius.core.Domain;
import genius.core.issue.Issue;
import genius.core.issue.Objective;

/**
 * Mock implementation of a {@link Domain} for integration and unit testing.
 * At present this ONLY supports set and get of a list of {@link Issue} domain objects.
 * @author Martin Ingram
 */
public class MockDomain implements Domain {
	
	private final List<Issue> issues;
	
	/**
	 * Default constructor.
	 */
	public MockDomain() {
		this.issues = new ArrayList<>();
		this.issues.add(null);
	}
	
	/**
	 * Add an {@link Issue} to this domain.
	 * @param issue Issue
	 */
	public void addIssue(final Issue issue) {
		this.issues.add(issue);
	}

	@Override
	public List<Objective> getObjectives() {
		// TODO Not currently supported.
		return null;
	}

	@Override
	public Objective getObjectivesRoot() {
		// TODO Not currently supported.
		return null;
	}

	@Override
	public List<Issue> getIssues() {
		return this.issues;
	}

	@Override
	public Bid getRandomBid(Random r) {
		// TODO Not currently supported.
		return null;
	}

	@Override
	public long getNumberOfPossibleBids() {
		return 0;
	}

	@Override
	public String getName() {
		return "Integration Test Domain";
	}
}
