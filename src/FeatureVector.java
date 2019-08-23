/**
 * Under the context that we are looking at the task of classifying whether a client is likely to default on their
 * credit card payment based on their past behavior and other characteristics, the feature vector contains 22
 * coordinates representing past behavior and characteristics, and a label indicating whether the card-holder defaults
 * on their credit card bill in October.
 *
 * @author Wenlyu Ye
 */
public class FeatureVector {

    static final int DIMENSION = 22;
    private double[] feature; // dimension = 22
    private double label; // indicates whether or not user sets default

    FeatureVector(String[] inputDataString) {

        if (inputDataString.length != DIMENSION + 1) {
            System.out.println("Error: Invalid data entry.");
        }

        feature = new double[DIMENSION];
        for (int i = 0; i < DIMENSION; i++) {
            feature[i] = Double.parseDouble(inputDataString[i]);
        }
        label = Double.parseDouble(inputDataString[DIMENSION]);
    }

    double[] getFeature() {
        return feature;
    }

    double getLabel() {
        return label;
    }

    boolean satisfies(Decision.DecisionRule rule) {
        return feature[rule.getFeatureIdx()] <= rule.getThreshold();
    }


}
