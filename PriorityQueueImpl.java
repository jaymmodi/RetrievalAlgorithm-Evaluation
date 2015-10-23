package Assignment2;


import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.PriorityQueue;

public class PriorityQueueImpl {

    public static void main(String[] args) {
        PriorityQueue<Integer> p = new PriorityQueue<Integer>(5, new DescendingOrder());
        p.add(1);
        p.add(5);
        p.add(2);
        p.add(-1);

        while (p.size() != 0) {
            System.out.println(p.remove());
        }

        try {
            byte[] fileArray = Files.readAllBytes(Paths.get("D:\\Jay\\IUB\\Fall_2015\\Search\\Programming Assignment\\Assignment -2\\topics.51-100"));
            String fullFileString = new String(fileArray);
            String[] titles = StringUtils.substringsBetween(fullFileString, "<title>", "<");
            for (int i = 0; i < titles.length; i++) {
                System.out.println(titles[i].trim());
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static class DescendingOrder implements java.util.Comparator<Integer> {

        public int compare(Integer o1, Integer o2) {
            if (o1 < o2) {
                return 1;
            }
            if (o1 > o2) {
                return -1;
            }
            return 0;
        }
    }

}