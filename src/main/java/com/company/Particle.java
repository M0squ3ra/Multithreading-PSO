package com.company;

import com.company.Criterias.Criteria;
import lombok.Data;

import java.util.Random;

@Data
class Particle {

    private Criteria criteria;
    private double position[];
    private double velocity[];
    private double bestPosition[];
    private double bestEval;

    Particle (Criteria criteria,int beginRange, int endRange) {
        this.criteria = criteria;
        if (beginRange >= endRange) {
            throw new IllegalArgumentException("Begin range must be less than end range.");
        }
        this.position = new double[this.criteria.getDimension()];
        setRandomPosition(beginRange, endRange);
        this.bestPosition = this.position.clone();
        this.bestEval = eval();

        this.velocity = new double[this.criteria.getDimension()];
        for (int i = 0; i < this.criteria.getDimension(); i++)
            this.velocity[i] = 0;
    }

    private static int rand (int beginRange, int endRange) {
        Random random = new java.util.Random();
        return random.nextInt(endRange - beginRange) + beginRange;
    }

    private void setRandomPosition(int beginRange, int endRange){
        for (int i = 0; i < this.criteria.getDimension(); i++){
            this.position[i] = rand(beginRange, endRange);
        }
    }

//    Eval current position
    private double eval (){
        return this.criteria.eval(this.position);
    }

    public double updatePersonalBest () {
        double eval = eval();
        if (this.criteria.compare(eval,this.bestEval)) {
            this.bestPosition = position.clone();
            this.bestEval = eval;
        }
        return eval;
    }

    public double[] getBestPosition() {
        return bestPosition.clone();
    }

    void updatePosition () {
        Util.add(this.position,this.velocity,criteria.getDimension());
    }
    void setVelocity (double[] velocity) {
        this.velocity = velocity.clone();
    }

    public void updateVelocity (double[] gBest, double inertia, double cognitiveComponent, double socialComponent) {
        double oldVelocity[] = this.velocity;
        double pBest[] = this.bestPosition;
        double pos[] = this.position;

        Random random = new Random();
        double r1 = random.nextDouble();
        double r2 = random.nextDouble();

        double[] newVelocity = oldVelocity.clone();
        Util.mul(newVelocity,inertia,this.criteria.getDimension());


        Util.sub(pBest,pos,this.criteria.getDimension());
        Util.mul(pBest,cognitiveComponent,this.criteria.getDimension());
        Util.mul(pBest,r1,this.criteria.getDimension());
        Util.add(newVelocity,pBest,criteria.getDimension());

        Util.sub(gBest,pos,this.criteria.getDimension());
        Util.mul(gBest,socialComponent,criteria.getDimension());
        Util.mul(gBest,r2,this.criteria.getDimension());
        Util.add(newVelocity,gBest,criteria.getDimension());

        this.setVelocity(newVelocity);
    }

}
