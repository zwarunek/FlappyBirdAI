package com.evo.NEAT;

import com.evo.NEAT.Environment;
import com.evo.NEAT.genome.Genome;
import com.evo.NEAT.Pool;

import java.util.ArrayList;

/**
 * Created by vishnughosh on 05/03/17.
 */
public class XOR implements Environment {
    @Override
    public void evaluateFitness(ArrayList<Genome> population) {

        for (Genome gene: population) {
            float fitness = 0;
            gene.setFitness(0);
            for (int i = 0; i < 2; i++)
                for (int j = 0; j < 2; j++) {
                    float inputs[] = {i, j};
                    float output[] = gene.evaluateNetwork(inputs);
                    int expected = i^j;
                    fitness +=  (1 - Math.abs(expected - output[0]));
                }
            fitness = fitness * fitness;

            gene.setFitness(fitness);

        }

    }

    public static void main(String[] arg0){
        XOR xor = new XOR();

        Pool pool = new Pool();
        pool.initializePool();

        Genome topGenome;
        int generation = 0;
        while(true){
            pool.evaluateFitness(xor);
            topGenome = pool.getTopGenome();
            System.out.println("TopFitness : " + topGenome.getPoints());

            if(topGenome.getPoints()>15){
                break;
            }
            System.out.println("Population : " + pool.getCurrentPopulation() );
            System.out.println("Generation : " + generation );

            pool.breedNewGeneration();
            generation++;

        }
        System.out.println(topGenome.evaluateNetwork(new float[]{1,0})[0]);
    }
}
