package com.zacharywarunek;

import com.zacharywarunek.Engine.Engine;
import com.zacharywarunek.FlappyBird.Bird;
import com.zacharywarunek.FlappyBird.FlappyBirdEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;

public class Driver extends JFrame {
    public static Engine engine;
    public static Driver driver;
    JPanel background;
    JPanel options;
    JPanel game;
    public Canvas neuralNet;

    JCheckBox humanPlayingCB;
    JCheckBox aiPlayingCB;
    JCheckBox trainingCB;
    JCheckBox fromFileCB;


    JButton play;
    JButton changeTPSButton;
    JTextField changeTPSTextField;
    public Driver(){
        super("Games and Stuff");
        Engine.TPS = 144;
        init();
        super.setResizable(false);
        super.setSize(900, 600);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.pack();
        super.setVisible(true);
    }


    private void init(){
        background = new JPanel(new GridBagLayout());

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                int k = e.getKeyCode();
                if(k == KeyEvent.VK_E){
                    if(engine.pause)
                        engine.pause = false;
                    else
                        engine.pause = true;
                }
            }
        });
        MouseListener chackboxListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getSource() == humanPlayingCB && humanPlayingCB.isSelected()){
                    aiPlayingCB.setSelected(false);
                    trainingCB.setSelected(false);
                    fromFileCB.setSelected(false);
                }
                if(e.getSource() == aiPlayingCB && aiPlayingCB.isSelected()){
                    humanPlayingCB.setSelected(false);
                }
                if(e.getSource() == trainingCB && trainingCB.isSelected()){
                    humanPlayingCB.setSelected(false);
                    aiPlayingCB.setSelected(true);
                }
                if(e.getSource() == fromFileCB && fromFileCB.isSelected()){
                    humanPlayingCB.setSelected(false);
                    aiPlayingCB.setSelected(true);
                    trainingCB.setSelected(true);
                }
                if(e.getSource() == trainingCB && !trainingCB.isSelected()){
                    fromFileCB.setSelected(false);
                }
                if(e.getSource() == aiPlayingCB && !aiPlayingCB.isSelected()){
                    trainingCB.setSelected(false);
                    fromFileCB.setSelected(false);
                }
            }
        };

        game = new JPanel();
        options = new JPanel(new GridBagLayout());
        neuralNet = new Canvas();
        game.setBackground(Color.black);
        game.setPreferredSize(new Dimension(610, 610));
        humanPlayingCB = new JCheckBox();
        humanPlayingCB.setSelected(true);
        humanPlayingCB.addMouseListener(chackboxListener);
        humanPlayingCB.setLabel("Human Playing");
        aiPlayingCB = new JCheckBox();
        aiPlayingCB.addMouseListener(chackboxListener);
        aiPlayingCB.setLabel("AI Playing");
        trainingCB = new JCheckBox();
        trainingCB.addMouseListener(chackboxListener);
        trainingCB.setLabel("Training AI");
        fromFileCB = new JCheckBox();
        fromFileCB.addMouseListener(chackboxListener);
        fromFileCB.setLabel("Train From File");

        neuralNet.setBackground(Color.lightGray);
        changeTPSTextField = new JTextField(10);
        changeTPSTextField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!((c >= '0') && (c <= '9') || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
                    e.consume();
                }
            }
        });
        changeTPSButton = new JButton("Change TPS");
        changeTPSButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getSource() == changeTPSButton){
                    Engine.TPS = Integer.parseInt(changeTPSTextField.getText());
                }
            }
        });
        play = new JButton("Start/Restart");
        play.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getSource() == play){
                    startFlappyBird(600, 600, Engine.TPS, humanPlayingCB.isSelected(), trainingCB.isSelected(), fromFileCB.isSelected());
                }
            }
        });
        addComponent(options, changeTPSTextField, 0, 0, 1, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0,  new Insets(10, 10, 0, 5));
        addComponent(options, changeTPSButton,    1, 0, 1, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0,  new Insets(10, 5, 0, 10));
        addComponent(options, play,               1, 1, 1, 1, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0,  new Insets(10, 5, 0, 10));
        addComponent(options, humanPlayingCB,     0, 1, 1, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.SOUTHWEST, 0, 0,  new Insets(0, 10, 0, 0));
        addComponent(options, aiPlayingCB,        0, 2, 1, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST,   0, 0,  new Insets(0, 10, 0, 0));
        addComponent(options, trainingCB,         0, 3, 1, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST,   0, 0,  new Insets(0, 10, 0, 0));
        addComponent(options, fromFileCB,         0, 4, 1, 1, 1, 1, GridBagConstraints.NONE, GridBagConstraints.WEST,   0, 0,  new Insets(0, 10, 0, 0));

        addComponent(background, game,            0, 0, 1, 2, 1,  0, GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0, new Insets(10, 10, 10, 10));
        addComponent(background, options,         1, 0, 1, 1, 1, .1, GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0, new Insets(10, 10, 10, 10));
        addComponent(background, neuralNet,       1, 1, 1, 1, 1,  5, GridBagConstraints.BOTH, GridBagConstraints.CENTER, 0, 0, new Insets(10, 10, 10, 10));
        add(background);

    }

    public void drawNN(Bird bird) {

        BufferStrategy bs = neuralNet.getBufferStrategy();
        if(bs == null) {
            neuralNet.createBufferStrategy(2);
            bs = neuralNet.getBufferStrategy();
        }
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.clearRect(0, 0, neuralNet.getWidth(), neuralNet.getHeight());

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        bird.drawNN(g);
        g.dispose();
        bs.show();
    }

    private void stopCurrentGame(){
        if(engine != null) {
            game.remove(engine);
            engine.stop();
        }
    }
    private void startNewGame(){
        game.add(engine);
        engine.setPreferredSize(new Dimension(Engine.WIDTH, Engine.HEIGHT));
        engine.init();
        engine.setVisible(true);
        engine.start();
        pack();
    }
    public void startFlappyBird(int width, int height, int tps, boolean humanPlaying, boolean training, boolean fromFile){
        stopCurrentGame();
        engine = new FlappyBirdEngine(this, width, height, tps, humanPlaying, training, fromFile);
        startNewGame();
    }
    public static void addComponent( Container f, Component c, int gridx, int gridy, int w, int h, double wtx, double wty, int fill, int anchor, int ipadx, int ipady, Insets insets) {

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = w;
        gbc.gridheight = h;
        gbc.fill = fill;
        gbc.insets = insets;
        gbc.anchor = anchor;
        gbc.ipadx = ipadx;
        gbc.ipady = ipady;
        gbc.weightx = wtx;
        gbc.weighty = wty;
        f.add(c, gbc);
    }
    public static void main(String[] args){
        driver = new Driver();
    }
}
