import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class ID3Test {

    /**
     * This function parses the input data file into a set of FeatureVectors.
     * @param dataFile input data file
     * @param emptyDataSet HashSet of FeatureVectors
     * @throws FileNotFoundException pa2train.txt/pa2validation.txt/pa2test.txt
     */
    private static void parseDataFile(File dataFile, List<FeatureVector> emptyDataSet) throws FileNotFoundException {
        Scanner fScanner = new Scanner(dataFile);
        int entryCounter = 0;
        while (fScanner.hasNextLine()) {
            String[] dataStringArray = fScanner.nextLine().split(" ");
            emptyDataSet.add(new FeatureVector(dataStringArray));
            entryCounter++;
        }
        fScanner.close();
        System.out.println("Parsing \"" + dataFile.getName() + "\", " + entryCounter + " entries read.");
    }

    public static void main(String[] args) {

        // data files
        File trainingDataFile = new File("pa2train.txt");
        File validationDataFile = new File("pa2validation.txt");
        File testDataFile = new File("pa2test.txt");

        // parse training dataset
        List<FeatureVector> trainingDataSet = new LinkedList<>();
        try {
            parseDataFile(trainingDataFile, trainingDataSet);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}
