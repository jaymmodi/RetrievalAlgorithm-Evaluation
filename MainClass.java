package Assignment2;


import Assignment2.Task1AndTask2.EasySearch;
import Assignment2.Task1AndTask2.QueryScore;
import Assignment2.Task1AndTask2.SearchTRECTopics;
import Assignment2.Task3AndTask4.CompareAlgorithms;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.PriorityQueue;

public class MainClass {

    public static void main(String[] args) {

        byte[] fileArray;
        String fullFileString;

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Please path to index : ");

            String indexPath = br.readLine();
            EasySearch easySearch = new EasySearch(indexPath);

            System.out.println("Please enter path to topics.51-100 file.");
            fileArray = Files.readAllBytes(Paths.get(br.readLine()));
            fullFileString = new String(fileArray);

            String titles[] = getAllTitles(fullFileString);
            String description[] = getAllDescription(fullFileString);

            easySearch.calculateLengthForAllDocs();

            createTitlesFile(easySearch, titles);
            createDescriptionFile(easySearch, description);

            PriorityQueue<QueryScore> queryScores = easySearch.calculateRelevanceScore("New York", 1);
            SearchTRECTopics searchTRECTopics = new SearchTRECTopics("newyork.txt");
            searchTRECTopics.printTop1000Docs(queryScores);

            compareAlgorithms(indexPath, titles, "shortQuery.txt");
            compareAlgorithms(indexPath, description, "longQuery.txt");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void compareAlgorithms(String indexPath, String[] queries, String fileSuffix) {
        CompareAlgorithms compareAlgorithms = new CompareAlgorithms(indexPath, new BM25Similarity(), "BM25" + fileSuffix);
        calculateForAllQueries(queries, compareAlgorithms);

        compareAlgorithms = new CompareAlgorithms(indexPath, new DefaultSimilarity(), "DefaultSimilarity" + fileSuffix);
        calculateForAllQueries(queries, compareAlgorithms);

        compareAlgorithms = new CompareAlgorithms(indexPath, new LMDirichletSimilarity(), "LMDirichletSimilarity" + fileSuffix);
        calculateForAllQueries(queries, compareAlgorithms);

        compareAlgorithms = new CompareAlgorithms(indexPath, new LMJelinekMercerSimilarity((float) 0.7), "LMJelinekMercerSimilarity" + fileSuffix);
        calculateForAllQueries(queries, compareAlgorithms);
    }

    private static void calculateForAllQueries(String[] queries, CompareAlgorithms compareAlgorithms) {
        int count = 51;
        for (String query : queries) {
            compareAlgorithms.calculateForGivenAlgorithms(query, count);
            ++count;
        }
        compareAlgorithms.closeResources();
    }

    private static void createDescriptionFile(EasySearch easySearch, String[] description) {
        System.out.println("Creating Description File");
        SearchTRECTopics searchTRECTopics = new SearchTRECTopics("longQuery_tdIdf.txt");
        int id = 51;
        for (String aDescription : description) {
            System.out.println(id);
            PriorityQueue<QueryScore> queryScores = easySearch.calculateRelevanceScore(aDescription.trim(), id);
            searchTRECTopics.printTop1000Docs(queryScores);
            ++id;
        }
        searchTRECTopics.closeResources();
    }

    private static void createTitlesFile(EasySearch easySearch, String[] titles) {
        System.out.println("Creating Titles File");
        SearchTRECTopics searchTRECTopics = new SearchTRECTopics("shortQuery_tfIdf.txt");
        int id = 51;
        for (String title : titles) {
            System.out.println(id);
            PriorityQueue<QueryScore> queryScores = easySearch.calculateRelevanceScore(title.trim(), id);
            searchTRECTopics.printTop1000Docs(queryScores);
            ++id;
        }
        searchTRECTopics.closeResources();
    }

    private static String[] getAllDescription(String fullFileString) {
        return StringUtils.substringsBetween(fullFileString, "Description:", "<");
    }

    private static String[] getAllTitles(String fullFileString) {
        return StringUtils.substringsBetween(fullFileString, "Topic:", "<");
    }
}
