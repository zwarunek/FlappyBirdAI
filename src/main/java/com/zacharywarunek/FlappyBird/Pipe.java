package com.zacharywarunek.FlappyBird;

import com.zacharywarunek.Engine.Engine;
import com.zacharywarunek.Engine.GameObject;
import com.zacharywarunek.Engine.HitBox;
import com.zacharywarunek.Engine.ID;

import java.awt.*;

import static com.zacharywarunek.FlappyBird.FlappyBirdEngine.panSpeed;

public class Pipe extends GameObject {
    public Pipe(Engine engine, HitBox hitbox, ID id) {
        super(engine, hitbox, id);
        setVelX(-panSpeed);
    }

    @Override
    public void tick() {

        setX(getX() + getVelX());
    }

    @Override
    public void render(Graphics2D g) {
        if(getId() == ID.PipeUpper)
            g.drawImage((FlappyBirdEngine.pipeUpperTexture), (int)hitbox.getX(), (int)(hitbox.getY() - 800 + hitbox.getHeight()), null);
        else if(getId() == ID.PipeLower)
            g.drawImage((FlappyBirdEngine.pipeLowerTexture), (int)hitbox.getX(), (int)(hitbox.getY()), null);
    }
}
