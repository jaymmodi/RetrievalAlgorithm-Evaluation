package Assignment2;


import java.util.PriorityQueue;

public class SearchTRECTopics {


    public void printTop1000Docs(PriorityQueue<QueryScore> queue) {

        while (queue.size() != 0) {
            System.out.println(queue.remove().getScore());
        }
    }


}
