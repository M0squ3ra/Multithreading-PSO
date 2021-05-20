package com.company;

import com.company.Criterias.Criteria;

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

    private final Lock getParticleLock = new ReentrantLock();
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
        double worstValue = this.criteria.getWorstValue();
        bestPosition = new double[] {worstValue, worstValue, worstValue};
        bestEval = this.criteria.getWorstValue();
        beginRange = DefaulParams.DEFAULT_BEGIN_RANGE;
        endRange = DefaulParams.DEFAULT_END_RANGE;
    }

    public void run () throws InterruptedException {
        this.particles = initializeParticles();
        Sync sync = new Sync(this.numOfThreads);

        Thread threads[] = initializeThreads(sync);
        for (Thread t: threads){
            t.start();
        }

        double oldEval = bestEval;
        System.out.println("--------------------------EXECUTING-------------------------");
//        Benchmark this
        long t1;
        synchronized (sync) {
            while (!sync.allThreadsFinished())
                sync.wait();

            System.out.println("---------------------ALL THREADS RUNNING--------------------");
            System.out.println("New Best Evaluation (Epoch " + 0 + "):\t"  + bestEval);

            t1 = System.nanoTime();
            for (int i = 0; i < epochs; i++) {
                if (this.criteria.compare(bestEval, oldEval)) {
                    System.out.println("New Best Evaluation (Epoch " + (i + 1) + "):\t" + bestEval);
                    oldEval = bestEval;
                }

                sync.setNextCalc(true);
                while (!sync.allThreadsFinished())
                    sync.wait();

                sync.setNextCalc(false);
                while (!sync.allThreadsFinished()) {
                    sync.wait();
                }
            }
        }
        long t2 = System.nanoTime();

        System.out.println("---------------------------RESULT---------------------------");
        for (int i = 0; i < this.criteria.getDimension(); i++)
            System.out.println("X" + i + " = " + this.bestPosition[i]);

        System.out.println("Best Evaluation: " + bestEval);
        System.out.println("Time: " + ((double)((double)(t2-t1) / 100000)/10000) + " sec") ;

        sync.setDone(true);
        for (Thread t: threads)
            t.join();
    }

    public double[] getBestPosition() {
        return this.bestPosition.clone();
    }

    private Thread[] initializeThreads(Sync sync){
        Thread[] threads = new Thread[this.numOfThreads];

        int n = this.numOfParticles / this.numOfThreads;

        Particle threadParticles[];
        for (int i = 0; i < this.numOfThreads - 1; i++) {
            threadParticles = new Particle[n];
            for (int j = 0; j < n; j++)
                threadParticles[j] = this.particles[j + (i * n)];

            threads[i] = new Thread(new ParticleCalculator(sync,this,this.inertia,this.criteria,this.cognitiveComponent,this.socialComponent,threadParticles));
        }
        threadParticles = new Particle[this.numOfParticles - (this.numOfThreads - 1) * n];
        for (int j = n * (this.numOfThreads - 1); j < this.numOfParticles; j++)
            threadParticles[j - ( n * (this.numOfThreads - 1))] = this.particles[j];
        threads[this.numOfThreads - 1] = new Thread(new ParticleCalculator(sync,this,this.inertia,this.criteria,this.cognitiveComponent,this.socialComponent,threadParticles));

        return threads;
    }

    private Particle[] initializeParticles() {
        Particle[] particles = new Particle[numOfParticles];
        for (int i = 0; i < numOfParticles; i++) {
            Particle particle = new Particle(criteria,beginRange, endRange);
            particles[i] = particle;
            updateGlobalBest(particle);
        }
        return particles;
    }

    public void updateGlobalBest (Particle particle) {
        updateGlobalBestLock.lock();
        try {
            if (this.criteria.compare(particle.getBestEval(),bestEval)) {
                double a[] = particle.getBestPosition();
                this.bestPosition = particle.getBestPosition();
                this.bestEval = particle.getBestEval();
            }
        } finally {
            updateGlobalBestLock.unlock();
        }
    }

}
