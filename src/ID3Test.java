import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class ID3Test {

    /**
     * This function parses the input data file into a set of FeatureVectors.
     * @param dataFile input data file
     * @param emptyDataset HashSet of FeatureVectors
     * @throws FileNotFoundException pa2train.txt/pa2validation.txt/pa2test.txt
     */
    private static void parseDataFile(File dataFile, List<FeatureVector> emptyDataset) throws FileNotFoundException {
        Scanner fScanner = new Scanner(dataFile);
        int entryCounter = 0;
        while (fScanner.hasNextLine()) {
            String[] dataStringArray = fScanner.nextLine().split(" ");
            emptyDataset.add(new FeatureVector(dataStringArray));
            entryCounter++;
        }
        fScanner.close();
        System.out.println("Parsing \"" + dataFile.getName() + "\", " + entryCounter + " entries read.");
    }

    /**
     * This function run the input dataset against the decision tree trained, return the prediction error
     * @param inputDataset input dataset
     * @return prediction error = # of error / # of data
     */
    static double testDecisionTree(List<FeatureVector> inputDataset) {
        int numOfErr = 0;
        for (FeatureVector vector : inputDataset) {
            if (ID3.predict(vector) != vector.getLabel()) {
                numOfErr++;
            }
        }
        return (double) numOfErr / (double) inputDataset.size();
    }

    /**
     * print the first three levels of the decision three, levels separated by \n
     */
    private static void printTree3() {
        Queue<Decision> BFSQueue = new ArrayDeque<>();
        BFSQueue.offer(ID3.decisionTree);
        System.out.print("\n");
        for (int i = 0; i < 3; i++) {
            int sizeBuffer = BFSQueue.size();
            for (int n = 0; n < sizeBuffer; n++) {
                Decision curNode = BFSQueue.poll();
                if (curNode != null) curNode.printDecision();

                BFSQueue.offer(curNode.Y);
                BFSQueue.offer(curNode.N);
            }
            System.out.print('\n');
        }
    }

    public static void main(String[] args) {

        // data files
        File trainingDataFile = new File("pa2train.txt");
        File validationDataFile = new File("pa2validation.txt");
        File testDataFile = new File("pa2test.txt");

        // parse training dataset
        List<FeatureVector> trainingDataset = new LinkedList<>();
        try {
            parseDataFile(trainingDataFile, trainingDataset);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // parse validation dataset
        List<FeatureVector> validationDataset = new LinkedList<>();
        try {
            parseDataFile(validationDataFile, validationDataset);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // parse test dataset
        List<FeatureVector> testDataset = new LinkedList<>();
        try {
            parseDataFile(testDataFile, testDataset);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // generate the decision tree
        System.out.println("Generating decision tree with training data...");
        ID3.decisionTree = ID3.train(trainingDataset);

        // print the first three levels of the tree
        printTree3();

        // calculate training/testing/validation error
        double trainingError = testDecisionTree(trainingDataset);
        System.out.println("Training Error = " + trainingError);
        double testError = testDecisionTree(testDataset);
        System.out.println("Test Error = " + testError);
        double validationError = testDecisionTree(validationDataset);
        System.out.println("Validation Error = " + testError);

        // prune the decision tree
        ID3.prune(validationDataset,testDataset);

    }
}
