import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Paul on 6/5/2017.
 */
public class Author_ID {
    static ArrayList<Author_Table> authorTables = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Author ID Project");

        // specify test file
        final String TEST_FILE = "Test_Files\\Verne";
        System.out.println("Processing file: " + TEST_FILE);
        Standardize_Text(TEST_FILE);
        Author_Table testFile =  new Author_Table("Test_File", TEST_FILE);

        // build reference dictionaries from author's corpus
        String[] authors = {"Austen", "Dickens", "Verne", "Doyle", "Stevenson" };
        for (String author : authors) {
            String fileName = author + "\\" + author;
            System.out.println("Processing file: " + fileName);
            Standardize_Text(fileName);
            authorTables.add(new Author_Table(author, fileName));
        }

        //System.out.println(authorTables.get(1)); // for testing

        // compute Bayesian 'logScore' for each author and choose best
        double bestScore = Double.NEGATIVE_INFINITY;
        String likelyAuthor = "";
        for (Author_Table table : authorTables) {
            String author = table.getAuthor();
            double score = table.logScore(testFile);
            System.out.printf("The testFile %s logScore is: %.1f%n", author, score);
            if (score > bestScore) {
                bestScore = score;
                likelyAuthor = author;
            }
        }
        System.out.println("Most likely author is: " + likelyAuthor);

    }

    static void Standardize_Text(String fileIn) {
        File infile = new File("resources\\" + fileIn + ".txt");
        File outfile = new File("resources\\" + fileIn + "_Processed.txt");
        BufferedWriter writer;

        try {
            Scanner input = new Scanner(infile);
            writer = new BufferedWriter(new FileWriter(outfile));
            while (input.hasNextLine())
                writer.write (processString(input.nextLine()) + "\n");
            writer.close();
        }
        catch (FileNotFoundException e) {
            System.out.println(fileIn + " error");
            e.printStackTrace();
        }
        catch (IOException e) {
            System.out.println(fileIn + " error");
            e.printStackTrace();
        }
    }

    private static String processString(String line) {
        // add roman numerals replacement here since they are in upper case
        // see https://stackoverflow.com/questions/267399/
        // how-do-you-match-only-valid-roman-numerals-with-a-regular-expression

        // after roman numeral handling then convert to lower case
        line = line.toLowerCase().trim();

        // order of replace can be important
        // substitute spaces for dashes prior to next step
        line = line.replace("-", " ");
        line = line.replace("_", " ");
        line = line.replace("—", " ");

        // process special character sequences
        line = line.replace(",", "");
        line = line.replace("\"", "");
        line = line.replace("(", "");
        line = line.replace(")", "");
        line = line.replace(":", "");
        line = line.replace("’s", "");
        line = line.replace("'s", "");
        line = line.replace("*", " ");

        // do quotes second
        // leave single quote as it could be possessive
        line = line.replace("“", "");
        line = line.replace("”", "");
        line = line.replace(" ‘", " ");
        line = line.replace(" '", " ");
        line = line.replace("‘", "");
        line = line.replace("’ ", " ");
        line = line.replace("' ", " ");
        line = line.replace("’,", "");
        line = line.replace("',", "");
        line = line.replace(",’", "");
        line = line.replace(",'", "");

        // process end of sentence
        line = line.replace("!", ".");
        line = line.replace("?", ".");
        line = line.replace(".’", ".");
        line = line.replace("’.", ".");
        line = line.replace(";", ".");

        line = line.replace("..", ".");
        line = line.replace("...", ".");
        line = line.replace(".", " </s> ");

        if (isLastCharApostrophe(line))
            line = line.substring(0, line.length() - 1);

        return line;
    }

    static String processWord(String word) {
        // strip apostrophe from beginning and end of word
        if (isLastCharApostrophe(word))
            word = word.substring(0, word.length() - 1);
        if (word.length() > 0) {
            char firstChar = word.charAt(0);
            if (firstChar == '\'' || firstChar == '‘' || firstChar == '’')
                word = word.substring(1);
        }
        return word;
    }

    static boolean isLastCharApostrophe(String str) {
        if (str.length() == 0)
            return false;
        char lastChar = str.charAt(str.length() - 1);
        return lastChar == '’' || lastChar == '\'';
    }
}
