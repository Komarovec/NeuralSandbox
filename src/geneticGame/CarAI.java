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
    
    private ArrayList<Sensor> sensors;
    
    private int sLenght;
    private boolean showSensors;
    
    public CarAI(Playground pg, Point pos, int length, int width, Color fillColor, Color cirColor, int angle, ArrayList<ArrayList<ArrayList<Double>>> brainData) {
        super(pg, pos, length, width, fillColor, cirColor, angle);

        sensors = new ArrayList<>();
        sLenght = 150;
        sensors.add(new Sensor(this, sLenght, 1, 5, Color.CYAN, Color.BLACK, -Math.PI/4));
        sensors.add(new Sensor(this, sLenght, 1, 5, Color.CYAN, Color.BLACK, Math.PI/4));
        sensors.add(new Sensor(this, sLenght, 1, 5, Color.CYAN, Color.BLACK, 0));
        showSensors = true;
        
        if(brainData == null) {
            brainData = generateRandomBrainData(new ArrayList<>(Arrays.asList(3,2)));
        }
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
    //End of Getters and Setters
    
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
    
    public void applyBrainOutput(ArrayList<Double> output) {
        setForce(output.get(0));
        setRotation(output.get(1));
        
        //System.out.println("Outputs: "+output.get(0)+";"+output.get(1));
    }
    
    public ArrayList<Double> measureDistance() {
        ArrayList<Double> inputs = new ArrayList();
        
        for(Sensor s : sensors) {
            inputs.add(s.getDistanceToBarrier());
        }
        
        //System.out.println("Inputs: "+inputs.get(0)+";"+inputs.get(1)+";"+inputs.get(2));
        
        return inputs;
    }
    
    public boolean isShowSensors() {
        return showSensors;
    }

    public void setShowSensors(boolean showSensors) {
        this.showSensors = showSensors;
    }

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
        
        for(Sensor s : sensors) {
            s.paint(gr, showSensors);
        }
        
        brain.think(measureDistance());
        
        brain.paint(gr, pg);
        //System.out.println("Dist: "+sLeft.getDistanceToBarrier());
    }
}
