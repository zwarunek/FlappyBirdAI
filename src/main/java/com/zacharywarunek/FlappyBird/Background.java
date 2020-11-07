package com.zacharywarunek.FlappyBird;

import com.zacharywarunek.Engine.Engine;
import com.zacharywarunek.Engine.GameObject;
import com.zacharywarunek.Driver;
import com.zacharywarunek.Engine.HitBox;
import com.zacharywarunek.Engine.ID;

import java.awt.*;


public class Background extends GameObject {
    public Background(Engine engine, HitBox hitbox, ID id) {
        super(engine, hitbox, id);
        setVelX(-FlappyBirdEngine.panSpeed/2);
    }

    @Override
    public void tick() {
        setX(getX()+getVelX());
        if(getX() + hitbox.getWidth() <= 0){
            ((FlappyBirdEngine) Driver.engine).addBackground(this);
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.drawImage((FlappyBirdEngine.backgroundTexture), (int)hitbox.getX(), (int)hitbox.getY(), null);
    }
}
