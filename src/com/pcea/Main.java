package com.pcea;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    //TODO:Write better comments

    public static final long MAX_NUMBER = 1_000_000_000L;
    private static final long DIVISOR = 3;

    public static void main(String[] args) {
        System.out.println("Sequential execution starting...");
        long startTimeOfSeq = System.currentTimeMillis();
        long seqRes = Calculator.calculateNumberOfDivisible(0, MAX_NUMBER, DIVISOR);
        long endTimeOfSeq = System.currentTimeMillis();
        long elapsedTimeSeq = endTimeOfSeq - startTimeOfSeq;
        System.out.println("Result          : " + seqRes + " calculated under " + elapsedTimeSeq + " milliseconds");

        // Parallel execution
        long resultFuture = 0;
        System.out.println("Starting parallel execution ....");
        long startTimeOfFuture = System.currentTimeMillis();

        // Create a new ExecutorService with all available threads in the machine to execute and store the Futures
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        System.out.println(Runtime.getRuntime().availableProcessors() + " thread(s) available");
        //Create list that will store all the FutureTask
        List<FutureTask<Long>> taskList = new ArrayList<>();
        for (int i = 1; i < Runtime.getRuntime().availableProcessors() +1 ; i++) {
            var last = MAX_NUMBER/(Runtime.getRuntime().availableProcessors());
            var first= ((last*i)-last)+1;
            if(i == 1){
                first = 0;
           }
            //Create as many FutureTask as number of threads are available in the machine
            FutureTask<Long> futureTask = new FutureTask<Long>(new CallableCalculator(first, last*i, DIVISOR));
            //Add this newly created FutureTask to the task list
            taskList.add(futureTask);
            //Execute the FutureTask
            executor.execute(futureTask);
        }

        // Wait until all results are available and combine them at the same time
        for (FutureTask<Long> futureTask : taskList) {
            try {
                resultFuture += futureTask.get(1000,TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Shutdown the ExecutorService
        executor.shutdown();

        long endTimeOfFuture = System.currentTimeMillis();
        long elapsedTimeFuture = endTimeOfFuture - startTimeOfFuture;
        System.out.println("Result (Future): " + resultFuture + " calculated in " + elapsedTimeFuture + " milliseconds");

    }

}
