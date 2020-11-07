package com.zacharywarunek.FlappyBird;

import com.zacharywarunek.Engine.Engine;
import com.zacharywarunek.Engine.GameObject;
import com.zacharywarunek.Engine.HitBox;
import com.zacharywarunek.Driver;
import com.zacharywarunek.Engine.ID;
import org.warunek.kettering.Engine.*;

import java.awt.*;
import java.util.Random;

public class PipePair extends GameObject {
    Pipe upper, lower;
    public boolean nextPipe = false;
    int opening, openSize = 170,minSize = 10;
    public PipePair lastPipes;
    public PipePair(Engine engine, HitBox hitbox, ID id, PipePair lestPipes) {
        super(engine, hitbox, id);
        setVelX(-FlappyBirdEngine.panSpeed);
        this.lastPipes = lestPipes;
        Random r = new Random();
        opening = r.nextInt(lastPipes == null ? engine.HEIGHT - (openSize + minSize*2): Math.min((int)lastPipes.lower.getY(), engine.HEIGHT - (openSize + minSize*2)));
        upper = new Pipe(engine, new HitBox(getHitbox().getX(),0 , getHitbox().getWidth(), opening +minSize), ID.PipeUpper);
        lower = new Pipe(engine, new HitBox(getHitbox().getX(),upper.getHitbox().getHeight() + openSize , getHitbox().getWidth(), Engine.HEIGHT - (upper.getHitbox().getHeight() + openSize)), ID.PipeLower);
    }

    @Override
    public void tick(){

        setX(getX()+ getVelX());
        upper.tick();
        lower.tick();
        if(getX() + getHitbox().getWidth() <= 0){
            ((FlappyBirdEngine)Driver.engine).remove(this);
        }
    }

    @Override
    public void render(Graphics2D g) {
        upper.render(g);
        lower.render(g);
//        drawHitbox(g);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
