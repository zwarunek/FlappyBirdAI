package com.zacharywarunek.Engine;


import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;

public abstract class Handle<E> {
    HashMap<ID, HashSet<E>> map = new HashMap<>();

    public abstract void tick();
    public abstract void render(Graphics2D g);
    public abstract void addObject(E object);
    public abstract void removeObject(E object);
    public abstract void replaceObject(E oldObject, E newObject);
}
