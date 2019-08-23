/**
 * Under the context that we are looking at the task of classifying whether a client is likely to default on their
 * credit card payment based on their past behavior and other characteristics, the feature vector contains 22
 * coordinates representing past behavior and characteristics, and a label indicating whether the card-holder defaults
 * on their credit card bill in October.
 *
 * @author Wenlyu Ye
 */
public class FeatureVector {

    public static final int DIMENSION = 22;
    private int[] feature; // dimension = 22
    private int label; // indicates whether or not user sets default

    FeatureVector(String[] inputDataString) {

        if (inputDataString.length != DIMENSION + 1) {
            System.out.println("Error: Invalid data entry.");
        }

        feature = new int[DIMENSION];
        for (int i = 0; i < DIMENSION; i++) {
            feature[i] = Integer.parseInt(inputDataString[i]);
        }
        label = Integer.parseInt(inputDataString[DIMENSION + 1]);

        if (label != 0 && label != 1) {
            System.out.println("Error: Invalid data entry.");
        }
    }

    public int[] getFeature() {
        return feature;
    }

    public int getLabel() {
        return label;
    }


}
