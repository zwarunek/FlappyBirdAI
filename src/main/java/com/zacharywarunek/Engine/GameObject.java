package com.zacharywarunek.Engine;

import java.awt.*;

public abstract class GameObject implements GameO {


    protected ID id;
    protected double velX;
    protected double velY;
    protected HitBox hitbox;
    protected Engine engine;

    public GameObject(Engine engine, HitBox hitbox,  ID id){
        this.engine = engine;
        this.id = id;
        this.hitbox = hitbox;
    }

    public abstract void tick();
    public abstract void render(Graphics2D g);

    /*SETTERS*/
    public void setX(double x){hitbox.x = x;}
    public void setY(double y){hitbox.y = y;}
    public void setVelX(double velX){this.velX = velX;}
    public void setVelY(double velY){this.velY = velY;}
    public void setId(ID id){this.id = id;}
    public void setHitbox(HitBox hitbox){this.hitbox = hitbox;}
    /*GETTERS*/
    public double getX(){return hitbox.getX();}
    public double getY(){return hitbox.getY();}
    public double getVelX(){return velX;}
    public double getVelY(){return velY;}
    public ID getId(){return id;}
    public HitBox getHitbox(){return hitbox;}


}
