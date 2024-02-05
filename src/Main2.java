import java.util.*;
import java.util.concurrent.*;

public class Main2 {
    public static void main(String[] args) {
        long startTimeSingle, endTimeSingle, startTimeMulti, endTimeMulti;

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

        // Call merge on each interval in sequence (single-threaded version)
        int[] arraySingleThreaded = Arrays.copyOf(array, array.length);
        startTimeSingle = System.currentTimeMillis();
        for (Interval interval : intervals) {
            merge(arraySingleThreaded, interval.getStart(), interval.getEnd());
        }
        endTimeSingle = System.currentTimeMillis();
        long durationSingleThreaded = endTimeSingle - startTimeSingle;
        System.out.println("Single-threaded merge sort took " + durationSingleThreaded + " ms.");

        // Sanity check (Single-threaded)
        if (isSorted(arraySingleThreaded)) {
            System.out.println("Singlethreaded Array is sorted.");
        } else {
            System.out.println("Singlethreaded Array is not sorted.");
        }

        // TODO: Call merge on each interval in sequence
        int[] arrayMultiThreaded = Arrays.copyOf(array, array.length);
        // Executor service for concurrency
        ExecutorService executor = Executors.newFixedThreadPool(nThreadCount);

        // Map to keep track of futures representing sorted intervals
        Map<Interval, Future<?>> futures = new HashMap<>();

        startTimeMulti = System.currentTimeMillis();

        // Submit initial sorting tasks for each interval
        for (Interval interval : intervals) {
            Future<?> future = executor.submit(() -> {
                try {
                    // If the interval is more than one element, wait for the left and right halves to be sorted
                    if (interval.getStart() < interval.getEnd()) {
                        int middle = interval.getStart() + (interval.getEnd() - interval.getStart()) / 2;
                        Interval left = new Interval(interval.getStart(), middle);
                        Interval right = new Interval(middle + 1, interval.getEnd());
                        Future<?> leftFuture = futures.get(left);
                        Future<?> rightFuture = futures.get(right);

                        if (leftFuture != null) leftFuture.get(); // wait for left half to be sorted
                        if (rightFuture != null) rightFuture.get(); // wait for right half to be sorted
                    }

                    // Perform the merge on this interval
                    merge(arrayMultiThreaded, interval.getStart(), interval.getEnd());
                } catch (InterruptedException | ExecutionException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            });

            // Store the future
            futures.put(interval, future);
        }

        // Shut down the executor service and await termination of all tasks
        executor.shutdown();
        try {
            if (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        endTimeMulti = System.currentTimeMillis();
        long durationMultiThreaded = endTimeMulti - startTimeMulti;
        System.out.println("Multi-threaded merge sort took " + durationMultiThreaded + " ms.");
        
        // Sanity check (Multi-threaded)
        if (isSorted(arrayMultiThreaded)) {
            System.out.println("Multithreaded Array is sorted.");
        } else {
            System.out.println("Multithreaded Array is not sorted.");
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
