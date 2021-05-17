package com.company;

public class Util {
    private static void limit(double[] v, int n){
        double limit = Double.MAX_VALUE;
        long sum = 0;
        for (int i = 0; i < n; i++)
            sum += v[i]*v[i];
        double m = Math.sqrt(sum);
        if (m > limit) {
            double ratio = (double)(m / limit);
            for (int i = 0; i < n; i++)
                v[i] /= ratio;
        }
    }

    public static void add(double[] v1,double[] v2, int n){
        for (int i = 0; i < n; i++)
            v1[i] += v2[i];
        limit(v1,n);
    }
    public static void sub(double[] v1,double[] v2, int n){
        for (int i = 0; i < n; i++)
            v1[i] -= v2[i];
        limit(v1,n);
    }
    public static void mul(double[] v,double c, int n){
        for (int i = 0; i < n; i++)
            v[i] *= c;
        limit(v,n);
    }
    public static void div(double[] v,double c, int n){
        for (int i = 0; i < n; i++)
            v[i] /= c;
        limit(v,n);
    }
}
