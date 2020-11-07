package com.evo.NEAT.genome;

import com.evo.NEAT.com.evo.NEAT.config.NEAT_Config;
import com.evo.NEAT.genes.ConnectionGene;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

public class GenomeUtils {
    private static final Random rand = new Random();

    public static Genome crossOver(Genome parent1, Genome parent2) {
        if (parent1.getFitness() < parent2.getFitness()) {
            Genome temp = parent1;
            parent1 = parent2;
            parent2 = temp;
        }

        Genome child = new Genome();
        TreeMap<Integer, ConnectionGene> geneMap1 = new TreeMap<>();
        TreeMap<Integer, ConnectionGene> geneMap2 = new TreeMap<>();

        for (ConnectionGene con : parent1.getConnectionGeneList()) {
            assert !geneMap1.containsKey(con.getInnovation());             //TODO Remove for better performance
            geneMap1.put(con.getInnovation(), con);
        }

        for (ConnectionGene con : parent2.getConnectionGeneList()) {
            assert !geneMap2.containsKey(con.getInnovation());             //TODO Remove for better performance
            geneMap2.put(con.getInnovation(), con);
        }

        Set<Integer> innovationP1 = geneMap1.keySet();
        Set<Integer> innovationP2 = geneMap2.keySet();

        Set<Integer> allInnovations = new HashSet<>(innovationP1);
        allInnovations.addAll(innovationP2);

        for (int key : allInnovations) {
            ConnectionGene trait;

            if (geneMap1.containsKey(key) && geneMap2.containsKey(key)) {
                if (rand.nextBoolean()) {
                    trait = new ConnectionGene(geneMap1.get(key));
                } else {
                    trait = new ConnectionGene(geneMap2.get(key));
                }

                if ((geneMap1.get(key).isEnabled() != geneMap2.get(key).isEnabled())) {
                    trait.setEnabled(!(rand.nextFloat() < 0.75f));
                }

            } else if (parent1.getFitness() == parent2.getFitness()) {               // disjoint or excess and equal fitness
                if (geneMap1.containsKey(key))
                    trait = geneMap1.get(key);
                else
                    trait = geneMap2.get(key);

                if (rand.nextBoolean()) {
                    continue;
                }

            } else
                trait = geneMap1.get(key);

            child.getConnectionGeneList().add(trait);
        }

        return child;
    }

    public static boolean isSameSpecies(Genome g1, Genome g2){
        TreeMap<Integer, ConnectionGene> geneMap1 = new TreeMap<>();
        TreeMap<Integer, ConnectionGene> geneMap2 = new TreeMap<>();

        int matching = 0;
        int disjoint = 0;
        int excess = 0;
        float weight = 0;
        int lowMaxInnovation;
        float delta = 0;

        for(ConnectionGene con: g1.getConnectionGeneList()) {
            assert  !geneMap1.containsKey(con.getInnovation());             //TODO Remove for better performance
            geneMap1.put(con.getInnovation(), con);
        }

        for(ConnectionGene con: g2.getConnectionGeneList()) {
            assert  !geneMap2.containsKey(con.getInnovation());             //TODO Remove for better performance
            geneMap2.put(con.getInnovation(), con);
        }
        if(geneMap1.isEmpty() || geneMap2.isEmpty())
            lowMaxInnovation = 0;
        else
            lowMaxInnovation = Math.min(geneMap1.lastKey(),geneMap2.lastKey());

        Set<Integer> innovationP1 = geneMap1.keySet();
        Set<Integer> innovationP2 = geneMap2.keySet();

        Set<Integer> allInnovations = new HashSet<Integer>(innovationP1);
        allInnovations.addAll(innovationP2);

        for(int key : allInnovations){

            if(geneMap1.containsKey(key) && geneMap2.containsKey(key)){
                matching ++;
                weight += Math.abs(geneMap1.get(key).getWeight() - geneMap2.get(key).getWeight());
            }else {
                if(key < lowMaxInnovation){
                    disjoint++;
                }else{
                    excess++;
                }
            }

        }

        int N = matching+disjoint+excess ;

        if(N>0)
            delta = (NEAT_Config.EXCESS_COEFFICENT * excess + NEAT_Config.DISJOINT_COEFFICENT * disjoint) / N + (NEAT_Config.WEIGHT_COEFFICENT * weight) / matching;

        return delta < NEAT_Config.COMPATIBILITY_THRESHOLD;

    }
}
