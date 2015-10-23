package Assignment2;

import java.util.Comparator;

public class QueryScore implements Comparator<QueryScore> {

    String query;
    String docNo;
    double score;
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getDocNo() {
        return docNo;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int compare(QueryScore o1, QueryScore o2) {
        if (o1.getScore() < o2.getScore()) {
            return 1;
        }
        if (o1.getScore() > o2.getScore()) {
            return -1;
        }
        return 0;
    }
}
