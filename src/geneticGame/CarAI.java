/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticGame;

import geneticGame.neuralNetwork.NeuralNetwork;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Denis Kurka
 */
public class CarAI extends Car {
    private NeuralNetwork brain;
    private double fitness;
    
    private ArrayList<Sensor> sensors;
    
    private int sLenght;
    private boolean showSensors;
    private boolean playerControl;
    
    public CarAI(Playground pg, Point pos, int length, int width, Color fillColor, Color cirColor, int angle, ArrayList<ArrayList<ArrayList<Double>>> brainData) {
        super(pg, pos, length, width, fillColor, cirColor, angle);
        
        sensors = new ArrayList<>();
        sLenght = 200;
        sensors.add(new Sensor(this, sLenght, 1, 5, Color.CYAN, Color.BLACK, -Math.PI/4));
        sensors.add(new Sensor(this, sLenght, 1, 5, Color.CYAN, Color.BLACK, Math.PI/4));
        sensors.add(new Sensor(this, sLenght, 1, 5, Color.CYAN, Color.BLACK, 0));
        showSensors = true;
        
        if(brainData == null) {
            brainData = generateRandomBrainData(new ArrayList<>(Arrays.asList(3,2)));
        }
        
        fitness = 0;
        playerControl = false;
        
        brain = new NeuralNetwork(this, brainData);
    }
    
    public CarAI(Playground pg, Point pos) {
        this(pg, pos, 10, 4, Color.RED, Color.BLACK, 0, null);
    }
    
    public CarAI(Playground pg) {
        this(pg, new Point(pg.getWidth()/2,pg.getHeight()/2), 10, 4, Color.RED, Color.BLACK, 0, null);
    }
    
    public CarAI(Playground pg, Point pos, ArrayList<ArrayList<ArrayList<Double>>> brainData) {
        this(pg, pos, 10, 4, Color.RED, Color.BLACK, 0, brainData);
    }
    
    public CarAI(Playground pg, ArrayList<ArrayList<ArrayList<Double>>> brainData) {
        this(pg, new Point(pg.getWidth()/2,pg.getHeight()/2), 10, 4, Color.RED, Color.BLACK, 0, brainData);
    }
    
    //Getters and Setters
    public NeuralNetwork getBrain() {
        return brain;
    }

    public void setBrain(NeuralNetwork brain) {
        this.brain = brain;
    }

    public ArrayList getSensors() {
        return sensors;
    }

    public void setSensors(ArrayList sensors) {    
        this.sensors = sensors;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public boolean isPlayerControl() {
        return playerControl;
    }

    public void setPlayerControl(boolean playerControl) {
        this.playerControl = playerControl;
    }
    //End of Getters and Setters
    
    public void calculateFitness() {
        double rawFitness = 1/((this.pos.distance(pg.getFinish().getFinishpoint())/pg.getSpawn().pos.distance(pg.getFinish().getFinishpoint())));
        
        this.fitness = Math.pow(rawFitness,4);
    }
    
    //Vygenerování náhodnách vah do neuronové site
    public final ArrayList<ArrayList<ArrayList<Double>>> generateRandomBrainData(ArrayList<Integer> layers) {
        ArrayList<ArrayList<ArrayList<Double>>> brainData = new ArrayList<>();
        
        Random rand = new Random();
        
        for(int i = 0; i < layers.size(); i++) {
            brainData.add(new ArrayList<>());
            
            int neuronConnections;
            
            if(i == 0) {
                neuronConnections = sensors.size();
            }
            else {
                neuronConnections = layers.get(i-1);
            }
            
            for(int j = 0; j < layers.get(i); j++) {
                brainData.get(i).add(new ArrayList<>());
                
                for(int k = 0; k < neuronConnections; k++) {
                    brainData.get(i).get(j).add((rand.nextDouble()*2)-1);
                }
            }
        }
         
        return brainData;
    }
    
    //Pouzit vystup NS do pohybovych funkci auta
    public void applyBrainOutput(ArrayList<Double> output) {
        if(playerControl) return;
        
        double force = output.get(0);
        
        /*
        if(force > 0.5) {
            force = 1.4*force-0.4;
        }
        else {
            force = 1.4*force-1;
        }
        */

        setForce(force);
        setRotation(output.get(1));
        
        //System.out.println("Outputs: "+output.get(0)+";"+output.get(1));
    }
    
    
    //Zmeří a zabalí vzdálenosti od nejblizsích barier do pole
    public ArrayList<Double> measureDistance() {
        ArrayList<Double> inputs = new ArrayList();
        
        for(Sensor s : sensors) {
            inputs.add(s.getDistanceToBarrier());
        }
        
        //System.out.println("Inputs: "+inputs.get(0)+";"+inputs.get(1)+";"+inputs.get(2));
        
        return inputs;
    }
    
    //Sensory jsou viditelne
    public boolean isShowSensors() {
        return showSensors;
    }

    //Zapnout/Vypnout viditelnost senzoru
    public void setShowSensors(boolean showSensors) {
        this.showSensors = showSensors;
    }

    //Vykresli obsah
    public void drawArea(Graphics2D g2d ,Area area) {
        g2d.setColor(this.fillColor);
        g2d.fill(area);
        g2d.setColor(this.cirColor);
        g2d.draw(area); 
    }
    
    @Override
    public void paint(Graphics gr) {
        super.paint(gr);
        Graphics2D g2d = (Graphics2D)gr;
        
        if(!this.frozen) {
            for(Sensor s : sensors) {
                s.paint(gr, showSensors);
            }

            brain.think(measureDistance());
            //System.out.println("Dist: "+sLeft.getDistanceToBarrier());
        }
    }
    
    public void paintBrain(Graphics gr) {
        brain.paint(gr, pg);
    }
}
