package Assignment2;


public class MainClass {

    public static void main(String[] args) {
        String queryString = "New York";
        String indexPath = "D:\\Jay\\IUB\\Fall_2015\\Search\\Programming Assignment\\Assignment -2\\index\\index";

        EasySearch easySearch = new EasySearch(indexPath);

        easySearch.calculateRelevanceScore(queryString);
    }
}
