package com.evo.NEAT;

import com.evo.NEAT.genome.Genome;
import com.zacharywarunek.PhysicsGameTest.GObject;

import java.awt.*;

public abstract class Player extends GObject {
    public int score = 0;
    public boolean dead = false;
    public Genome brain = new Genome();
    public float fitness;
    public abstract void look();
    public abstract void think();
    public abstract void calculateFitness();
    public abstract void drawNN(Graphics2D g);


}
