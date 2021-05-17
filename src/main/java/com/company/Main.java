package com.company;

import com.company.Criterias.*;

public class Main {

    public static void main(String[] args) {
        Criteria criteria = new Ackley();
        int particles = 100;
        int epochs = 1000;
        double inertia, cognitive, social;

        Swarm swarm = new Swarm(criteria,particles,epochs);

        swarm.run();
    }
}
