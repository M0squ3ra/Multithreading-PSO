package com.company.Criterias;

public class Ackley implements Criteria{
    @Override
    public double eval(double[] position) {
        double p1 = -20*Math.exp(-0.2*Math.sqrt(0.5*((position[0]*position[0])+(position[1]*position[1]))));
        double p2 = Math.exp(0.5*(Math.cos(2*Math.PI*position[0])+Math.cos(2*Math.PI*position[1])));
        return p1 - p2 + Math.E + 20;
    }

    @Override
    public boolean compare(double newEval, double oldEval) {
        return newEval < oldEval;
    }

    @Override
    public double getWorstValue() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public int getDimension() {
        return 2;
    }
}
