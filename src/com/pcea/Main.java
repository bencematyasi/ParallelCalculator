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
        long startTime = System.currentTimeMillis();
        long result = Calculator.calculateNumberOfDivisible(0, MAX_NUMBER, DIVISOR);
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Result          : " + result + " calculated under " + elapsedTime + " milliseconds");

        // Parallel execution
        System.out.println("Starting parallel execution ....");
        long timeStartFuture = System.currentTimeMillis();

        long resultFuture = 0;

        // Create a new ExecutorService with all available threads to execute and store the Futures
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        System.out.println(Runtime.getRuntime().availableProcessors() + " thread(s) available");
        //
        List<FutureTask<Long>> taskList = new ArrayList<>();

        for (int i = 1; i < Runtime.getRuntime().availableProcessors() +1 ; i++) {
            var last = MAX_NUMBER/(Runtime.getRuntime().availableProcessors());
            var first= ((last*i)-last)+1;
            if(i == 1){
                first = 0;
           }

            FutureTask<Long> futureTask = new FutureTask<Long>(new CallableCalculator(first, last*i, DIVISOR));
            taskList.add(futureTask);
            executor.execute(futureTask);
        }

        // Wait until all results are available and combine them at the same time
        for (FutureTask<Long> futureTask : taskList) {
            try {
                resultFuture += futureTask.get();
                //TODO:Throw exception if the elapsed time is longer than a given number, will work as a cancellation token

            } catch (InterruptedException | ExecutionException e) {
                futureTask.cancel(true);
                e.printStackTrace();
            }
        }

        // Shutdown the ExecutorService
        executor.shutdown();

        long timeEndFuture = System.currentTimeMillis();
        long timeNeededFuture = timeEndFuture - timeStartFuture;
        System.out.println("Result (Future): " + resultFuture + " calculated in " + timeNeededFuture + " milliseconds");

    }

}
