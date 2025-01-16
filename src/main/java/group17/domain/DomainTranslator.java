package group17.domain;

import genius.core.issue.Issue;
import genius.core.issue.IssueDiscrete;
import genius.core.issue.Value;
import genius.core.issue.ValueDiscrete;
import genius.core.issue.ValueInteger;
import genius.core.issue.ValueReal;

/**
 * Simple translation functionality for the Genius domain model.
 * Everything else assumes that we're only dealing with discrete issues and values, but possibly
 * best to be safe.
 * @author Martin Ingram
 */
public class DomainTranslator {

	/**
	 * Get a unique name (identifier) for an issue.
	 * @param issue Issue
	 * @return Issue name
	 */
	public static final String issueName(final Issue issue) {
		String name = issue.convertToString();
		if (issue instanceof IssueDiscrete) {
			final IssueDiscrete discrete = (IssueDiscrete) issue;
			name = discrete.getName();
		}
		return name;
	}
	
	/**
	 * Get a unique name (identifier) for a value.
	 * @param value Value
	 * @return Value name
	 */
	public static final String valueName(final Value value) {
		String name = value.toString();
		if (value instanceof ValueDiscrete) {
			final ValueDiscrete discrete = (ValueDiscrete) value;
			name = discrete.getValue();
		} else if (value instanceof ValueInteger) {
			final ValueInteger integer = (ValueInteger) value;
			name = String.valueOf(integer);
		} else if (value instanceof ValueReal) {
			final ValueReal real = (ValueReal) value;
			name = String.valueOf(real);
		}
		return name;
	}
}
