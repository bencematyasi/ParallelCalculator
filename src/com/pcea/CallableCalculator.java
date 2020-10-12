package com.pcea;

import java.util.concurrent.Callable;

public class CallableCalculator implements Callable<Long> {
    private final long first;
    private final long last;
    private final long divisor;

    public CallableCalculator(long first, long last, long divisor){
        this.first = first;
        this.last = last;
        this.divisor = divisor;
    }
    @Override
    public Long call() throws Exception {
        return Calculator.calculateNumberOfDivisible(first,last,divisor);
    }
}
