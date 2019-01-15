/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuralSandbox;

import neuralSandbox.neuralNetwork.NeuralNetwork;
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
    private boolean feedbackSensor;
    
    public CarAI(Playground pg, Point pos, int length, int width, Color fillColor, Color cirColor, int angle, boolean feedbackSensor, boolean showSensors, ArrayList<ArrayList<ArrayList<Double>>> brainData, ArrayList<Integer> brainTemplate) {
        super(pg, pos, length, width, fillColor, cirColor, angle);
        
        sensors = new ArrayList<>();
        sLenght = 200;
        sensors.add(new Sensor(this, sLenght, 1, 5, Color.CYAN, Color.BLACK, -Math.PI/4));
        sensors.add(new Sensor(this, sLenght, 1, 5, Color.CYAN, Color.BLACK, Math.PI/4));
        sensors.add(new Sensor(this, sLenght, 1, 5, Color.CYAN, Color.BLACK, 0));
        this.showSensors = showSensors;
        
        this.feedbackSensor = feedbackSensor;
        
        if(brainData == null) {
            brainData = generateRandomBrainData(brainTemplate);
        }
        
        fitness = 0;
        playerControl = false;
        
        brain = new NeuralNetwork(this, brainData);
    }
    
    public CarAI(Playground pg, Point pos) {
        this(pg, pos, 10, 4, Color.RED, Color.BLACK, 0, false, false, null, new ArrayList<>(Arrays.asList(3,2)));
    }
    
    public CarAI(ArrayList<Integer> brainTemplate, Playground pg, Point pos, boolean feedback, boolean showSensors) {
        this(pg, pos, 10, 4, Color.RED, Color.BLACK, 0, feedback, showSensors, null, brainTemplate);
    }
    
    public CarAI(Playground pg) {
        this(pg, new Point(pg.getWidth()/2,pg.getHeight()/2), 10, 4, Color.RED, Color.BLACK, 0, false, false, null, new ArrayList<>(Arrays.asList(3,2)));
    }
    
    public CarAI(Playground pg, Point pos, ArrayList<ArrayList<ArrayList<Double>>> brainData, boolean feedback) {
        this(pg, pos, 10, 4, Color.RED, Color.BLACK, 0, feedback, false, brainData, new ArrayList<>(Arrays.asList(3,2)));
    }
    
    public CarAI(Playground pg, ArrayList<ArrayList<ArrayList<Double>>> brainData, boolean feedback) {
        this(pg, new Point(pg.getWidth()/2,pg.getHeight()/2), 10, 4, Color.RED, Color.BLACK, 0, feedback, false, brainData, new ArrayList<>(Arrays.asList(3,2)));
    }
    
    public CarAI(Playground pg, Point pos, ArrayList<ArrayList<ArrayList<Double>>> brainData, ArrayList<Integer> brainTemplate, boolean feedback, boolean showSensors) {
        this(pg, pos, 10, 4, Color.RED, Color.BLACK, 0, feedback, showSensors, brainData, brainTemplate);
    }
    
    public CarAI(Playground pg, ArrayList<ArrayList<ArrayList<Double>>> brainData, ArrayList<Integer> brainTemplate, boolean feedback) {
        this(pg, new Point(pg.getWidth()/2,pg.getHeight()/2), 10, 4, Color.RED, Color.BLACK, 0, feedback, false, brainData, brainTemplate);
    }
    
    //Getters and Setters
    public NeuralNetwork getBrain() {
        return brain;
    }

    public void setBrain(NeuralNetwork brain) {
        this.brain = brain;
    }

    public int getSensorsCount() {
        return (feedbackSensor) ? sensors.size()+1 : sensors.size();
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
    
    //Funkce pro výpočet fitness skóre jedince
    public void calculateFitness() {
        double rawFitness = 1/((this.pos.distance(pg.getFinish().getFinishpoint())/pg.getSpawn().pos.distance(pg.getFinish().getFinishpoint())));
        
        this.fitness = Math.pow(rawFitness,8);
    }
    
    //Vygenerování náhodnách vah do neuronové site
    public final ArrayList<ArrayList<ArrayList<Double>>> generateRandomBrainData(ArrayList<Integer> layers) {
        ArrayList<ArrayList<ArrayList<Double>>> brainData = new ArrayList<>();
        
        Random rand = new Random();
        
        for(int i = 0; i < layers.size(); i++) {
            brainData.add(new ArrayList<>());
            
            int neuronConnections;
            
            if(i == 0) {
                neuronConnections = getSensorsCount();
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
        
        force = 3*force;

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
        
        double finishDistance = 1-(this.pos.distance(pg.getFinish().getFinishpoint())/pg.getSpawn().getSpawnpoint().distance(pg.getFinish().getFinishpoint()));
        
        if(finishDistance < 0) finishDistance = 0;
        
        inputs.add(finishDistance);
        
        //System.out.println("Inputs: "+inputs.get(0)+";"+inputs.get(1)+";"+inputs.get(2)+";"+inputs.get(3));
        
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
    public void paint(Graphics gr, boolean noPaint) {
        super.paint(gr, noPaint);
        if(!this.frozen) {
            for(Sensor s : sensors) {
                s.paint(gr, (noPaint) ? false : showSensors);
            }
            
            if(pg.isLearning())
                brain.think(measureDistance());
            
            //DEBUG!!
            if(playerControl)
                System.out.println("Dist: "+sensors.get(0).getDistanceToBarrier());
        }
    }
    
    //PAINT MA BRAINN!"!
    public void paintBrain(Graphics gr) {
        brain.paint(gr, pg);
    }
}
