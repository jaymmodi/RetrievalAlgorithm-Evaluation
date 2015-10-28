package Assignment2;


import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.PriorityQueue;

public class MainClass {

    public static void main(String[] args) {

        byte[] fileArray;
        String fullFileString;
        String indexPath = "index";
        EasySearch easySearch = new EasySearch(indexPath);
        try {
            fileArray = Files.readAllBytes(Paths.get("topics.51-100"));
            fullFileString = new String(fileArray);

            String titles[] = getAllTitles(fullFileString);
            String description[] = getAllDescription(fullFileString);

            easySearch.calculateLengthForAllDocs();
            createTitlesFile(easySearch, titles);
            createDescriptionFile(easySearch, description);
//            PriorityQueue<QueryScore> queryScores = easySearch.calculateRelevanceScore("New York", 1);
//            SearchTRECTopics searchTRECTopics = new SearchTRECTopics("newyork.txt");
//            searchTRECTopics.printTop1000Docs(queryScores);

        } catch (IOException e) {
            e.printStackTrace();
        }

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
