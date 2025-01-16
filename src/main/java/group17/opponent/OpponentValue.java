package group17.opponent;

public abstract class OpponentValue {

	protected int count = 0;
	protected final String name;
	
	/**
	 * Construct an Opponent Value based on an the name of a value in the current domain.
	 * @param name Name
	 */
	public OpponentValue(final String name) {
		this.name = name;
	}
		
	public int getCount() {
		return this.count;
	}
	
	public void incrementCount() {
		this.count++;
	}

	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return new StringBuilder(this.name)
			.append(", count=")
			.append(this.count)
			.toString();
	}
}
