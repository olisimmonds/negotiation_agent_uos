/**
 * 
 */
package group17.domain;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hyperparameter Properties, loaded from file at startup.
 * @author Martin Ingram
 */
public class HyperparameterProperties {
	
	private static final Logger LOG = LoggerFactory.getLogger(HyperparameterProperties.class);

	protected static final String BOULWARE_BETA = "boulwareBeta";
	protected static final String FILE_NAME = "hyperparameter.properties";
	protected static final String FINISH_TIME = "finishTime";
	protected static final String GIVE_UP_TIME = "giveUpTime";
	protected static final String MAX_LIST_SIZE = "maxListSize";
	protected static final String RECENT_BID_WINDOW = "recentBidWindow";
	protected static final String TRANSITION_TIME = "transitionTime";
	protected static final String MAX_ELICITATION_PENALTY = "maxElicitationPenalty";


	private final double boulwareBeta;
	private final double finishTime;
	private final double giveUpTime;
	private final int maxListSize;
	private final int recentBidWindow;
	private final double transitionTime;
	private final double maxElicitationPenalty;
	
	/**
	 * Create Hyperparameter properties from a file on the classpath.
	 */
	public HyperparameterProperties() {
		this(FILE_NAME);
	}
		
	protected HyperparameterProperties(final String fileName) {
		final Properties properties = this.getPropertiesFromFile(fileName);
		this.boulwareBeta = Double.parseDouble(properties.getProperty(BOULWARE_BETA, "0.1"));
		this.finishTime = Double.parseDouble(properties.getProperty(FINISH_TIME, "0.4"));
		this.giveUpTime = Double.parseDouble(properties.getProperty(GIVE_UP_TIME, "1.0"));
		this.maxListSize = (int) Double.parseDouble(properties.getProperty(MAX_LIST_SIZE, "330"));
		this.recentBidWindow = (int) Double.parseDouble(properties.getProperty(RECENT_BID_WINDOW, "10"));
		this.transitionTime = Double.parseDouble(properties.getProperty(TRANSITION_TIME, "0.5"));
		this.maxElicitationPenalty = Double.parseDouble(properties.getProperty(MAX_ELICITATION_PENALTY, "0.05"));
	}
	
	public double getBoulwareBeta() {
		return this.boulwareBeta;
	}

	public double getFinishTime() {
		return this.finishTime;
	}

	public double getGiveUpTime() {
		return this.giveUpTime;
	}

	public int getMaxListSize() {
		return this.maxListSize;
	}
	
	public int getRecentBidWindow() {
		return recentBidWindow;
	}

	public double getTransitionTime() {
		return this.transitionTime;
	}

	public double getMaxElicitationPenalty() {
		return maxElicitationPenalty;
	}

	/**
	 * Dump the properties to the console and log file.
	 */
	public void diagnostics() {
		LOG.info("{}", this);
		LOG.debug("{}", this.toCSVLabels());
		LOG.debug("{}", this.toCSV());
	}
	
	/**
	 * CSV representation of the property names.
	 * @return CSV representation
	 */
	public String toCSVLabels() {
		return new StringBuilder()
			.append(BOULWARE_BETA).append(",")
			.append(FINISH_TIME).append(",")
			.append(GIVE_UP_TIME).append(",")
			.append(MAX_LIST_SIZE).append(",")
			.append(RECENT_BID_WINDOW).append(",")
			.append(TRANSITION_TIME).append(",")
			.append(MAX_ELICITATION_PENALTY)
			.toString();
	}
	
	/**
	 * CSV representation of the property values.
	 * @return CSV representation
	 */
	public String toCSV() {
		return new StringBuilder()
			.append(this.boulwareBeta).append(",")
			.append(this.finishTime).append(",")
			.append(this.giveUpTime).append(",")
			.append(this.maxListSize).append(",")
			.append(this.recentBidWindow).append(",")
			.append(this.transitionTime).append(",")
			.append(this.maxElicitationPenalty)
			.toString();
	}
	
	@Override
	public String toString() {
		return new StringBuilder("Hyperparameters: [")
			.append(BOULWARE_BETA).append("=").append(this.boulwareBeta).append(", ")
			.append(FINISH_TIME).append("=").append(this.finishTime).append(", ")
			.append(GIVE_UP_TIME).append("=").append(this.giveUpTime).append(", ")
			.append(MAX_LIST_SIZE).append("=").append(this.maxListSize).append(", ")
			.append(RECENT_BID_WINDOW).append("=").append(this.recentBidWindow).append(", ")
			.append(TRANSITION_TIME).append("=").append(this.transitionTime).append(",")
			.append(MAX_ELICITATION_PENALTY).append("=").append(this.maxElicitationPenalty)
			.append("]")
			.toString();
	}

	/**
	 * Load the properties file from the classpath.
	 * @return Properties
	 */
	private Properties getPropertiesFromFile(final String fileName) {
		final Properties properties = new Properties();
		final ClassLoader loader = HyperparameterProperties.class.getClassLoader();
		try (final InputStream input = loader.getResourceAsStream(fileName)) {
		    properties.load(input);
		} catch (IOException ioe) {
			LOG.error("Error loading hyperparameters from " + fileName +": ", ioe);
			LOG.warn("Hyperparameters will be set from hardcoded defaults.");
		}
		return properties;
	}
}
