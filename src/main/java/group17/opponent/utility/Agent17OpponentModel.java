package group17.opponent.utility;

import genius.core.Domain;
import genius.core.issue.Issue;
import group17.opponent.OpponentModel;

/**
 * An {@link OpponentModel} that modifies the "Jonny Black" algorithms we worked with in Lab 3.
 * This is our benchmark for any improved opponent modeling algorithms.
 */
public class Agent17OpponentModel extends UtilityOpponentModel<Agent17OpponentIssue, Agent17OpponentValue> {

	public Agent17OpponentModel(final Domain domain) {
		super(domain);
	}
	
	public Agent17OpponentModel(final Domain domain, final int recentBidWindow) {
		super(domain, recentBidWindow);
	}

	@Override
	protected Agent17OpponentIssue createUtilityOpponentIssue(final Issue issue) {
		return new Agent17OpponentIssue(issue);
	}
}
