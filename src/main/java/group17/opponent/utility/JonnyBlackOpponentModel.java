package group17.opponent.utility;

import genius.core.Domain;
import genius.core.issue.Issue;
import group17.opponent.OpponentModel;

/**
 * An {@link OpponentModel} that implements the "Jonny Black" algorithms we worked with in Lab 3.
 * This is our benchmark for any improved opponent modeling algorithms.
 */
public class JonnyBlackOpponentModel extends UtilityOpponentModel<JonnyBlackOpponentIssue, JonnyBlackOpponentValue> {

	public JonnyBlackOpponentModel(final Domain domain) {
		super(domain);
	}
	
	public JonnyBlackOpponentModel(final Domain domain, final int recentBidWindow) {
		super(domain, recentBidWindow);
	}

	@Override
	protected JonnyBlackOpponentIssue createUtilityOpponentIssue(final Issue issue) {
		return new JonnyBlackOpponentIssue(issue);
	}
}
