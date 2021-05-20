package com.company;

import com.company.Criterias.Criteria;
import lombok.SneakyThrows;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

public class ParticleCalculator implements Runnable{
    private Sync sync;
    private Swarm swarm;
    private double inertia;
    private Criteria criteria;
    private double cognitiveComponent;
    private double socialComponent;
    private Particle particles[];


    public ParticleCalculator(Sync sync,Swarm swarm, double inertia, Criteria criteria, double cognitiveComponent, double socialComponent, Particle[] particles) {
        this.sync = sync;
        this.swarm = swarm;
        this.inertia = inertia;
        this.criteria = criteria;
        this.cognitiveComponent = cognitiveComponent;
        this.socialComponent = socialComponent;
        this.particles = particles;
    }

    @SneakyThrows
    @Override
    public void run() {
        synchronized (this.sync) {
            System.out.println("Thread " + Thread.currentThread().getId() + " is running");

            sync.finish();
            double gBest[];
            Particle auxBest = particles[0];
            double auxBestEval = criteria.getWorstValue();
            double auxEval = criteria.getWorstValue();
            while (!sync.isDone()){
                while (!sync.isNextCalc()) {
                    sync.wait();
                }

                for (Particle p: particles){
                    auxEval = p.updatePersonalBest();
                    if (auxEval < auxBestEval){
                        auxBestEval = auxEval;
                        auxBest = p;
                    }
                }
                swarm.updateGlobalBest(auxBest);

                sync.finish();

                while (sync.isNextCalc()) {
                    sync.wait();
                }

                gBest = swarm.getBestPosition();
                for (Particle p: particles) {
                    p.updateVelocity(gBest,this.inertia,this.cognitiveComponent,this.socialComponent);
                    p.updatePosition();
                }
                sync.finish();
            }
        }
    }



}
