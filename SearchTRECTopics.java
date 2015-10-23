package Assignment2;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.PriorityQueue;

public class SearchTRECTopics {


    File file;
    FileWriter fileWriter;
    BufferedWriter bufferedWriter;

    public SearchTRECTopics(String s) {
        try {
            this.file = new File(s);
            this.fileWriter = new FileWriter(file, true);
            this.bufferedWriter = new BufferedWriter(fileWriter);
            writeFirstLine(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeFirstLine(String s) {
        try {
            bufferedWriter.write("QueryID\tQ0\tDocID\t\t\tRank\t\tScore\t\t\t\tRunID");
            bufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void printTop1000Docs(PriorityQueue<QueryScore> queue) {
        int count = 1;
        int rank = 1;
        while (count != 1001) {
            QueryScore queryScore = queue.remove();

            try {
                bufferedWriter.write(String.valueOf(queryScore.getId()) + "\t\t" + "Q0" + "\t" + queryScore.getDocNo() + "\t" + String.valueOf(rank) + "\t\t" + String.valueOf(queryScore.getScore()) + "\t\t" + "run-1");
                bufferedWriter.newLine();
                ++rank;
                ++count;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        System.out.println("Done writing to file");
    }

    public void closeResources() {
        try {
            this.bufferedWriter.close();
            this.fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
