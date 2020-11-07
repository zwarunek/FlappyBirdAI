package com.evo.NEAT;

import com.evo.NEAT.com.evo.NEAT.config.NEAT_Config;
import com.evo.NEAT.genome.Genome;
import com.evo.NEAT.genome.GenomeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by vishnu on 7/1/17.
 */
public class Species implements Comparable {
    Random rand = new Random();
    private ArrayList<Genome> genomes = new ArrayList<>();
    private float topFitness = 0;
    private int staleness = 0;
    public float color;

    public Species() {
        super();
        this.color = new Random().nextFloat();
    }

    public Species(Genome top) {
        super();
        this.genomes.add(top);
    }

    public void calculateGenomeAdjustedFitness() {
        for (Genome g : genomes) {
            g.setAdjustedFitness(g.getFitness() / genomes.size());
        }
    }

    public float getTotalAdjustedFitness() {
        float totalAdjustedFitness = 0;
        for (Genome g : genomes) {
            totalAdjustedFitness += g.getAdjustedFitness();
        }

        return totalAdjustedFitness;
    }

    private void sortGenomes() {
        //sort internally genomes
        Collections.sort(genomes, Collections.reverseOrder());
    }

    public void removeWeakGenomes(boolean allButOne) {
        sortGenomes();
        int surviveCount = 1;
        if (!allButOne)
            surviveCount = (int) Math.ceil(genomes.size() / 2f);

        ArrayList<Genome> survivedGenomes = new ArrayList<>();
        for (int i = 0; i < surviveCount; i++) {
            survivedGenomes.add(new Genome(genomes.get(i)));
        }
        genomes = survivedGenomes;
    }

    public Genome getTopGenome() {
        sortGenomes();
        return genomes.get(0);
    }

    public Genome breedChild() {
        Genome child;
        if (rand.nextFloat() < NEAT_Config.CROSSOVER_CHANCE) {
            Genome g1 = genomes.get(rand.nextInt(genomes.size()));
            Genome g2 = genomes.get(rand.nextInt(genomes.size()));
            child = GenomeUtils.crossOver(g1, g2);
        } else {
            Genome g1 = genomes.get(rand.nextInt(genomes.size()));
            child = g1;
        }
        child = new Genome(child);
        child.Mutate();

        return child;
    }

    public ArrayList<Genome> getGenomes() {
        return genomes;
    }

    public float getTopFitness() {
        topFitness = getTopGenome().getFitness();
        topFitness = getTopGenome().getFitness();
        return topFitness;
    }

    public void setTopFitness(float topFitness) {
        this.topFitness = topFitness;
    }

    public int getStaleness() {
        return staleness;
    }

    public void setStaleness(int staleness) {
        this.staleness = staleness;
    }

    @Override
    public int compareTo(Object o) {
        Species s = (Species) o;
        float top = getTopFitness();
        float otherTop = s.getTopFitness();

        if (top == otherTop)
            return 0;
        else if (top > otherTop)
            return 1;
        else
            return -1;
    }
}
