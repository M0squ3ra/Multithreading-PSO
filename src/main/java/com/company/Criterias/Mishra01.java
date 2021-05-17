package com.company.Criterias;

public class Mishra01 implements Criteria{
    private int n;

    public Mishra01(int n) {
        this.n = n;
    }

    @Override
    public double eval(double[] position) {
        double sum = 0;
        for (int i = 0; i < (this.n - 1); i++)
            sum += position[i];

        double xn = (double) this.n - sum;

        return Math.pow((1 + xn), xn);
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
        return this.n;
    }
}
