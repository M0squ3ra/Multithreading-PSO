package com.company.Criterias;

public class Both implements Criteria{
    @Override
    public double eval(double[] position) {
        double p1 = (position[0] + 2 * position[1] - 7) * (position[0] + 2 * position[1] - 7);
        double p2 = (2 * position[0] + position[1] - 5) * (2 * position[0] + position[1] - 5);
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

    @Override
    public String getName() {
        return "Both";
    }
}
