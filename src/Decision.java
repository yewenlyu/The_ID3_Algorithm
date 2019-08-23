import java.util.List;

/**
 * Branch node decision is of the form "Is x_i <= t?"
 * Leaf node decision is of the form "Predict true/false"
 * @author Wenlyu Ye
 */
public class Decision {

    Decision Y;
    Decision N;

    private DecisionRule rule; // only valid when isPure is false (branch node)
    private double prediction; // only valid when isPure is true (leaf node)

    private List<FeatureVector> associatedDataset;

    /**
     * Static nested class for decision rule, only exits in branch node,
     * containing variable i and t for "x_i <= t?"
     */
    static class DecisionRule {
        int featureIdx;
        double threshold;

        DecisionRule(int featureIdx, double threshold) {
            this.featureIdx = featureIdx;
            this.threshold = threshold;
        }

        int getFeatureIdx() {
            return featureIdx;
        }

        double getThreshold() {
            return threshold;
        }

        void printRule() {
            System.out.println("\"Is x_" + featureIdx + " <= " + threshold + "?\"");
        }
    }

    Decision(DecisionRule rule, List<FeatureVector> inputDataset) {
        this.rule = rule;
        associatedDataset = inputDataset;

    }

    Decision(double prediction, List<FeatureVector> inputDataset) {
        this.prediction = prediction;
        associatedDataset = inputDataset;
    }

    DecisionRule getRule() {
        return rule;
    }

    double getPrediction() {
        return prediction;
    }

    List<FeatureVector> getAssociatedDataset() {
        return associatedDataset;
    }

    void printDecision() {
        if (rule != null) {
            rule.printRule();
        } else {
            System.out.println("\"predict = " + (int) prediction + "\"");
        }
    }
}
