import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main2 {
    public static void main(String[] args) {
        // TODO: Seed your randomizer
        long seed = 12345; // Set a constant seed value
        Random random = new Random(seed);

        // TODO: Get array size and thread count from user
        final int nArraySize;
        final int nThreadCount;
        final int DEFAULT_THREAD_COUNT = 1;
        Scanner scanner = new Scanner(System.in);

        System.out.print("Input array size: ");
        nArraySize = scanner.nextInt();
        scanner.nextLine();  // Consume the newline since nextInt only captures the integer

        System.out.print("Input thread count: ");
        String input = scanner.nextLine();

            if (input.isEmpty()) {
                nThreadCount = DEFAULT_THREAD_COUNT;
                System.out.println("No input entered. Using default thread count value: " + DEFAULT_THREAD_COUNT);
            } else {
                nThreadCount = Integer.parseInt(input);
            }
        scanner.close();

        // TODO: Generate a random array of given size
        int[] array = generateRandomArray(nArraySize, random);

        // TODO: Call the generate_intervals method to generate the merge sequence
        List<Interval> intervals = generate_intervals(0, array.length - 1);
        Set<Interval> done = new HashSet<>();
//        for (Interval interval : intervals) {
//            System.out.println(interval.getStart() + "," + interval.getEnd());
//        }


        long startTime, endTime;

//        // TODO: Call merge on each interval in sequence
        ExecutorService executor = Executors.newFixedThreadPool(nThreadCount);
        startTime = System.currentTimeMillis();

        int counter = 0;
        while (done.size() < intervals.size()){
            if (counter >= intervals.size()){
                counter = 0;
            }
            else {
                Interval temp = intervals.get(counter);

                //divide the pair into two (start, mid) (mid + 1, end)
                List<Interval> subIntervals = generate_intervals((int)temp.getStart(), (int)temp.getEnd());
                subIntervals.remove(subIntervals.size() - 1);


                if ((int) temp.getStart() == (int) temp.getEnd()){
                    executor.submit(() -> merge(array, (int)temp.getStart(), (int)temp.getEnd()));
                    done.add(temp);
                }
                else if (done.containsAll(subIntervals)){
                    executor.submit(() -> merge(array, (int)temp.getStart(), (int)temp.getEnd()));
                    done.add(temp);
                }

                counter++;

            }


        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        endTime = System.currentTimeMillis();
        long durationConcurrent = endTime - startTime;

//        System.out.println(" Sorted  array: " + Arrays.toString(array));
        System.out.println("Concurrent merge sort took " + durationConcurrent + " ms.");
        // Sanity check
        if (isSorted(array)) {
            System.out.println("Array is sorted.");
        } else {
            System.out.println("Array is not sorted.");
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
            int s = (int) frontier.get(i).getStart();
            int e = (int) frontier.get(i).getEnd();

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
    /*
    This function generates a random array of given size.

    Parameters:
    size : int - size of the array
    random : Random - random number generator object

    Returns a random array of integers.
    */
    public static int[] generateRandomArray(int size, Random random) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = i + 1; // Fill the array with integers from 1 to N
        }
        shuffleArray(array, random);
        return array;
    }

    /*
    This function shuffles the elements of an array using the Fisher-Yates algorithm.

    Parameters:
    array : int[] - array to shuffle
    random : Random - random number generator object
    */
    public static void shuffleArray(int[] array, Random random) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
    /*
    This function checks if an array is sorted in ascending order.

    Parameters:
    array : int[] - array to check

    Returns true if the array is sorted, false otherwise.
    */
    public static boolean isSorted(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                return false;
            }
        }
        return true;
    }
}
