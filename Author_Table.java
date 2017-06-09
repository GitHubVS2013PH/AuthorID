import java.io.*;
import java.util.*;

/**
 * Created by Paul on 6/5/2017.
 */
public class Author_Table {
    private int wordCount = 0;
    private String author = "";
    private HashMap<String, Double> wordMap = new HashMap<>();
    private final double logSmallest;
    private final int COUNT_THRESHOLD = 0;

    Author_Table(String author, String inFile) {
        this.author = author;
        processFile(inFile);
        //logSmallest = -Math.log10(this.wordCount);
        logSmallest = Math.log10(Double.MIN_VALUE);
    }

    private void processFile(String inFile) {
        File infile = new File("resources\\" + inFile + "_Processed.txt");
        try {
            FileReader fileReader = new FileReader(infile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // split by any number of spaces
                String[] tokens = line.split("\\s+");

                for (int i = 0; i < tokens.length; ++i) {
                    tokens[i] = Author_ID.processWord(tokens[i]);
                    if (tokens[i].length() == 0)
                        continue;
                    if (Character.isDigit(tokens[i].charAt(0)))
                        tokens[i] = "<NUM>";

                    if (wordMap.containsKey(tokens[i]))
                        wordMap.put(tokens[i], wordMap.get(tokens[i]) + 1.0);
                    else
                        wordMap.put(tokens[i], 1.0); // initial count
                    ++wordCount;
                }
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            System.out.println(inFile + " file error in Author_Table constructor");
            e.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Error reading file '" + infile + "'");
        }
    }

    public double logScore(Author_Table sample) {
        double rtnVal = 0.0;
        for (Map.Entry<String,Double> entry : sample.getWordMap().entrySet()) {
            String word = entry.getKey();
            if (this.wordMap.containsKey(word)) {
                double count = this.wordMap.get(word);
                if (count <= COUNT_THRESHOLD) // de-noising feature
                    rtnVal += logSmallest;
                else
                    rtnVal += Math.log10(count / this.wordCount);
            }
            else
                rtnVal += logSmallest;
        }
        return rtnVal;
    }

    public int getWordCount() { return wordCount; }

    public String getAuthor() { return author; }

    public HashMap<String, Double> getWordMap() { return wordMap; }

    @Override
    public String toString() {
        String rtnStr = "For author " + author + " the word count is: " + wordCount
                + " and word frequencies are:\n";

        for(Map.Entry<String, Double> entry : wordMap.entrySet())
            rtnStr += entry.getKey() + " = " + entry.getValue() + "\n";

        return rtnStr;
    }
}
