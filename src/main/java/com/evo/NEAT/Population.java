package com.evo.NEAT;

import com.evo.NEAT.genes.ConnectionGene;
import com.evo.NEAT.genome.Genome;
import com.zacharywarunek.Driver;
import com.zacharywarunek.FlappyBird.Bird;
import com.zacharywarunek.FlappyBird.FlappyBirdEngine;
import com.zacharywarunek.FlappyBird.PipePair;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static com.zacharywarunek.FlappyBird.FlappyBirdEngine.superSpeed;

public class Population implements Environment {

    public int gensSinceNewWorld;
    public boolean newStage;
    public boolean massExtinctionEvent;
    public ArrayList<Bird> players = new ArrayList<>();
    public ArrayList<Bird> alivePlayers = new ArrayList<>();
    public BirdPlayer bestPlayer;
    public int bestScore;
    public int globalBestScore;
    public int currentBestScore;
    public int gen = 0;
    public Pool pool;
    public double x, width;
    public AIEngine engine;
    public Genome topGenome;
    public HashMap<Genome, Bird> birdGenomeHashMap = new HashMap<>();


    public Population(AIEngine engine) {
        this.engine = engine;
        pool = new Pool();
        pool.initializePool();
        players = new ArrayList<>();
        setPlayers(pool.getSpecies());
    }
    public Population(AIEngine engine, ArrayList<String> genes){
        ArrayList<ConnectionGene> geneList = new ArrayList<>();
        for(String str : genes) {
            String[] strings = str.split(",");
            geneList.add(new ConnectionGene(Integer.parseInt(strings[0]),Integer.parseInt(strings[1]),Integer.parseInt(strings[1]),Float.parseFloat(strings[3]),Boolean.parseBoolean(strings[4])));
        }
        this.engine = engine;
        pool = new Pool();
        pool.initializePool();

        setPlayers(pool.getSpecies(), geneList);
        handleNeuralNet();

    }
    private void setPlayers(ArrayList<Species> s, ArrayList<ConnectionGene> geneList){
        players = new ArrayList<>();
        birdGenomeHashMap = new HashMap<>();
        for(Species species : s)
            for(Genome genome : species.getGenomes()){
                Bird player = (Bird)engine.getBirdPlayer();
                player.color = species.color;
                genome.setConnectionGeneList(geneList);
                birdGenomeHashMap.put(genome, player);
                genome.setFitness(1);
                player.brain = genome;
                players.add(player);
            }
        alivePlayers = (ArrayList<Bird>) players.clone();
    }
    private void setPlayers(ArrayList<Species> s){
        players = new ArrayList<>();
        birdGenomeHashMap = new HashMap<>();
        for(Species species : s)
            for(Genome genome : species.getGenomes()){
                Bird player = (Bird)engine.getBirdPlayer();
                player.color = species.color;
                birdGenomeHashMap.put(genome, player);
                player.brain = genome;
                players.add(player);
            }
        alivePlayers = (ArrayList<Bird>) players.clone();
    }
    public void tick(){
        currentBestScore = 0;
        for (Bird player : this.players) {
            if (!player.dead) {

                for (int j = 0; j < superSpeed; j++) {
                    player.look(); //get inputs for brain
                    player.think();//use outputs from neural network
                    player.tick(); //move the player according to the outputs from the neural network
                    if(player.score == 500)
                        player.dead = true;
                    x = player.getX();
                    width = player.getHitbox().getWidth();
                }
                if (player.score > this.globalBestScore) {
                    this.globalBestScore = player.score;
                }
                if(currentBestScore<player.score)
                    currentBestScore = player.score;
                if(player.dead) {
                    alivePlayers.remove(player);
                }
            }
            else
                player.tick();

        }
    }
    public String getInfo(){
        return "Best Fitness: " + pool.getTopGenome().getFitness()+"  Gen: " + gen + " Pop size: " + pool.getCurrentPopulation() + "   High Score: " + currentBestScore + "   Flaps: " + birdGenomeHashMap.get(pool.getTopGenome()).amountOfFlaps + "   Lifespan: " + birdGenomeHashMap.get(pool.getTopGenome()).lifespan;
    }
    public void save(){
        try {
            pool.getTopGenome().writeTofile();
        }catch(Exception ignored){}
    }
    public void checkPipes(PipePair pipes){
        if (x + width >= (int)pipes.getX()) {
            ((FlappyBirdEngine) Driver.engine).addPipe();
        }
    }
    boolean drawn = false;
    public void render(Graphics2D g){
        for (int i = 0; i < players.size(); i++){
            Bird player = this.players.get(i);
                player.render(g);
        }
        drawn = false;
    }
    public boolean allDead(){
        for(int i = 0; i < players.size(); i++){
            BirdPlayer player = this.players.get(i);
            if(!player.isDead())
                return false;
        }
        return true;
    }
    public void evaluateFitness(ArrayList<Genome> population){
        for(Genome gene : population){
            Bird player = birdGenomeHashMap.get(gene);
            player.calculateFitness();
            gene.setFitness(player.fitness);
        }
    }
    public void handleNeuralNet() {
        pool.evaluateFitness(this);
        if(birdGenomeHashMap.get(pool.getTopGenome()).score == this.globalBestScore){
            save();
        }
        System.out.println(getInfo());
        pool.breedNewGeneration();
        setPlayers(pool.getSpecies());
        gen++;
    }
}
