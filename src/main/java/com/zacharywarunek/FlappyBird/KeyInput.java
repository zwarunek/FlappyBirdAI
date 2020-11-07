package com.zacharywarunek.FlappyBird;

import com.zacharywarunek.Engine.GameObject;
import com.zacharywarunek.Engine.ID;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {
    FlappyBirdEngine flappyBirdEngine;
    boolean reset = true;
    public KeyInput(FlappyBirdEngine flappyBirdEngine){
        this.flappyBirdEngine = flappyBirdEngine;
    }
    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        if(reset && !flappyBirdEngine.pause && !flappyBirdEngine.dead){
            int k = e.getKeyCode();
            if(k == KeyEvent.VK_SPACE) {
                for (GameObject object : flappyBirdEngine.getHandler().getMap().get(ID.Player)) {
                    ((Bird) object).flap();
                }
            }
            reset = false;
        }

        if(e.getKeyCode() == KeyEvent.VK_UP){
            flappyBirdEngine.TPS += 100;
            System.out.println("up");
        }
        if(e.getKeyCode() == KeyEvent.VK_DOWN){
            flappyBirdEngine.TPS -= 100;
            System.out.println("down");
        }
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE && !flappyBirdEngine.dead)
            flappyBirdEngine.pause = !flappyBirdEngine.pause;
        if(e.getKeyCode() == KeyEvent.VK_SPACE && flappyBirdEngine.before)
            flappyBirdEngine.before = false;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        super.keyReleased(e);
        reset = true;
    }
}
