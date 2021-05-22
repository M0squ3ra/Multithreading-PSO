package com.company;

import com.company.Criterias.*;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {

        Criteria criteria = new MultiModal(3);
        int particles = 10000;
        int epochs = 1000;
        double inertia, cognitive, social;
        int numbOfThreads = 2;

        Swarm swarm = new Swarm(criteria,particles,epochs,numbOfThreads);

        swarm.run();
    }
}
