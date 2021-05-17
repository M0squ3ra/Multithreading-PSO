package com.company;

import com.company.Criterias.Criteria;

import java.util.Random;

public class Swarm {
    private int numOfParticles, epochs;
    private double inertia, cognitiveComponent, socialComponent;
    private double bestPosition[];
    private double bestEval;
    private Criteria criteria;

    private int beginRange, endRange;

    public Swarm (Criteria criteria, int particles, int epochs) {
        this(criteria,particles, epochs, DefaulParams.DEFAULT_INERTIA, DefaulParams.DEFAULT_COGNITIVE, DefaulParams.DEFAULT_SOCIAL);
    }

    public Swarm (Criteria criteria,int particles, int epochs, double inertia, double cognitive, double social) {
        this.criteria = criteria;
        this.numOfParticles = particles;
        this.epochs = epochs;
        this.inertia = inertia;
        this.cognitiveComponent = cognitive;
        this.socialComponent = social;
        double worstValue = this.criteria.getWorstValue();
        bestPosition = new double[] {worstValue, worstValue, worstValue};
        bestEval = this.criteria.getWorstValue();
        beginRange = DefaulParams.DEFAULT_BEGIN_RANGE;
        endRange = DefaulParams.DEFAULT_END_RANGE;
    }

    public void run () {
        Particle[] particles = initialize();

        double oldEval = bestEval;
        System.out.println("--------------------------EXECUTING-------------------------");
        System.out.println("New Best Evaluation (Epoch " + 0 + "):\t"  + bestEval);

//        Benchmark this
        for (int i = 0; i < epochs; i++) {

            if (this.criteria.compare(bestEval,oldEval)) {
                System.out.println("New Best Evaluation (Epoch " + (i + 1) + "):\t" + bestEval);
                oldEval = bestEval;
            }

            for (Particle p : particles) {
                p.updatePersonalBest();
                updateGlobalBest(p);
            }

            for (Particle p : particles) {
                updateVelocity(p);
                p.updatePosition();
            }
        }

        System.out.println("---------------------------RESULT---------------------------");
        for (int i = 0; i < this.criteria.getDimension(); i++)
            System.out.println("X" + i + " = " + this.bestPosition[i]);

        System.out.println("Best Evaluation: " + bestEval);
    }

    private Particle[] initialize () {
        Particle[] particles = new Particle[numOfParticles];
        for (int i = 0; i < numOfParticles; i++) {
            Particle particle = new Particle(criteria,beginRange, endRange);
            particles[i] = particle;
            updateGlobalBest(particle);
        }
        return particles;
    }

    private void updateGlobalBest (Particle particle) {
        if (this.criteria.compare(particle.getBestEval(),bestEval)) {
            double a[] = particle.getBestPosition();
            this.bestPosition = particle.getBestPosition();
            this.bestEval = particle.getBestEval();
        }
    }

    private void updateVelocity (Particle particle) {
        double oldVelocity[] = particle.getVelocity();
        double pBest[] = particle.getBestPosition();
        double gBest[] = bestPosition.clone();
        double pos[] = particle.getPosition();

        Random random = new Random();
        double r1 = random.nextDouble();
        double r2 = random.nextDouble();

        double[] newVelocity = oldVelocity.clone();
        Util.mul(newVelocity,inertia,criteria.getDimension());


        Util.sub(pBest,pos,criteria.getDimension());
        Util.mul(pBest,cognitiveComponent,criteria.getDimension());
        Util.mul(pBest,r1,criteria.getDimension());
        Util.add(newVelocity,pBest,criteria.getDimension());

        Util.sub(gBest,pos,criteria.getDimension());
        Util.mul(gBest,socialComponent,criteria.getDimension());
        Util.mul(gBest,r2,criteria.getDimension());
        Util.add(newVelocity,gBest,criteria.getDimension());

        particle.setVelocity(newVelocity);
    }

}
