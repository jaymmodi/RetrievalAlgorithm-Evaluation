package Assignment2;


import java.util.PriorityQueue;

public class PriorityQueueImpl {

    public static void main(String[] args) {
        PriorityQueue<Integer> p = new PriorityQueue<Integer>(5, new DescendingOrder());
        p.add(1);
        p.add(5);
        p.add(2);
        p.add(-1);

        while (p.size() != 0)
        {
            System.out.println(p.remove());
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