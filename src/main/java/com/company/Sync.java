package com.company;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Sync {
    private int numThreads;
    private int finishCounter;
    private Lock finishCounterLock;
    private boolean done;
    private boolean nextCalc;

    public Sync(int numThreads) {
        this.numThreads = numThreads;
        this.finishCounter = 0;
        this.finishCounterLock = new ReentrantLock();
        this.done = false;
    }

    public boolean allThreadsFinished(){
        this.finishCounterLock.lock();
        try {
            if (this.finishCounter == this.numThreads){
                resetFinishCounter();
                return true;
            }
            return false;
        }finally {
            this.finishCounterLock.unlock();
        }

    }

    public void resetFinishCounter(){
        finishCounterLock.lock();
        try{
            this.finishCounter = 0;
            notifyAll();
        } finally {
            finishCounterLock.unlock();
        }
    }

    public void finish(){
        finishCounterLock.lock();
        try{
            this.finishCounter++;
            notifyAll();
        } finally {
            finishCounterLock.unlock();
        }
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isNextCalc() {
        return nextCalc;
    }

    public void setNextCalc(boolean nextCalc){
        this.nextCalc = nextCalc;
        notifyAll();
    }

}
