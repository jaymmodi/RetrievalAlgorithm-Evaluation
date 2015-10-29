package Assignment2.Task1AndTask2;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Stack;

public class SearchTRECTopics {


    File file;
    FileWriter fileWriter;

    public SearchTRECTopics(String s) {
        try {
            this.file = new File(s);
            this.fileWriter = new FileWriter(file, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printTop1000Docs(PriorityQueue<QueryScore> queue) {
        int rank = 1;

        QueryScore queryScore;
        StringBuilder strToPrint = new StringBuilder("");
        Stack<QueryScore> stack = new Stack<QueryScore>();

        while (!queue.isEmpty()) {
            queryScore = queue.remove();
            stack.push(queryScore);
        }
        while (!stack.isEmpty()) {
            queryScore = stack.pop();
            strToPrint = strToPrint.append(String.valueOf(queryScore.getId())).append("\t\t").append("Q0").append("\t").append(queryScore.getDocNo()).append("\t").append(String.valueOf(rank)).append("\t\t").append(String.valueOf(queryScore.getScore())).append("\t\t").append("run-1\n");
            ++rank;
        }
        try {
            fileWriter.write(strToPrint.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeResources() {
        try {
            this.fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
