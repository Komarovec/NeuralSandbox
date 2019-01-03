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

/**
 *
 * @author Denis Kurka
 */
public class CarAI extends Car {
    private NeuralNetwork brain;
    
    private Sensor sLeft, sRight, sMid;
    private int sLenght;
    private boolean showSensors;
    
    public CarAI(Playground pg, Point pos, int length, int width, Color fillColor, Color cirColor, int angle) {
        super(pg, pos, length, width, fillColor, cirColor, angle);
        
        sLenght = 150;
        sLeft = new Sensor(this, sLenght, 1, 5, Color.CYAN, Color.BLACK, -Math.PI/4);
        sRight = new Sensor(this, sLenght, 1, 5, Color.CYAN, Color.BLACK, Math.PI/4);
        sMid = new Sensor(this, sLenght, 1, 5, Color.CYAN, Color.BLACK, 0);
        
        showSensors = true;
        
        brain = new NeuralNetwork(this);
    }
    
    public CarAI(Playground pg, Point pos) {
        this(pg, pos, 10, 4, Color.RED, Color.BLACK, 0);
    }
    
    public CarAI(Playground pg) {
        this(pg, new Point(pg.getWidth()/2,pg.getHeight()/2), 10, 4, Color.RED, Color.BLACK, 0);
    }

    public Sensor getsLeft() {
        return sLeft;
    }

    public Sensor getsRight() {
        return sRight;
    }

    public Sensor getsMid() {
        return sMid;
    }   
    
    public void applyBrainOutput(ArrayList<Double> output) {
        setForce(output.get(0));
        setRotation(output.get(1));
        
        System.out.println("Outputs: "+output.get(0)+";"+output.get(1));
    }
    
    public ArrayList<Double> measureDistance() {
        ArrayList<Double> inputs = new ArrayList();
        
        inputs.add(getsLeft().getDistanceToBarrier());
        inputs.add(getsMid().getDistanceToBarrier());
        inputs.add(getsRight().getDistanceToBarrier());
        
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
        
        sLeft.paint(gr, showSensors);
        sRight.paint(gr, showSensors);
        sMid.paint(gr, showSensors);
        
        brain.think(measureDistance());
        
        brain.paint(gr, pg);
        //System.out.println("Dist: "+sLeft.getDistanceToBarrier());
    }
}
