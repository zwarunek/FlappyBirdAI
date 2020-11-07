package com.zacharywarunek.Engine;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;

public class Handler{
    HashMap<ID, HashSet<GameObject>> map = new HashMap<>();
    ID[] ids;
    public Handler(ID[] ids){
        this.ids = ids;
    }

    public void tick(){
        for(HashSet<GameObject> set : map.values()) {
            for(GameObject object : set)
                object.tick();
        }
    }

    public void render(Graphics2D g){
        for(ID id : ids)
            if(map.containsKey(id))
                for(GameObject object : map.get(id))
                    object.render(g);
    }

    public void addObject(GameObject object){
        HashSet<GameObject> tempSet;
        if(map.containsKey(object.getId()))
            tempSet = map.get(object.getId());
        else
            tempSet = new HashSet<>();
        tempSet.add(object);
        map.put(object.getId(), tempSet);
    }

    public void removeObject(GameObject object){
        try {
            HashSet<GameObject> tempSet = map.get(object.getId());
            tempSet.remove(object);
            map.replace(object.getId(), tempSet);
        }catch (NullPointerException ignored){}
    }

    public void replaceObject(GameObject oldObject, GameObject newObject){
        HashSet<GameObject> tempSet = map.get(newObject.getId());
        tempSet.remove(oldObject);
        tempSet.add(newObject);
        map.replace(newObject.getId(), tempSet);
    }

    public HashMap<ID, HashSet<GameObject>> getMap(){return map;}
}
