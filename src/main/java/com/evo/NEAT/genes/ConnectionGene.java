package com.evo.NEAT.genes;

/**
 * ConnectionGene Represents the connection(Axon) of the neuron
 * ConnectionGenes can completely represent the neuron as Nodes are generated while performing operation
 * Created by vishnughosh on 28/02/17.
 */
public class ConnectionGene {

    private int source, target, innovation;
    private float weight;
    private boolean enabled;

    public ConnectionGene(int source, int target, int innovation, float weight, boolean enabled) {
        this.source = source;
        this.target = target;
        this.innovation = innovation;
        this.weight = weight;
        this.enabled = enabled;
    }

    public ConnectionGene(ConnectionGene connectionGene) {
        if (connectionGene != null) {
            this.source = connectionGene.getSource();
            this.target = connectionGene.getTarget();
            this.innovation = connectionGene.getInnovation();
            this.weight = connectionGene.getWeight();
            this.enabled = connectionGene.isEnabled();
        }
    }

    public int getSource() {
        return source;
    }

    public int getTarget() {
        return target;
    }

    public int getInnovation() {
        return innovation;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return source + "," + target + "," + innovation + "," +  weight + "," + enabled;
    }
}
