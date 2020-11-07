package com.zacharywarunek.Engine;

import com.sun.javafx.geom.*;

public class HitBox{
    double x;
    double y;
    double width;
    double height;
    public Vec2d bottomLeft, bottomRight, topLeft, topRight, center;
    double theta;

    public HitBox(double x, double y, double width, double height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        bottomLeft = new Vec2d(x, y + height);
        bottomRight = new Vec2d(x + width, y + height);
        topLeft = new Vec2d(x, y);
        topRight = new Vec2d(x + width, y);
        center = new Vec2d(x + width/2 , y + height/2);
    }

    public void setX(double x){
        this.x = x;
    }
    public void setY(double y){
        this.y = y;
    }
    public void setWidth(double width){
        this.width = width;
    }
    public void setHeight(double height){
        this.height = height;
    }
    public void setTheta(double theta){
        this.theta = theta;
    }


    public double getHeight() {
        return height;
    }
    public double getWidth() {
        return width;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getTheta(){
        return this.theta;
    }
    public boolean intersects(HitBox r) {
        double tw = this.width;
        double th = this.height;
        double rw = r.width;
        double rh = r.height;
        if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
            return false;
        }
        double tx = this.x;
        double ty = this.y;
        double rx = r.x;
        double ry = r.y;
        rw += rx;
        rh += ry;
        tw += tx;
        th += ty;
        //      overflow || intersect
        return ((rw < rx || rw > tx) &&
                (rh < ry || rh > ty) &&
                (tw < tx || tw > rx) &&
                (th < ty || th > ry));
    }
}
