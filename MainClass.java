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
        String indexPath = "D:\\Jay\\IUB\\Fall_2015\\Search\\Programming Assignment\\Assignment -2\\index";
        EasySearch easySearch = new EasySearch(indexPath);
        try {
            fileArray = Files.readAllBytes(Paths.get("D:\\Jay\\IUB\\Fall_2015\\Search\\Programming Assignment\\Assignment -2\\topics.51-100"));
            fullFileString = new String(fileArray);

            String titles[] = getAllTitles(fullFileString);
            String description[] = getAllDescription(fullFileString);

            easySearch.calculateLengthForAllDocs();
            createTitlesFile(easySearch, titles);
            createDescriptionFile(easySearch, description);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void createDescriptionFile(EasySearch easySearch, String[] description) {
        System.out.println("Creating Description File");
        SearchTRECTopics searchTRECTopics = new SearchTRECTopics("description.txt");
        int id = 51;
        for (int i = 0; i < description.length; i++) {
            PriorityQueue<QueryScore> queryScores = easySearch.calculateRelevanceScore(description[i].trim(), id);
            searchTRECTopics.printTop1000Docs(queryScores);
            ++id;
        }
        searchTRECTopics.closeResources();
    }

    private static void createTitlesFile(EasySearch easySearch, String[] titles) {
        System.out.println("Creating Titles File");
        SearchTRECTopics searchTRECTopics = new SearchTRECTopics("titles.txt");
        int id = 51;
        for (int i = 0; i < titles.length; i++) {
            PriorityQueue<QueryScore> queryScores = easySearch.calculateRelevanceScore(titles[i].trim(), id);
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
