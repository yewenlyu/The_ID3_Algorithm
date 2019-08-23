import java.util.List;

/**
 * Branch node decision is of the form "Is x_i <= t?"
 * Leaf node decision is of the form "Predict true/false"
 * @author Wenlyu Ye
 */
public class Decision {

    Decision Y;
    Decision N;
    List<FeatureVector> dataSetAssociated;

    boolean isPure;
    DecisionRule rule; // only valid when isPure is false (branch node)
    int predictionLabel; // only valid when isPure is true (leaf node)

    /**
     * Static nested class for decision rule, only exits in branch node,
     * containing variable i and t for "x_i <= t?"
     */
    public static class DecisionRule {
        int featureIdx;
        int threshold;

        public DecisionRule(int featureIdx, int threshold) {
            this.featureIdx = featureIdx;
            this.threshold = threshold;
        }
    }

    public Decision(DecisionRule rule, List<FeatureVector> inputDataSet) {

    }

    public Decision(int predictionLabel, List<FeatureVector> inputDataSet) {

    }

    public void setY(Decision Y) {
        this.Y = Y;
    }

    public void setN(Decision N) {
        this.N = N;
    }






}
