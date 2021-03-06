package com.company;

import com.company.Criterias.Criteria;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Swarm {
    private int numOfParticles;
    private int epochs;
    private double inertia, cognitiveComponent, socialComponent;
    private double bestPosition[];
    private double bestEval;
    private Criteria criteria;
    private int beginRange, endRange;

    private Particle[] particles;
    private final Lock updateGlobalBestLock = new ReentrantLock();
    private int numOfThreads;


    public Swarm (Criteria criteria, int particles, int epochs, int numOfThreads) {
        this(criteria,particles, epochs,numOfThreads, DefaulParams.DEFAULT_INERTIA, DefaulParams.DEFAULT_COGNITIVE, DefaulParams.DEFAULT_SOCIAL);
    }

    public Swarm (Criteria criteria,int particles, int epochs, int numOfThreads, double inertia, double cognitive, double social) {
        this.criteria = criteria;
        this.numOfParticles = particles;
        this.epochs = epochs;
        this.numOfThreads = numOfThreads;
        this.inertia = inertia;
        this.cognitiveComponent = cognitive;
        this.socialComponent = social;
        double worstValue = criteria.getWorstValue();
        this.bestPosition = new double[] {worstValue, worstValue, worstValue};
        this.bestEval = criteria.getWorstValue();
        this.beginRange = DefaulParams.DEFAULT_BEGIN_RANGE;
        this.endRange = DefaulParams.DEFAULT_END_RANGE;
    }

    public void run () throws InterruptedException, BrokenBarrierException {
        this.particles = initializeParticles();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(this.numOfThreads);
        executor.prestartAllCoreThreads();
        final CyclicBarrier barrier = new CyclicBarrier(this.numOfThreads + 1);

        List<Particle[]> particlesArray = initializeParticlesArrays();

        double oldEval = this.bestEval;
        System.out.println("--------------------------EXECUTING-------------------------");
        System.out.println("New Best Evaluation (Epoch " + 0 + "):\t"  + bestEval);

        long t1 = System.nanoTime();
        for (int i = 0; i < epochs; i++) {
            if (this.criteria.compare(this.bestEval, oldEval)) {
                System.out.println("New Best Evaluation (Epoch " + (i + 1) + "):\t" + this.bestEval);
                oldEval = this.bestEval;
            }

            barrier.reset();
            for (Particle[] p: particlesArray){
                executor.execute(
                        new Runnable(){
                            @SneakyThrows
                            @Override
                            public void run() {
                                firstCalc(p,barrier);
                            }
                        }
                );
            }
            barrier.await();
            barrier.reset();
            for (Particle[] p: particlesArray){
                executor.execute(
                        new Runnable(){
                            @SneakyThrows
                            @Override
                            public void run() {
                                secondCalc(p,barrier);
                            }
                        }
                );
            }
            barrier.await();
        }
        long t2 = System.nanoTime();

        executor.shutdownNow();

        System.out.println("---------------------------RESULT---------------------------");
        System.out.println("Objective Funcion: " + this.criteria.getName());
        for (int i = 0; i < this.criteria.getDimension(); i++)
            System.out.println("X" + i + " = " + this.bestPosition[i]);
        System.out.println("Best Evaluation: " + this.bestEval);
//        Performance
        System.out.println("------------------------PERFORMANCE------------------------");
        System.out.println("Number of Threads: " + this.numOfThreads);
        System.out.println("Number of Particles: " + this.particles.length);
        System.out.println("Epochs: " + this.epochs);
        System.out.println("Execution Time: " + ((double)((double)(t2-t1) / 100000)/10000) + " sec") ;
    }

    public void firstCalc(Particle[] particles, CyclicBarrier barrier) throws BrokenBarrierException, InterruptedException {
        Particle auxBest = particles[0];
        double auxBestEval = this.criteria.getWorstValue();
        double auxEval = auxBestEval;

        for (Particle p: particles){
            auxEval = p.updatePersonalBest();
            if (auxEval < auxBestEval){
                auxBestEval = auxEval;
                auxBest = p;
            }
        }
        this.updateGlobalBest(auxBest);
        barrier.await();
    }
    public void secondCalc(Particle[] particles, CyclicBarrier barrier) throws BrokenBarrierException, InterruptedException {
        double gBest[] = getBestPosition();
        for (Particle p: particles) {
            p.updateVelocity(gBest,this.inertia,this.cognitiveComponent,this.socialComponent);
            p.updatePosition();
        }
        barrier.await();
    }

    public double[] getBestPosition() {
        return this.bestPosition.clone();
    }

    private List<Particle[]> initializeParticlesArrays(){
        List<Particle[]> particlesArray = new ArrayList<Particle[]>();

        int n = this.numOfParticles / this.numOfThreads;

        Particle threadParticles[];
        for (int i = 0; i < this.numOfThreads - 1; i++) {
            threadParticles = new Particle[n];
            for (int j = 0; j < n; j++)
                threadParticles[j] = this.particles[j + (i * n)];

            particlesArray.add(threadParticles);
        }

        threadParticles = new Particle[this.numOfParticles - (this.numOfThreads - 1) * n];
        for (int j = n * (this.numOfThreads - 1); j < this.numOfParticles; j++)
            threadParticles[j - ( n * (this.numOfThreads - 1))] = this.particles[j];

        particlesArray.add(threadParticles);

        return particlesArray;
    }

    private Particle[] initializeParticles() {
        Particle[] particles = new Particle[this.numOfParticles];
        for (int i = 0; i < this.numOfParticles; i++) {
            Particle particle = new Particle(this.criteria,this.beginRange, this.endRange);
            particles[i] = particle;
            updateGlobalBest(particle);
        }
        return particles;
    }

    public void updateGlobalBest (Particle particle) {
        this.updateGlobalBestLock.lock();
        try {
            if (this.criteria.compare(particle.getBestEval(),this.bestEval)) {
                double a[] = particle.getBestPosition();
                this.bestPosition = particle.getBestPosition();
                this.bestEval = particle.getBestEval();
            }
        } finally {
            this.updateGlobalBestLock.unlock();
        }
    }

}
