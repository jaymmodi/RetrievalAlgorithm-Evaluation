package Assignment2;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.PriorityQueue;

public class SearchTRECTopics {


    File file;
    FileWriter fileWriter;
//    BufferedWriter bufferedWriter;

    public SearchTRECTopics(String s) {
        try {
            this.file = new File(s);
            this.fileWriter = new FileWriter(file, true);
//            this.bufferedWriter = new BufferedWriter(fileWriter);
            writeFirstLine(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeFirstLine(String s) {
        try {
//            bufferedWriter.write("QueryID\tQ0\tDocID\t\t\tRank\t\tScore\t\t\t\tRunID");
//            bufferedWriter.newLine();
            fileWriter.write("QueryID\tQ0\tDocID\t\t\tRank\t\tScore\t\t\t\tRunID\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void printTop1000Docs(PriorityQueue<QueryScore> queue) {
        int count = 1;
        System.out.println("count = " + count);
        int rank = 1;

        QueryScore queryScore;
        StringBuilder strToPrint = new StringBuilder("");
        while (count != 1001) {
            queryScore = queue.remove();
            strToPrint = strToPrint.append(String.valueOf(queryScore.getId())).append("\t\t").append("Q0").append("\t").append(queryScore.getDocNo()).append("\t").append(String.valueOf(rank)).append("\t\t").append(String.valueOf(queryScore.getScore())).append("\t\t").append("run-1\n");
            ++rank;
            ++count;
        }
        try {
//            bufferedWriter.write(strToPrint.toString());
            fileWriter.write(strToPrint.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Done writing to file");
    }

    public void closeResources() {
        try {
//            this.bufferedWriter.close();
            this.fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
