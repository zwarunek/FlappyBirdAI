package com.evo.NEAT;

/**
 * Created by vishnu on 7/1/17.
 */

import com.evo.NEAT.com.evo.NEAT.config.NEAT_Config;
import com.evo.NEAT.genome.Genome;
import com.evo.NEAT.genome.GenomeUtils;

import java.util.ArrayList;
import java.util.Collections;

public class Pool {

    private ArrayList<Species> species = new ArrayList<>();
    private float lastTopFitness;
    private int poolStaleness = 0;

    public ArrayList<Species> getSpecies() {
        return species;
    }

    public void initializePool() {

        for (int i = 0; i < NEAT_Config.POPULATION; i++) {
            addToSpecies(new Genome());
        }

    }

    public void addToSpecies(Genome g) {
        for (Species s : species) {
            if (s.getGenomes().size() == 0)
                continue;
            Genome g0 = s.getGenomes().get(0);
            if (GenomeUtils.isSameSpecies(g, g0)) {
                s.getGenomes().add(g);
                return;
            }
        }
        Species childSpecies = new Species();
        childSpecies.getGenomes().add(g);
        species.add(childSpecies);
    }

    public void evaluateFitness(Environment environment) {

        ArrayList<Genome> allGenome = new ArrayList<>();

        for (Species s : species) {
            for (Genome g : s.getGenomes()) {
                allGenome.add(g);
            }
        }

        environment.evaluateFitness(allGenome);
//        rankGlobally();
    }

    // experimental
    private void rankGlobally() {                // set fitness to rank
        ArrayList<Genome> allGenome = new ArrayList<>();

        for (Species s : species) {
            for (Genome g : s.getGenomes()) {
                allGenome.add(g);
            }
        }
        Collections.sort(allGenome);

        for (int i = 0; i < allGenome.size(); i++) {
            allGenome.get(i).setPoints(allGenome.get(i).getFitness());      //TODO use adjustedFitness and remove points
            allGenome.get(i).setFitness(i);
        }
    }

    public Genome getTopGenome() {
        ArrayList<Genome> allGenome = new ArrayList<>();

        for (Species s : species) {
            for (Genome g : s.getGenomes()) {
                allGenome.add(g);
            }
        }
        Collections.sort(allGenome, Collections.reverseOrder());

        return allGenome.get(0);
    }

    // all species must have the totalAdjustedFitness calculated
    public float calculateGlobalAdjustedFitness() {
        float total = 0;
        for (Species s : species) {
            total += s.getTotalAdjustedFitness();
        }
        return total;
    }

    public void removeWeakGenomesFromSpecies(boolean allButOne) {
        for (Species s : species) {
            s.removeWeakGenomes(allButOne);
        }
    }

    public void removeStaleSpecies() {
        ArrayList<Species> survived = new ArrayList<>();

        float topFitness = getLastTopFitness();
        if (this.lastTopFitness < topFitness) {
            poolStaleness = 0;
        } else {
            this.lastTopFitness = topFitness;
        }

        for (Species s : species) {
            Genome top = s.getTopGenome();
            if (top.getFitness() > s.getTopFitness()) {
                s.setTopFitness(top.getFitness());
                s.setStaleness(0);
            } else {
                s.setStaleness(s.getStaleness() + 1);     // increment staleness
            }

            if (s.getStaleness() < NEAT_Config.STALE_SPECIES || s.getTopFitness() >= this.getLastTopFitness()) {
                survived.add(s);
            }
        }

        Collections.sort(survived, Collections.reverseOrder());

        if (poolStaleness > NEAT_Config.STALE_POOL) {
            for (int i = survived.size(); i > 1; i--)
                survived.remove(i);
        }

        species = survived;
        poolStaleness++;
    }

    public void calculateGenomeAdjustedFitness() {
        for (Species s : species) {
            s.calculateGenomeAdjustedFitness();
        }
    }

    public ArrayList<Genome> breedNewGeneration() {

        calculateGenomeAdjustedFitness();
        ArrayList<Species> survived = new ArrayList<>();

        removeWeakGenomesFromSpecies(false);
        removeStaleSpecies();
        float globalAdjustedFitness = calculateGlobalAdjustedFitness();
        ArrayList<Genome> children = new ArrayList<>();
        float carryOver = 0;
        for (Species s : species) {
            float fchild = NEAT_Config.POPULATION * (s.getTotalAdjustedFitness() / globalAdjustedFitness);//- 1;       // reconsider
            int nchild = (int) fchild;
            carryOver += fchild - nchild;
            if (carryOver > 1) {
                nchild++;
                carryOver -= 1;
            }

            if (nchild < 1)
                continue;
            Species species = new Species(s.getTopGenome());
            species.color = s.color;
            survived.add(species);

            for (int i = 1; i < nchild; i++) {
                Genome child = s.breedChild();
                children.add(child);
            }

        }
        species = survived;
        for (Genome child : children)
            addToSpecies(child);
        return children;
    }

    public float getLastTopFitness() {
        float topFitness = 0;
        Genome topGenome = null;
        for (Species s : species) {
            topGenome = s.getTopGenome();
            if (topGenome.getFitness() > topFitness) {
                topFitness = topGenome.getFitness();
            }
        }
        return topFitness;
    }

    public int getCurrentPopulation() {
        int p = 0;
        for (Species s : species)
            p += s.getGenomes().size();
        return p;
    }
}
