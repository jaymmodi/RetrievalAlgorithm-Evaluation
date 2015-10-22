package Assignment2;


import java.util.PriorityQueue;

public class MainClass {

    public static void main(String[] args) {
        String queryString = "New York";
        String indexPath = "D:\\Jay\\IUB\\Fall_2015\\Search\\Programming Assignment\\Assignment -2\\index";

        EasySearch easySearch = new EasySearch(indexPath);

        PriorityQueue<QueryScore> queryScores = easySearch.calculateRelevanceScore(queryString);

        SearchTRECTopics searchTRECTopics = new SearchTRECTopics();
        searchTRECTopics.printTop1000Docs(queryScores);
    }
}
