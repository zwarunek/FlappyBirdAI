package com.evo.NEAT;

import com.evo.NEAT.genome.Genome;
import com.zacharywarunek.Engine.Engine;
import com.zacharywarunek.Engine.GameObject;
import com.zacharywarunek.Engine.HitBox;
import com.zacharywarunek.Engine.ID;

import java.awt.*;

public abstract class BirdPlayer extends GameObject {
    public BirdPlayer(Engine engine, HitBox hitbox, ID id) {
        super(engine, hitbox, id);
    }
    public int score = 0;
    public boolean dead = false;
    public Genome brain = new Genome();
    public float fitness = 0;
    public abstract void tick();
    public abstract void render(Graphics2D g);
    public abstract void look();
    public abstract void think();
    public abstract void calculateFitness();
    public abstract void drawNN(Graphics2D g);

    public abstract boolean isDead();
}
