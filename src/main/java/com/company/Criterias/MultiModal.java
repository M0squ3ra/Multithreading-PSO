package com.company.Criterias;

public class MultiModal implements Criteria{
    private int n;

    public MultiModal(int n) {
        this.n = n;
    }

    @Override
    public double eval(double[] position) {
        double p1 = 0;
        double p2 = 1;
        for (int i = 0; i < this.n; i++){
            p1 += Math.abs(position[i]);
            if (position[i] != 0)
                p2 *= Math.abs(position[i]);
        }

        return p1 * p2;
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

    @Override
    public String getName() {
        return "MultiModal";
    }
}
