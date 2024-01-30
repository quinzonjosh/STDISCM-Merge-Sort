import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // TODO: Seed your randomizer
        final int SEED = 3001;
        Random random = new Random(SEED);
        int nRandNum = random.nextInt(1000);

        // TODO: Get array size and thread count from user
        final int nArraySize;
        final int nThreadCount;
        final int DEFAULT_THREAD_COUNT = 1;
        Scanner scanner = new Scanner(System.in);

        System.out.print("Input array size:");
        nArraySize = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Input thread count:");
        String input = scanner.nextLine();

        if (input.isEmpty()) {
            nThreadCount = DEFAULT_THREAD_COUNT;
            System.out.println("No input entered. Using default thread count value: " + DEFAULT_THREAD_COUNT);
        } else {
            nThreadCount = Integer.parseInt(input);
        }

        scanner.close();

        // TODO: Generate a random array of given size
        // Initialize array
        int[] arr = new int[nArraySize];
        for(int i=1; i<=nArraySize; i++) {
            arr[i - 1] = i;
        }
        shuffleArray(arr, random);

        // TODO: Call the generate_intervals method to generate the merge
        // sequence

        // TODO: Call merge on each interval in sequence

        // Once you get the single-threaded version to work, it's time to
        // implement the concurrent version. Good luck :)

    }

    // Fisher-Yates shuffling algo
    // from: https://www.geeksforgeeks.org/shuffle-a-given-array-using-fisher-yates-shuffle-algorithm/
    public static void shuffleArray(int[] arr, Random random) {
        for (int i = 0; i < arr.length; i++) {
            int j = random.nextInt(i + 1);

            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }


    /*
    This function generates all the intervals for merge sort iteratively, given
    the range of indices to sort. Algorithm runs in O(n).

    Parameters:
    start : int - start of range
    end : int - end of range (inclusive)

    Returns a list of Interval objects indicating the ranges for merge sort.
    */
    public static List<Interval> generate_intervals(int start, int end) {
        List<Interval> frontier = new ArrayList<>();
        frontier.add(new Interval(start,end));

        int i = 0;
        while(i < frontier.size()){
            int s = frontier.get(i).getStart();
            int e = frontier.get(i).getEnd();

            i++;

            // if base case
            if(s == e){
                continue;
            }

            // compute midpoint
            int m = s + (e - s) / 2;

            // add prerequisite intervals
            frontier.add(new Interval(m + 1,e));
            frontier.add(new Interval(s,m));
        }

        List<Interval> retval = new ArrayList<>();
        for(i = frontier.size() - 1; i >= 0; i--) {
            retval.add(frontier.get(i));
        }

        return retval;
    }

    /*
    This function performs the merge operation of merge sort.

    Parameters:
    array : vector<int> - array to sort
    s     : int         - start index of merge
    e     : int         - end index (inclusive) of merge
    */
    public static void merge(int[] array, int s, int e) {
        int m = s + (e - s) / 2;
        int[] left = new int[m - s + 1];
        int[] right = new int[e - m];
        int l_ptr = 0, r_ptr = 0;
        for(int i = s; i <= e; i++) {
            if(i <= m) {
                left[l_ptr++] = array[i];
            } else {
                right[r_ptr++] = array[i];
            }
        }
        l_ptr = r_ptr = 0;

        for(int i = s; i <= e; i++) {
            // no more elements on left half
            if(l_ptr == m - s + 1) {
                array[i] = right[r_ptr];
                r_ptr++;

                // no more elements on right half or left element comes first
            } else if(r_ptr == e - m || left[l_ptr] <= right[r_ptr]) {
                array[i] = left[l_ptr];
                l_ptr++;
            } else {
                array[i] = right[r_ptr];
                r_ptr++;
            }
        }
    }
}