package com.company.Criterias;

public interface Criteria {
    public double eval(double[] position);
//    MAX or MIN criteria
    public boolean compare(double newEval, double oldEval);
    public double getWorstValue();
    public int getDimension();
    public String getName();
}
