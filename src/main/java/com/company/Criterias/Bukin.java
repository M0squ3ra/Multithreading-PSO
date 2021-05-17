package com.company.Criterias;

public class Bukin implements Criteria{
    @Override
    public double eval(double[] position) {
        double p1 = 100 * Math.sqrt(Math.abs(position[1] - 0.01 * (position[0]*position[0])));
        double p2 = 0.01 * Math.abs(position[0] + 10);
        return p1 + p2;
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
