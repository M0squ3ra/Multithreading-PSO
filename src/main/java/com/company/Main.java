package com.company;

import com.company.Criterias.*;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Criteria criteria = new Ackley();
        int particles = 10000;
        int epochs = 1000;
        double inertia, cognitive, social;
        int numbOfThreads = 1;

        Swarm swarm = new Swarm(criteria,particles,epochs,numbOfThreads);

        swarm.run();
    }
}
