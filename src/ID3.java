import java.util.*;

public class ID3 {

    /**
     * root node of the ID3 decision tree
     */
    static Decision decisionTree;

    /**
     * Encapsulated return class for split function, pair of dataset
     */
    private static class DatasetPair {
        List<FeatureVector> ySet;
        List<FeatureVector> nSet;

        DatasetPair(List<FeatureVector> ySet, List<FeatureVector> nSet) {
            this.ySet = ySet;
            this.nSet = nSet;
        }
    }

    /**
     * This function uses the divide and conquer approach to build the decision tree.
     *
     * @param inputDataset subset of training dataset
     * @return the current decision tree node
     */
    static Decision train(List<FeatureVector> inputDataset) {

        // base case
        if (isPure(inputDataset) != -1) {
            return new Decision(isPure(inputDataset), inputDataset);
        }

        // recursive case
        Decision.DecisionRule rule = generateRule(inputDataset);
        rule.printRule();

        Decision curDecision = new Decision(rule, inputDataset);
        DatasetPair splitDataset = split(inputDataset, rule);

        curDecision.Y = train(splitDataset.ySet);
        curDecision.N = train(splitDataset.nSet);

        return curDecision;
    }

    /**
     * Prediction rule of 1D3: traverse down the decision tree according to the decision rules,
     * return the prediction label at the leaf
     * @param vector input feature vector
     * @return 0.0 or 1.1
     */
    static double predict(FeatureVector vector) {
        Decision cur = decisionTree;
        while (cur.getRule() != null) {
            if (vector.satisfies(cur.getRule())) {
                cur = cur.Y;
            } else {
                cur = cur.N;
            }
        }
        return cur.getPrediction();
    }

    /**
     * Prune the decision tree in BFS order
     * @param validationDataset
     */
    static void prune(List<FeatureVector> validationDataset, List<FeatureVector> testDataset) {

        double rawValidationError = ID3Test.testDecisionTree(validationDataset);
        double pruneValidationError;

        Queue<Decision> decisionQueue = new ArrayDeque<>();
        decisionQueue.offer(decisionTree);

        Decision savedDecision;
        int pruneCount = 0;

        while (pruneCount < 2) {
            Decision curDecision = decisionQueue.poll();

            savedDecision = curDecision.Y;
            curDecision.Y = forceLeaf(curDecision.Y);
            pruneValidationError = ID3Test.testDecisionTree(validationDataset);
            if (pruneValidationError < rawValidationError) {
                System.out.print("Pruning ");
                savedDecision.printDecision();
                System.out.print("Updated Validation Error = " + pruneValidationError + ", ");
                System.out.println("Test Error = " + ID3Test.testDecisionTree(testDataset));
                pruneCount++;
            } else {
                curDecision.Y = savedDecision;
            }

            savedDecision = curDecision.N;
            curDecision.N = forceLeaf(curDecision.N);
            pruneValidationError = ID3Test.testDecisionTree(validationDataset);
            if (pruneValidationError < rawValidationError) {
                System.out.print("Pruning ");
                savedDecision.printDecision();
                System.out.print("Updated Validation Error = " + pruneValidationError + ", ");
                System.out.println("Test Error = " + ID3Test.testDecisionTree(testDataset));
                pruneCount++;
            } else {
                curDecision.N = savedDecision;
            }

            decisionQueue.offer(curDecision.Y);
            decisionQueue.offer(curDecision.N);
        }
    }

    /**
     * This function calculates the conditional entropy given the condition x_i <= t or x_i > t
     *
     * @param inputDataset subset of training data
     * @param i            feature index
     * @param t            threshold
     * @return H(Y | X_i) = P(X_i <= t)H(Y|X_i <= t) + P(X_i > t)H(Y|X_i > t)
     */
    static double conditionalEntropy(List<FeatureVector> inputDataset, int i, double t) {
        int s = inputDataset.size(); // total number of vectors
        int y0 = 0; // # of vectors with x_i <= t and label 0
        int y1 = 0; // # of vectors with x_i <= t and label 1
        int n0 = 0; // # of vectors with x_i > t and label 0
        int n1 = 0; // # of vectors with x_i > t and label 1

        for (FeatureVector vector : inputDataset) {
            if (vector.getFeature()[i] <= t) {
                if (vector.getLabel() == 0.0) {
                    y0++;
                } else {
                    y1++;
                }
            } else {
                if (vector.getLabel() == 0.0) {
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
        double yEntropy = entropy(y0, y1);
        double nEntropy = entropy(n0, n1);

        return (ypr == 0 ? 0 : ypr * entropy(y0, y1)) + (npr == 0 ? 0 : npr * entropy(n0, n1));
    }

    /**
     * This function calculates the entropy of a set with two states
     * @param p1 frequency of state one
     * @param p2 frequency of state two
     * @return H(X) = \sum_{x \in \mathcal{X}} -pr(X = x)\log{pr(X = x)}, define 0log(0) to be 0
     */
    private static double entropy(int p1, int p2) {
        double pr1 = (double) p1 / (double) (p1 + p2);
        double pr2 = (double) p2 / (double) (p1 + p2);
        return (pr1 == 0 ? 0 : -pr1 * Math.log(pr1)) + (pr2 == 0 ? 0 : -pr2 * Math.log(pr2));
    }

    /**
     * This function generates a list of possible thresholds for the given feature
     * @param inputDataset subset of training dataset
     * @param i index of the feature
     * @return list of possible thresholds
     */
    private static List<Double> generateBoundaries(List<FeatureVector> inputDataset, int i) {

        inputDataset.sort(new Comparator<FeatureVector>() {
            @Override
            public int compare(FeatureVector v1, FeatureVector v2) {
                if (v1.getFeature()[i] < v2.getFeature()[i]) {
                    return -1;
                } else if (v1.getFeature()[i] > v2.getFeature()[i]) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        List<Double> boundaries = new ArrayList<>();
        for (int n = 0; n + 1 < inputDataset.size(); n++) {
            double boundary = (inputDataset.get(n).getFeature()[i] + inputDataset.get(n+1).getFeature()[i]) / 2;
            if (!boundaries.contains(boundary)) {
                boundaries.add(boundary);
            }
        }
        return boundaries;
    }

    /**
     * This function explores all the possible decision rules to split the data,
     * returns the one that yields most information gain H.
     *
     * @param inputDataset subset of training data
     * @return Decision instance: {feature index, threshold}
     */
    private static Decision.DecisionRule generateRule(List<FeatureVector> inputDataset) {

        double minEntropy = Double.MAX_VALUE;
        int minEntropyIndex = Integer.MAX_VALUE;
        double minEntropyThreshold = Double.MAX_VALUE;

        for (int i = 0; i < FeatureVector.DIMENSION; i++) {
            List<Double> thresholds = generateBoundaries(inputDataset, i);
            for (Double t : thresholds) {
                double curEntropy = conditionalEntropy(inputDataset, i, t);

                if (curEntropy <= minEntropy) {
                    minEntropy = curEntropy;
                    minEntropyIndex = i;
                    minEntropyThreshold = t;
                }
            }
        }

        return new Decision.DecisionRule(minEntropyIndex, minEntropyThreshold);
    }

    /**
     * This function split the input dataset in to two with the decision rule.
     *
     * @param inputDataset subset of a dataset
     * @param rule         input rule of the form "Is x_i <= t?"
     * @return pair of dataset {dataset that satisfies the rule, dataset that doesn't}
     */
    private static DatasetPair split(List<FeatureVector> inputDataset, Decision.DecisionRule rule) {
        List<FeatureVector> ySet = new LinkedList<>();
        List<FeatureVector> nSet = new LinkedList<>();
        for (FeatureVector vector : inputDataset) {
            if (vector.satisfies(rule)) {
                ySet.add(vector);
            } else {
                nSet.add(vector);
            }
        }
        return new DatasetPair(ySet, nSet);
    }

    /**
     * This function is used in decision tree pruning, if forces a leaf node by ignoring the minor labels.
     *
     * @param inputBranchNode branch node to be replaced
     * @return decision tree leaf, with label of majority
     */
    private static Decision forceLeaf(Decision inputBranchNode) {
        if (inputBranchNode.getRule() == null) {
            return inputBranchNode;
        }

        int n0 = 0;
        int n1 = 0;
        for (FeatureVector vector : inputBranchNode.getAssociatedDataset()) {
            if (vector.getLabel() == 0.0) {
                n0++;
            } else {
                n1++;
            }
        }

        return new Decision(n0 > n1 ? 0.0 : 1.0, inputBranchNode.getAssociatedDataset());
    }

    /**
     * This function checks if the input dataset contains only one label, if so, return the common label;
     * if not, return -1
     *
     * @param inputDataset input dataset
     * @return 0: dataset is purely 0; 1: dataset is purely 1; -1: dataset is imPure (contains both label)
     */
    private static double isPure(List<FeatureVector> inputDataset) {
        double res = inputDataset.get(0).getLabel();
        for (FeatureVector vector : inputDataset) {
            if (vector.getLabel() != res) {
                return -1;
            }
        }
        return res;
    }



}
