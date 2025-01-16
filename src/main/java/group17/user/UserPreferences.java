package group17.user;

import genius.core.Bid;
import genius.core.issue.Issue;
import genius.core.uncertainty.UserModel;
import group17.domain.DomainTranslator;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserPreferences {

    private final UserModel userModel;
    private final List<PreferencePair> preferenceOrder;

    public UserPreferences(UserModel userModel) {
        this.userModel = userModel;
        this.preferenceOrder = calculatePreferences();
    }

    public List<PreferencePair> getPreferenceOrder() {
        return preferenceOrder;
    }

    /**
     * calculates a rough estimate of the users preferred issues
     *
     * for each value in an issue, we calculate its average position in the bid ranking
     * then we take the standard deviation of each of the means, and assign it to the issue
     * the issue with the highest sd, is likely to be the most preferred
     *
     * @return issue preference order
     */
    private List<PreferencePair> calculatePreferences() {
        List<Bid> bidOrder = userModel.getBidRanking().getBidOrder();
        Map<String, Map<String, List<Integer>>> counts = new HashMap<>();

        for (int i = 0; i < bidOrder.size(); i++) {
            Bid bid = bidOrder.get(i);

            for (Issue issue : bid.getIssues()) {
                String issueName = DomainTranslator.issueName(issue);
                String valueName = DomainTranslator.valueName(bid.getValue(issue));

                if (!counts.containsKey(issueName)) counts.put(issueName, new HashMap<>());
                if (!counts.get(issueName).containsKey(valueName))
                    counts.get(issueName).put(valueName, new ArrayList<>());

                counts.get(issueName).get(valueName).add(i);
            }
        }

        StandardDeviation sd = new StandardDeviation();
        List<PreferencePair> prefOrder = new ArrayList<>();
        for (Map.Entry<String, Map<String, List<Integer>>> issueEntry : counts.entrySet()) {
            double[] means = new double[issueEntry.getValue().size()];
            int i = 0;
            for (Map.Entry<String, List<Integer>> valueEntry : issueEntry.getValue().entrySet()) {
                double total = 0;
                for (Integer idx : valueEntry.getValue()) total += idx;
                means[i++] = (total/(double) valueEntry.getValue().size());
            }

            prefOrder.add(new PreferencePair(issueEntry.getKey(), sd.evaluate(means)));
        }

        prefOrder.sort(Comparator.comparingDouble(PreferencePair::sd));
        return prefOrder;
    }

    public record PreferencePair(String issueName, double sd) {}
}
