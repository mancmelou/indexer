package org.indexer;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryparser.classic.ParseException;
import org.indexer.indexing.Indexer;
import org.indexer.search.Searcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

abstract class Command {
    /**
     * Create command. Used in creating a new index.
     *
     * @param inputFilePath Path to the input file.
     * @param outputDirPath Path to the output dir where your index will be stored.
     */
    public static void create(String inputFilePath, String outputDirPath) {
        try {
            Long startTime = System.currentTimeMillis();
            Indexer index  = new Indexer(inputFilePath, outputDirPath);

            System.out.print("Creating index " + outputDirPath + " from " + inputFilePath + " ... ");
            index.insert();

            System.out.format("Finished in %.2f seconds.\n", (float) (System.currentTimeMillis() - startTime) / 1000);
        } catch (IOException error) {
            System.out.println(error.getMessage());
            System.exit(1);
        }
    }

    /**
     * Appends documents to an existing index.
     *
     * @param inputFilePath Path to the input file.
     * @param outputDirPath Path to the output dir where your index will be stored.
     */
    public static void append(String inputFilePath, String outputDirPath) {
        try {
            Long startTime = System.currentTimeMillis();
            Indexer index = new Indexer(inputFilePath, outputDirPath, Indexer.OpenMode.APPEND);

            System.out.print("Appending index " + outputDirPath + " with " + inputFilePath + " ... ");
            index.insert();

            System.out.format("Finished in %.2f seconds.\n", (float) (System.currentTimeMillis() - startTime) / 1000);
        } catch (IOException error) {
            System.out.println(error.getMessage());
            System.exit(1);
        }
    }

    /**
     * Drops an existing index.
     *
     * @param outputDirPath Path to the output dir where your index will be stored.
     */
    public static void drop(String outputDirPath) {
        try {
            Long startTime = System.currentTimeMillis();
            System.out.print("Dropping index " + outputDirPath + " ... ");

            Indexer.drop(outputDirPath);
            System.out.format("Finished in %.2f seconds.\n", (float) (System.currentTimeMillis() - startTime) / 1000);
        } catch (IOException error) {
            System.out.println(error.getMessage());
            System.exit(1);
        }
    }

    /**
     * Find command. Searches the index and prints the results in CSV format.
     *
     * @param indexDirPath Path to the input file.
     * @param fields Fields that will be returned e.g. email, id, firstname etc.
     * @param queryString Search criteria, Lucene query.
     */
    public static void find(String indexDirPath, String[] fields, String queryString) {
        try {
            Searcher searcher = new Searcher(indexDirPath);

            ArrayList<LinkedHashMap<String, String>> result = searcher.find(fields, queryString);

            System.out.println(StringUtils.join(result.get(0).keySet().toArray(), ","));

            for (LinkedHashMap<String, String> row : result) {
                System.out.println(StringUtils.join(row.values().toArray(), ","));
            }
        } catch (IOException error) {
            System.out.println(error.getMessage());
            System.exit(1);
        } catch (ParseException error) {
            System.out.println(error.getMessage());
            System.exit(1);
        }
    }

    /**
     * Find command. Searches the index and prints the results in CSV format.
     *
     * @param indexDirPath Path to the input file.
     * @param fields Fields that will be returned e.g. email, id, firstname etc.
     * @param limit Maximum number of results
     * @param queryString Search criteria, Lucene query.
     */
    public static void find(String indexDirPath, String[] fields, String queryString, int limit) {
        try {
            Searcher searcher = new Searcher(indexDirPath);

            ArrayList<LinkedHashMap<String, String>> result = searcher.find(fields, queryString, limit);

            System.out.println(StringUtils.join(result.get(0).keySet().toArray(), ","));

            for (LinkedHashMap<String, String> row : result) {
                System.out.println(StringUtils.join(row.values().toArray(), ","));
            }
        } catch (IOException error) {
            System.out.println(error.getMessage());
            System.exit(1);
        } catch (ParseException error) {
            System.out.println(error.getMessage());
            System.exit(1);
        }
    }

    /**
     * Updates the index with the new content.
     *
     * @param inputFilePath Path to the input file.
     * @param outputDirPath Path to the index dir.
     */
    public static void update(String inputFilePath, String outputDirPath) {
        try {
            Long startTime = System.currentTimeMillis();
            Indexer index = new Indexer(inputFilePath, outputDirPath, Indexer.OpenMode.CREATE_OR_APPEND);

            System.out.print("Updating index " + outputDirPath + " with " + inputFilePath + " ... ");
            index.update();

            System.out.format("Finished in %.2f seconds.\n", (float) (System.currentTimeMillis() - startTime) / 1000);
        } catch (IOException error) {
            System.out.println(error.getMessage());
            System.exit(1);
        }
    }

    /**
     * Command to print the command line utility usage.
     */
    public static void printUsage() {
        System.out.println("Indexer, version 1.0\n");

        System.out.println("Indexes and searches the contents of a CSV file.");
        System.out.println("- The CSV file must have one column named id which is the unique identifier.");

        System.out.println("\nUsage:\n");

        System.out.println("- indexer create [ index/dir ] from [ input.csv ]");
        System.out.println("- indexer append [ index/dir ] from [ input.csv ]");
        System.out.println("- indexer update [ index/dir ] from [ input.csv ]");
        System.out.println("- indexer drop [ index/dir ]");
        System.out.println("- indexer find [ all | n ] return [ \"field1,field2 ...\" ] from [ index/dir ] where [ \"criteria AND criteria ...\"]");
    }

    /**
     * Shorthand for print usage and exit.
     *
     * @param exitCode Exit code to return to the shell process.
     */
    public static void printUsageAndExit(int exitCode) {
        printUsage();
        System.exit(exitCode);
    }
}