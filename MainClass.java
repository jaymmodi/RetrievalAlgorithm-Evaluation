package Assignment2;


import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.PriorityQueue;

public class MainClass {

    public static void main(String[] args) {

        byte[] fileArray;
        String fullFileString;
        String indexPath = "/media/jay/New Volume/Jay/IUB/Fall_2015/Search/Programming Assignment/Assignment -2/index";
        EasySearch easySearch = new EasySearch(indexPath);
        try {
            fileArray = Files.readAllBytes(Paths.get("/media/jay/New Volume/Jay/IUB/Fall_2015/Search/Programming Assignment/Assignment -2/topics.51-100"));
            fullFileString = new String(fileArray);

            String titles[] = getAllTitles(fullFileString);
            String description[] = getAllDescription(fullFileString);

//            easySearch.calculateLengthForAllDocs();
//            createTitlesFile(easySearch, titles);
//            createDescriptionFile(easySearch, description);
//            PriorityQueue<QueryScore> queryScores = easySearch.calculateRelevanceScore("New York", 1);
//            SearchTRECTopics searchTRECTopics = new SearchTRECTopics("newyork.txt");
//            searchTRECTopics.printTop1000Docs(queryScores);

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
        SearchTRECTopics searchTRECTopics = new SearchTRECTopics("description.txt");
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
        SearchTRECTopics searchTRECTopics = new SearchTRECTopics("titles.txt");
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
