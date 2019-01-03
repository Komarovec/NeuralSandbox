/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticGame.neuralNetwork;

import geneticGame.CarAI;
import geneticGame.Playground;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.util.ArrayList;
import math.geom2d.conic.Circle2D;
/**
 *
 * @author Denis Kurka
 */
public class NeuralNetwork {
    private CarAI car;
    
    private NeuronLayer input;
    private NeuronLayer output;
    private NeuronLayer hiddenLayer1;
    private NeuronLayer hiddenLayer2;
    
    private ArrayList<Double> outputActs;
    
    public NeuralNetwork(CarAI car) {
        this.car = car;
        
        input = new NeuronLayer(3);
        hiddenLayer1 = new NeuronLayer(4, input);
        hiddenLayer2 = new NeuronLayer(3, hiddenLayer1);
        output = new NeuronLayer(2, hiddenLayer2);
        
        outputActs = new ArrayList();
    }
    
    public void think(ArrayList<Double> inputs) {
        input.setActivations(inputs);
        
        hiddenLayer1.calculateActivations();
        
        hiddenLayer2.calculateActivations();
        
        output.calculateActivations();
        
        outputActs.clear();
        outputActs = output.getActivations();
        
        car.applyBrainOutput(outputActs);
    }
    
    public void paint(Graphics gr, Playground pg) {
        Graphics2D g2d = (Graphics2D)gr;
        
        int maxOffset = 100;
        
        int i = 0;
        for(Neuron n : input.getNeurons()) {
            Circle2D nCir = new Circle2D(pg.getWidth()-pg.getScaledValue(100), pg.getScaledValue((maxOffset/input.getNeuronCount()))*i, pg.getScaledValue(5));
            g2d.setColor(Color.red);
            nCir.fill(g2d);
            
            i++;
        }
        
        i = 0;
        for(Neuron n : hiddenLayer1.getNeurons()) {
            Circle2D nCir = new Circle2D(pg.getWidth()-pg.getScaledValue(80), pg.getScaledValue((maxOffset/hiddenLayer1.getNeuronCount()))*i, pg.getScaledValue(5));
            g2d.setColor(Color.red);
            nCir.fill(g2d);
            
            i++;
        }
        
        i = 0;
        for(Neuron n : hiddenLayer2.getNeurons()) {
            Circle2D nCir = new Circle2D(pg.getWidth()-pg.getScaledValue(60), pg.getScaledValue((maxOffset/hiddenLayer2.getNeuronCount()))*i, pg.getScaledValue(5));
            g2d.setColor(Color.red);
            nCir.fill(g2d);
            
            i++;
        }
        
        i = 0;
        for(Neuron n : output.getNeurons()) {
            Circle2D nCir = new Circle2D(pg.getWidth()-pg.getScaledValue(40), pg.getScaledValue((maxOffset/output.getNeuronCount()))*i, pg.getScaledValue(5));
            g2d.setColor(Color.red);
            nCir.fill(g2d);
            
            i++;
        }
    }
}
