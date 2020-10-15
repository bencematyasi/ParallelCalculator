package com.pcea;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class Main {
    //TODO:Write better comments

    public static final long MAX_NUMBER = 5_000_000_000L;
    private static final long DIVISOR = 3;

    public static void main(String[] args) {
        System.out.println("Sequential execution starting...");
        SequentialRun();
        System.out.println("----------------------------------------");
        System.out.println("Parallel execution is starting...");
        ParallelRun();
    }
    private static void SequentialRun(){
        double start = System.currentTimeMillis();
        long result = Calculator.calculateNumberOfDivisible(1, MAX_NUMBER, DIVISOR);
        double stop = System.currentTimeMillis();
        double seconds = (stop-start)/ 1000.000d;
        System.out.println("Result: " + result + " calculated under " + seconds + " seconds");
    }
    private static void ParallelRun(){
        AtomicLong result = new AtomicLong();
        double start = System.currentTimeMillis();

        // Create a new ExecutorService and set all available threads in the machine to be used in its ThreadPool
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        //Create list that will store all the FutureTask
        List<FutureTask<Long>> taskList = new ArrayList<>();
        for (int i = 1; i < Runtime.getRuntime().availableProcessors() +1 ; i++) {
            var fraction = MAX_NUMBER/(Runtime.getRuntime().availableProcessors());
            var first= ((fraction*i)-fraction)+1;
            var last = fraction * i;

            //Create as many FutureTask as number of threads are available in the machine
            FutureTask<Long> futureTask = new FutureTask<Long>(new CallableCalculator(first, last, DIVISOR));
            //Add this newly created FutureTask to the task list
            taskList.add(futureTask);
            //Execute the FutureTask
            //executor.execute(futureTask);
        }
        /* Execute/start each task, the order of execution does not matter*/
        taskList.parallelStream().forEach(task -> executor.execute(task));

        /*Wait for all tasks to be completed and add the tasks' return value/result to the AtomicLong result variable.
        The order of addition does not matter, but to receive all the results the AtomicLong is required to have
        Task will be canceled after 5 seconds of runtime*/
        taskList.parallelStream().forEach(task ->{
            try {
                result.addAndGet(task.get(5000, TimeUnit.MILLISECONDS));
            } catch (CancellationException | InterruptedException | TimeoutException | ExecutionException e) {
                task.cancel(true);
                e.printStackTrace();
            }
        });
        // Shutdown the ExecutorService
        executor.shutdown();

        double stop = System.currentTimeMillis();
        double seconds = (stop - start)/1000.000d;
        System.out.println("Result (Future): " + result + " calculated in " + seconds + " seconds");

    }
}
