package com.zacharywarunek.FlappyBird;

import com.evo.NEAT.genes.ConnectionGene;
import com.evo.NEAT.BirdPlayer;
import com.evo.NEAT.com.evo.NEAT.config.NEAT_Config;
import com.jhlabs.image.HSBAdjustFilter;
import com.sun.javafx.geom.Vec2d;
import com.zacharywarunek.Engine.Engine;
import com.zacharywarunek.Engine.GameObject;
import com.zacharywarunek.Engine.HitBox;
import com.zacharywarunek.Driver;
import com.zacharywarunek.Engine.ID;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Bird extends BirdPlayer {
    float[] vision;
    float[] decision;
    public float color;
    boolean flap = false;
    public int amountOfFlaps = 0;
    PipePair nextPipe;
    PipePair lastPipes;
    public double lifespan = 1;
    boolean onDeath = false;
    int terminal = 10;
    public Bird(Engine engine) {
        super(engine, new HitBox(Engine.WIDTH / 2 - 50 * 2, Engine.HEIGHT / 2 + 50, 60, .666 * 80), ID.Player);
        nextPipe = ((FlappyBirdEngine) Driver.engine).currentPipes;
    }


    @Override
    public void tick() {

        if(!dead) {
            lifespan++;
            setVelY(getVelY() + FlappyBirdEngine.gravity);
            if (getVelY() >= terminal) {
                setVelY(terminal);
            }
            if(getVelY()>-4)
                flap = false;
            setY(getY() + getVelY());
            if (getY() + hitbox.getHeight() >= Engine.HEIGHT || getY() < 0) {
                dead = true;
                return;
            }

//            System.out.println(((FlappyBirdEngine) Driver.engine).currentPipes + "        " + nextPipe);
            for (GameObject object : Driver.engine.getHandler().getMap().get(ID.PipePair)) {
                if (nextPipe == object)
                    if (getX() > (int) object.getX() + object.getHitbox().getWidth() && nextPipe == object) {
                        lastPipes = nextPipe;
                        nextPipe = ((FlappyBirdEngine) Driver.engine).currentPipes;
                        score++;
                    }
                if ((((PipePair) object).lower.getHitbox().intersects(getHitbox()) || ((PipePair) object).upper.getHitbox().intersects(getHitbox()))) {
                    dead = true;
                }
            }
//            dead = false;
        }
        else{
            setX(getX() -2);
        }
    }

    @Override
    public boolean isDead() {
        return dead;
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.black);
        double theta = (90*(getVelY() / terminal)) * (Math.PI/180);
            g.rotate(theta, hitbox.getX() + hitbox.getWidth()/2, hitbox.getY() + hitbox.getHeight()/2);

            g.drawImage(filterImmage(FlappyBirdEngine.birdTexture, color, 1f, .5f), (int)hitbox.getX(), (int)hitbox.getY(), null);
        g.rotate(-theta, hitbox.getX() + hitbox.getWidth()/2, hitbox.getY() + hitbox.getHeight()/2);

    }
    public BufferedImage filterImmage(BufferedImage source, float hValue, float sValue, float bValue) {
        com.jhlabs.image.HSBAdjustFilter filter = new HSBAdjustFilter();
        BufferedImage destination = filter.createCompatibleDestImage(source, null);
        filter.setHFactor(hValue);
        filter.setSFactor(sValue);
        filter.setBFactor(bValue);
        return filter.filter(source, destination);
    }
    private void drawHitbox(Graphics2D g) {
        g.drawRect((int)hitbox.getX(), (int)hitbox.getY(), (int)hitbox.getWidth(), (int)hitbox.getHeight());
    }

    public void checkPipes(PipePair pipes){
        if (getX() + getHitbox().getWidth() >= (int)pipes.getX()) {
            ((FlappyBirdEngine) Driver.engine).addPipe();
        }
    }
    public void flap(){
        setVelY(-5);
        amountOfFlaps++;
    }
    public void look() {
        //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<replace
        this.vision = new float[NEAT_Config.INPUTS];
//        this.vision[0] = getFloat((float)this.velY, -5, terminal, 0, 1); //bird can tell its current y velocity

        int distanceToClosestPipe = (int) (nextPipe.getX() - getX());
        this.vision[0] = getFloat(distanceToClosestPipe, 0, (float)Engine.WIDTH - (float)getX() + (int)getHitbox().getWidth(), 0, 1);
        this.vision[1] = getFloat((float) (nextPipe.lower.getY() - getY()), -Engine.HEIGHT, Engine.HEIGHT, 0, 1); //height above bottomY
        this.vision[2] = getFloat((float)(getY() - nextPipe.upper.getHitbox().getHeight()), -Engine.HEIGHT, Engine.HEIGHT, 0, 1); //distance below topThing

    }
    public float getFloat(float in, float low, float high, float min, float max){
        float num = (((in - low)/(high - low)))*(max-min);
        return num < min ? min : (Math.min(num, max));
    }


    //---------------------------------------------------------------------------------------------------------------------------------------------------------
    //gets the output of the this.brain then converts them to actions
    public void think() {

        //get the output of the neural network
        this.decision = this.brain.evaluateNetwork(this.vision);
//        System.out.println(Arrays.toString(this.decision));
        if (this.decision[0] > 0.6) {
            flap = true;
            this.flap();
        }
    }
    public void calculateFitness() {

//        this.fitness = (float) ((1 + score * score + (this.lifespan/1000) + (this.lifespan/1000)) / 20.0);
        this.fitness = (float) (lifespan/10 - 1.5 *amountOfFlaps);
    }
    int nodeSize = 30, nodeSpace = nodeSize;
    int width = 9*nodeSize, height = Math.max(Math.max(NEAT_Config.INPUTS, NEAT_Config.HIDDEN_NODES), NEAT_Config.OUTPUTS)*(nodeSize + nodeSpace) + nodeSpace;
    public void drawNN(Graphics2D g){
        try{
            g.setColor(Color.WHITE);
            g.drawRect(0, 0, width, height);

            Vec2d[] nodeLocations = new Vec2d[NEAT_Config.INPUTS + NEAT_Config.HIDDEN_NODES + NEAT_Config.OUTPUTS];
            for(int i = 0; i < nodeLocations.length; i++){
                nodeLocations[i] = drawNode(g, i);
            }

            for(int i = 0; i < brain.getConnectionGeneList().size(); i++){
                g.setStroke(new BasicStroke(3));
                ConnectionGene gene = brain.getConnectionGeneList().get(i);
                if(gene.isEnabled()){

                    g.setColor(gene.getWeight() < 0 ? new Color(0, 0, 1, Math.abs(gene.getWeight())/3) : new Color(1, 0, 0, Math.abs(gene.getWeight())/3 ));
                    g.drawLine((int)nodeLocations[gene.getTarget()].x, (int)nodeLocations[gene.getTarget()].y, (int)nodeLocations[gene.getSource()].x, (int)nodeLocations[gene.getSource()].y);
                }
            }
            g.setStroke(new BasicStroke(1));
            for(int i = 0; i < nodeLocations.length; i++){
                g.setColor(engine.driver.neuralNet.getBackground());
                g.fillOval((int)nodeLocations[i].x - nodeSize/2, (int)nodeLocations[i].y - nodeSize/2, nodeSize, nodeSize);
                g.setColor(Color.white);
                g.drawOval((int)nodeLocations[i].x - nodeSize/2, (int)nodeLocations[i].y - nodeSize/2, nodeSize, nodeSize);
                g.setFont(new Font(Font.MONOSPACED, Font.BOLD, nodeSize*2/3));
                g.setColor(Color.BLACK);
                g.drawString(String.valueOf(i), (int)nodeLocations[i].x - g.getFontMetrics().stringWidth(String.valueOf(i))/2, (int)nodeLocations[i].y + nodeSize*2/3/4);
            }

        }catch(NullPointerException | IllegalArgumentException e){e.printStackTrace();}
    }
    private Vec2d drawNode(Graphics2D g, int i){
        int xSpace, numberPerRow;
        double xMultiplier;
        if(i < NEAT_Config.INPUTS){
            xSpace = nodeSpace + nodeSize*0;
            numberPerRow = NEAT_Config.INPUTS;
            xMultiplier = 0;
        } else if(i < NEAT_Config.HIDDEN_NODES + NEAT_Config.INPUTS){
            xSpace = nodeSpace + nodeSize*2;
            numberPerRow = NEAT_Config.HIDDEN_NODES;
            xMultiplier = .75;
            i -= NEAT_Config.INPUTS;
        } else{
            xSpace = nodeSpace + nodeSize*4;
            numberPerRow = NEAT_Config.OUTPUTS;
            xMultiplier = 1.5;
            i -= NEAT_Config.INPUTS + NEAT_Config.HIDDEN_NODES;
        }
        int space = (height - (numberPerRow * (nodeSpace + nodeSize) - nodeSpace))/2;
        return  new Vec2d(xSpace + nodeSize * xMultiplier + nodeSize/2, space + ((nodeSpace + nodeSize) * (i)) + nodeSize/2);
    }
}
