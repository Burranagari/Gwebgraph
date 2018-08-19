package me.tool.gwebneo4j;


import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Stream;


public class Converter {


  /** Check if the given string is a data line.
   *
   * Returns false for comment and empty lines. Note that no further
   * validation (regarding the format of a data line) is performed.
   */
  static boolean isDataLine(String line) {
    // Remove surrounding whitespace
    line = line.trim();
    // Handle empty lines
    if (line.length() == 0) {
      return false;
    }
    // Comment lines start with #, return false for them.
    if (line.charAt(0) == '#') {
      return false;
    }
    // Everything else is a data line.
    return true;
  }


  /** Convert the inFile to Neo4J statements in outFile.
   */
  static void convert(String inFile,
                      String outFile) throws IOException {          //calling input and output file
    try (PrintWriter pw = new PrintWriter(outFile, "UTF-8");
         Stream<String> input = Files.lines(Paths.get(inFile))) {
      input
        .unordered().parallel() // Allow efficient parallel
                                // processing, maintaining order is
                                // not necessary.
        .filter(Converter::isDataLine) // Only process data lines
        .map(line -> {
            // Expected format "<ws><from><ws><to>" with <ws> some
            // kind of whitespace.
            Scanner s = new Scanner(line);
            int from = s.nextInt();
            int to = s.nextInt();
            return
              "WITH count(*) as dummy\n" + // Separates statements to
                                           // allow reuse of variable
                                           // names "a" and "b".
              "MERGE (a:Node {id: " + from + "})\n" + // Create or
                                                      // find.
              "MERGE (b:Node {id: " + to + "})\n" +
              "CREATE (a)-[:LINK]->(b)"; // Create link.
          })
        .forEach(pw::println);
    }
  }


  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      System.err.println("Call with 2 arguments: <infile> <outfile>");
      System.exit(1);
    }
    convert(args[0], args[1]);
  }
}
