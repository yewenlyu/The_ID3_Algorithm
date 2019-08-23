import java.util.List;

public class ID3 {

    /** root node of the ID3 decision tree */
    public static Decision decisionTree;

    /**
     * This function checks if the input data set contains only one label, if so, return the common label;
     * if not, return -1
     *
     * @param inputDataSet input data set
     * @return 0: data set is purely 0; 1: data set is purely 1; -1: data set is imPure (contains both label)
     */
    private static int isPure(List<FeatureVector> inputDataSet) {
        int res = inputDataSet.get(0).getLabel();
        for (FeatureVector vector : inputDataSet) {
            if (vector.getLabel() != res) {
                return -1;
            }
        }
        return res;
    }

    /**
     * This function calculates the conditional entropy given the condition x_i <= t or x_i > t
     *
     * @param inputDataSet subset of training data
     * @param i feature index
     * @param t threshold
     * @return H(Y|X_i) = P(X_i <= t)H(Y|X_i <= t) + P(X_i > t)H(Y|X_i > t)
     */
    private static double conditionalEntropy(List<FeatureVector> inputDataSet, int i, int t) {
        int s = inputDataSet.size(); // total number of vectors
        int y0 = 0; // # of vectors with x_i <= t and label 0
        int y1 = 0; // # of vectors with x_i <= t and label 1
        int n0 = 0; // # of vectors with x_i > t and label 0
        int n1 = 0; // # of vectors with x_i > t and label 1

        for (FeatureVector vector : inputDataSet) {
            if (vector.getFeature()[i] <= t) {
                if (vector.getLabel() == 0) {
                    y0++;
                } else {
                    y1++;
                }
            } else {
                if (vector.getLabel() == 0) {
                    n0++;
                } else {
                    n1++;
                }
            }
        }

        // calculate y and n proportion
        double ypr = (double) (y0 + y1) / (double) s;
        double npr = (double) (n0 + n1) / (double) s;

        // calculate entropy: H(X) = \sum_{x \in \mathcal{X}} -p(X = x)\log{P(X = x)}
        double yp0 = (double) y0 / (double) (y0 + y1);
        double yp1 = (double) y1 / (double) (y0 + y1);
        double yEntropy = -yp0 * Math.log(yp0) - yp1 * Math.log(yp1);

        double np0 = (double) n0 / (double) (n0 + n1);
        double np1 = (double) n1 / (double) (n0 + n1);
        double nEntropy = -np0 * Math.log(np0) - np1 * Math.log(np1);

        return ypr * yEntropy + npr * nEntropy;
    }

    /**
     * This function explores all the possible decision rules to split the data,
     * returns the one that yields most information gain H.
     *
     * @param inputDataSet subset of training data
     * @return Decision instance: {feature index, threshold}
     */
    private static Decision.DecisionRule generateRule(List<FeatureVector> inputDataSet) {

        double minEntropy = Double.MAX_VALUE;
        int minEntropyIndex = 0;
        int minEntropyThreshold = Integer.MAX_VALUE;

        for (int i = 0; i < FeatureVector.DIMENSION; i++) {
            for (FeatureVector vector : inputDataSet) {
                double curEntropy = conditionalEntropy(inputDataSet, i, vector.getFeature()[i]);
                if (curEntropy < minEntropy) {
                    minEntropy = curEntropy;
                    minEntropyIndex = i;
                    minEntropyThreshold = vector.getFeature()[i];
                }
            }
        }

        return new Decision.DecisionRule(minEntropyIndex, minEntropyThreshold);
    }

    /**
     * This function uses the divide and conquer approach to build the decision tree.
     *
     * @param trainingDataSet training data set
     * @return the current decision tree node
     */
    public static Decision train(List<FeatureVector> trainingDataSet) {

        // base case
        if (ID3.isPure(trainingDataSet) != -1) {

        }


        return null;
    }
}
