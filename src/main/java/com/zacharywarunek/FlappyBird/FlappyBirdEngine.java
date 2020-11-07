package com.zacharywarunek.FlappyBird;

import com.evo.NEAT.*;
import com.evo.NEAT.genes.ConnectionGene;
import com.evo.NEAT.genome.Genome;
import com.zacharywarunek.Engine.*;
import com.zacharywarunek.Driver;
import org.warunek.kettering.Engine.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBirdEngine extends Engine implements AIEngine {
    public static final double panSpeed = 2;
    public static Image pipeUpperTexture;
    public static Image pipeLowerTexture;
    public static BufferedImage birdTexture;
    public static Image backgroundTexture;
    public static final int YVEL = 0;
    public static final int TOPIPE = 1;
    public static final int TOBOTTOM = 2;
    public static final int TOTOP = 3;
    public static double gravity = .13;
    public static int superSpeed = 1;
    public static boolean showBest = false; //true if only show the best of the previous generation
    public static boolean runBest = false; //true if replaying the best ever game
    public static boolean humanPlaying = false; //true if the user is playing
    public static boolean training = false;
    public static boolean showAll = true;
    public static boolean fromFile = true;
    public static boolean showBrain = false;
    public static boolean showBestEachGen = false;
    public static int upToGen = 0;
    public static boolean showNothing = false;
    public boolean firstTime = true;
    public Driver driver;
    ArrayList<Background> bg = new ArrayList<>();
    PipePair lastPipes;
    PipePair currentPipes;
    int pipeWidth = 100;
    int highscore;

    Population population;
    Bird bird;
    ArrayList<GameObject> toBeRemoved = new ArrayList<>();
    ArrayList<GameObject> toBeAdded = new ArrayList<>();
    ID[] idArray = {ID.Background, ID.PipePair, ID.Player};
    public FlappyBirdEngine(Driver driver, int WIDTH, int HEIGHT, int TPS, boolean humanPlaying, boolean training, boolean fromFile) {
        super(driver, WIDTH, HEIGHT, TPS);
        this.driver = driver;
        FlappyBirdEngine.humanPlaying = humanPlaying;
        FlappyBirdEngine.training = training;
        FlappyBirdEngine.fromFile = fromFile;
        handler = new Handler(idArray);
        addKeyListener(new KeyInput(this));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mouseClicked(e);
                if(dead)
                    restart();
            }
        });
    }

    @Override
    public void init() {
        requestFocus();
        try {
            pipeUpperTexture = ImageIO.read(this.getClass().getClassLoader().getResource("textures/pipeUpper.png")).getScaledInstance(pipeWidth, ImageIO.read(this.getClass().getClassLoader().getResource("textures/pipeUpper.png")).getHeight(), Image.SCALE_SMOOTH);
            pipeLowerTexture = ImageIO.read(this.getClass().getClassLoader().getResource("textures/pipeLower.png")).getScaledInstance(pipeWidth, ImageIO.read(this.getClass().getClassLoader().getResource("textures/pipeLower.png")).getHeight(), Image.SCALE_SMOOTH);
            birdTexture = resize(ImageIO.read(this.getClass().getClassLoader().getResource("textures/bird.png")), 80, (int)(.66 * 80))/*.getScaledInstance(80, (int)(.666 * 80), Image.SCALE_SMOOTH)*/;
            backgroundTexture = ImageIO.read(this.getClass().getClassLoader().getResource("textures/background.png")).getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Background temp = new Background(this, new HitBox(0, 0, WIDTH, HEIGHT), ID.Background);
        bg.add(temp);
        addPipe();
        getHandler().addObject(temp);
        temp = new Background(this, new HitBox(WIDTH, 0, WIDTH, HEIGHT), ID.Background);
        bg.add(temp);
        getHandler().addObject(temp);
        if(humanPlaying){
            bird = new Bird(this);
            bird.color = new Random().nextFloat();
            getHandler().addObject(bird);
        }
        else if(training){
            before = false;
            if(firstTime) {
                if(fromFile) {
                    String line;
                    ArrayList<String> array = new ArrayList<>();
                    try{
                        BufferedReader br = new BufferedReader(new FileReader(new File("Genome/Genome.txt")));
                        while((line = br.readLine()) != null){
                            if(!line.equals(""))
                                array.add(line);
                        }
                    } catch (IOException ignored) { }
                    population = new Population(this, array);
                }
                else
                    population = new Population(this);
                firstTime = false;
            }
        }
        else{
            before = false;
            String line;
            ArrayList<String> array = new ArrayList<>();
            try{
                BufferedReader br = new BufferedReader(new FileReader(new File("Genome/Genome.txt")));
                while((line = br.readLine()) != null){
                    if(!line.equals(""))
                        array.add(line);
                }
            } catch (IOException ignored) { }
            bird = new Bird(this);
            bird.color = new Random().nextFloat();
            Genome genome = new Genome();
            ArrayList<ConnectionGene> geneList = new ArrayList<>();
            for(String str : array) {
                String[] strings = str.split(",");
                geneList.add(new ConnectionGene(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]), Integer.parseInt(strings[1]), Float.parseFloat(strings[3]), Boolean.parseBoolean(strings[4])));
            }
            genome.setConnectionGeneList(geneList);
                bird.brain = genome;

        }
    }
    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }
    @Override
    public void paused(boolean val) {

    }

    private void drawBackground(Graphics2D g){
        for(Background background : bg)
            background.render(g);
    }
    protected synchronized void render(Graphics2D g){
        g.clearRect(0, 0, WIDTH, HEIGHT);
        drawBackground(g);
        handler.render(g);
        if(!humanPlaying && training) {
            population.render(g);
            if(population.pool.getTopGenome() != null){
                driver.drawNN(population.birdGenomeHashMap.get(population.pool.getTopGenome()));
            }
        }
        else if(!humanPlaying){
            bird.render(g);
            driver.drawNN(bird);
        }
        g.setColor(Color.DARK_GRAY);
        g.drawString("Current TPS: " + TPS, 0, 30);
        if(!dead) {
            try{
                g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
                g.setColor(Color.red);
                if(humanPlaying || !training)
                    g.drawString("Score: " + bird.score, WIDTH - g.getFontMetrics().stringWidth("Score: " + bird.score), g.getFontMetrics().getHeight() / 2 + 5);
                else {
                    g.drawString("High Score: " + population.globalBestScore, WIDTH - g.getFontMetrics().stringWidth("High Score: " + population.globalBestScore), g.getFontMetrics().getHeight() / 2 + 5);
                    g.drawString("Current Score: " + population.currentBestScore, WIDTH - g.getFontMetrics().stringWidth("Current Score: " + population.currentBestScore), g.getFontMetrics().getHeight()/2*2 + 5*2);
                    g.drawString("Generation: " + population.gen, WIDTH - g.getFontMetrics().stringWidth("Generation: " + population.gen), g.getFontMetrics().getHeight()/2*3 + 5*3);
                    g.drawString("Population: " + population.players.size(), WIDTH - g.getFontMetrics().stringWidth("Population: " + population.players.size()), g.getFontMetrics().getHeight()/2*4 + 5*4);
                    g.drawString("Alive: " + population.alivePlayers.size(), WIDTH - g.getFontMetrics().stringWidth("Alive: " + population.alivePlayers.size()), g.getFontMetrics().getHeight()/2*5 + 5*5);
                }
            }catch(NullPointerException ignored){}
        }
        if(pause && (humanPlaying || !training)){
            g.setColor(new Color(0,0,0, 100));
            g.fillRect(0, 0, WIDTH, HEIGHT);
            g.setColor(Color.blue);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 40));
            g.drawString("Press ESC to Continue",WIDTH/2 - g.getFontMetrics().stringWidth("Press ESC to Continue")/2, HEIGHT/2 - g.getFontMetrics().getHeight()/2);
        }
        if(dead && (humanPlaying || !training)){
            g.setColor(new Color(0,0,0, 100));
            g.fillRect(0, 0, WIDTH, HEIGHT);
            g.setColor(Color.cyan);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 40));
            g.drawString("You Died",WIDTH/2 - g.getFontMetrics().stringWidth("You Died")/2, HEIGHT/2 - g.getFontMetrics().getHeight()/2);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
            g.drawString("Score: " + bird.score,WIDTH/2 - g.getFontMetrics().stringWidth("Score: " + bird.score)/2, HEIGHT/2 - g.getFontMetrics().getHeight()/2 + 30);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
            g.drawString("(Click to Continue)",WIDTH/2 - g.getFontMetrics().stringWidth("(Click to Continue)")/2, HEIGHT/2 - g.getFontMetrics().getHeight()/2 + 50);
        }
        if(before && (humanPlaying || !training)){
            g.setColor(new Color(0,0,0, 50));
            g.fillRect(0, 0, WIDTH, HEIGHT);
            g.setColor(new Color(200, 150,0));
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 40));
            g.drawString("Highscore: " + highscore,WIDTH/2 - g.getFontMetrics().stringWidth("Highscore: " + highscore)/2, HEIGHT/2 - g.getFontMetrics().getHeight()/2);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
            g.drawString("(Click Space)",WIDTH/2 - g.getFontMetrics().stringWidth("(Click Space)")/2, HEIGHT/2 - g.getFontMetrics().getHeight()/2 + 30);
        }
    }
    public void restart(){
        handler = new Handler(idArray);
        dead = false;
//        currentPipes = null;
//        lastPipes = null;
        init();
        if(humanPlaying)
            before = true;
    }

    @Override
    protected void compute() {

        if((humanPlaying || !training)) {
            if(!bird.dead){
                bird.checkPipes(currentPipes);
            }
            die();
        }
        else {
            population.checkPipes(currentPipes);
            if(population.allDead()){
                restart();
                population.handleNeuralNet();
            }
        }
        removeFromHandler();
        addToHandler();
    }
    @Override
    public void tick(){
        super.tick();
        if(!humanPlaying && training) {
            population.tick();
        }
        else if(!humanPlaying){
            bird.look();
            bird.think();
            bird.tick();
        }
    }
    public void addPipe(){
        lastPipes = currentPipes;
        currentPipes = new PipePair(this, new HitBox(WIDTH,0 , pipeWidth, Engine.HEIGHT), ID.PipePair, lastPipes);
        toBeAdded.add(currentPipes);
    }
    public void addBackground(Background remove){
        remove(remove);
        Background temp = new Background(this, new HitBox(WIDTH, 0, WIDTH, HEIGHT), ID.Background);
        toBeAdded.add(temp);
        bg.add(temp);
    }
    public void remove(GameObject object){
        toBeRemoved.add(object);
    }
    private void die(){
        if(bird.dead) {
            dead = true;
            if(highscore< bird.score)
                highscore = bird.score;
        }
    }
    protected void removeFromHandler() {
        if(!toBeRemoved.isEmpty()){
            for(GameObject object : toBeRemoved){
                handler.removeObject(object);
            }
            for(int i = toBeRemoved.size(); i>0; i--)
                toBeRemoved.remove(i - 1);
        }
    }

    protected void addToHandler() {
        if(!toBeAdded.isEmpty()){
            for(GameObject object : toBeAdded){
                handler.addObject(object);
            }
            for(int i = toBeAdded.size(); i>0; i--)
                toBeAdded.remove(i - 1);
        }

    }

    @Override
    public String toString() {
        return "Flappy Bird";
    }

    @Override
    public BirdPlayer getBirdPlayer() {
        return new Bird(this);
    }

    @Override
    public Player getPlayer() {
        return null;
    }
}
