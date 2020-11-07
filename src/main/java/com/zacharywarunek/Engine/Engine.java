package com.zacharywarunek.Engine;

import com.zacharywarunek.Driver;

import java.awt.*;
import java.awt.image.BufferStrategy;

public abstract class Engine extends Canvas implements Runnable{
    public static int FRAMEWIDTH, FRAMEHEIGHT, WIDTH, HEIGHT;
    protected Thread thread;
    public boolean running = false;
    protected Handler handler;
    public static int TPS;
    public boolean pause = false;
    public boolean dead = false;
    public boolean before = false;
    public int currentFPS;
    protected double delta;
    public Driver driver;

    public Engine(Driver driver, int WIDTH, int HEIGHT, int TPS){
        this.driver = driver;
        Engine.TPS = TPS;
        Engine.WIDTH = WIDTH;
        Engine.HEIGHT = HEIGHT;
        FRAMEHEIGHT = HEIGHT + 29;
        FRAMEWIDTH = WIDTH + 29;
    }

    public synchronized void start() {
        thread = new Thread(this);
        thread.start();
        running = true;
    }

    public synchronized void stop() {
        try{
            thread.stop();
            running = false;
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void init(){
    }
    @Override
    public void run() {
        requestFocus();
        long lastTime = System.nanoTime();
        delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while(running){
            long now = System.nanoTime();
            delta += (now - lastTime) / (1000000000.0 / TPS);
            lastTime = now;
            while (delta >= 1) {
                compute();
                if(!pause && !dead && !before)
                    tick();
                delta--;
            }
            try {
                render();
            }
            catch(IllegalStateException ignored){}
            frames++;
            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                currentFPS = frames;
                frames = 0;
            }

        }
        stop();
    }
    public void render(){

        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null) {
            this.createBufferStrategy(2);
            bs = this.getBufferStrategy();
        }
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.clearRect(0, 0, WIDTH, HEIGHT);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        render(g);
        g.dispose();
        bs.show();

    }

    public abstract void paused(boolean val);
    protected abstract void render(Graphics2D g);
    protected abstract void compute();
    protected synchronized void tick(){
        handler.tick();
    }
    public Handler getHandler(){
        return handler;
    }
}
