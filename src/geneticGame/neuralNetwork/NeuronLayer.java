/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticGame.neuralNetwork;

import geneticGame.Playground;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import math.geom2d.Point2D;
import math.geom2d.conic.Circle2D;

/**
 *
 * @author Denis Kurka
 */
public class NeuronLayer {
    private ArrayList<Neuron> neurons;
    private NeuronLayer sourceLayer;
    private boolean isInput;
    
    public NeuronLayer(NeuronLayer sourceLayer, ArrayList<ArrayList<Double>> weights) {
        this.sourceLayer = sourceLayer;
        this.neurons = new ArrayList();
        
        int neuronCount = weights.size();
        for(int i = 0; i < neuronCount; i++) {
            neurons.add(new Neuron(this, weights.get(i)));
        }
        
        isInput = false;
    }
    
    public NeuronLayer(int neuronCount) {
        this.sourceLayer = null;
        
        neurons = new ArrayList();
        
        for(int i = 0; i < neuronCount; i++) {
            neurons.add(new Neuron(this));
        }
        
        isInput = true;
    }

    //Getters and Setters
    public int getNeuronCount() {
        return neurons.size();
    }
    
    public ArrayList<Neuron> getNeurons() {
        return neurons;
    }

    public void setNeurons(ArrayList<Neuron> neurons) {
        this.neurons = neurons;
    }

    public NeuronLayer getSourceLayer() {
        return sourceLayer;
    }

    public boolean isIsInput() {
        return isInput;
    }
    //End of Getters and Setters
    
    //Přečte aktivace jiý aktivované neuronove vrsty a podle toho aktivuje všechny neurony
    public void calculateActivations() {
        ArrayList<Double> sourceActivations = new ArrayList();
        
        for(Neuron sn : sourceLayer.getNeurons()) {
            sourceActivations.add(sn.getActivation());
        }
        
        for(Neuron n : neurons) {        
            n.activate(sourceActivations);
        }
    }
    
    //"Na tvrdo" nastaví aktivace neuronů --> Hodí se pro input layer kde váhy nejsou potřeba
    public void setActivations(ArrayList<Double> acts) {
        int i = 0;
        for(Neuron n : neurons) {
            n.setActivation(acts.get(i));
            i++;
        }
    }
    
    //Zjistí aktivace neuronů
    public ArrayList<Double> getActivations() {
        ArrayList<Double> acts = new ArrayList();
        for(Neuron n : neurons) {
            acts.add(n.getActivation());
        }
        
        return acts;
    }
    
    //Zobrazí neurony graficky
    public void paintNeurons(Graphics2D g2d, Playground pg, int offset, int distance) {
        for(int i = 0; i < this.getNeuronCount(); i++) {
            Circle2D nCir = new Circle2D(pg.getWidth()-pg.getScaledValue(distance)+pg.getScaledValue(offset), (pg.getScaledValue(30)*i) + pg.getScaledValue(30), pg.getScaledValue(5));
            double act = this.getNeurons().get(i).getActivation();
            g2d.setColor(new Color((float)act ,0, 0));
            nCir.fill(g2d);
            
            this.getNeurons().get(i).setDrawPoint(nCir.center());
        }
    }
    
    
    //Zobrazí spojení mezi neurony graficky
    public void paintWeights(Graphics2D g2d, Playground pg) {
        if(isInput) return;
        
        for(int i = 0; i < this.getNeuronCount(); i++) {
            for(int j = 0; j < sourceLayer.getNeuronCount(); j++) {
                Point2D c1 = this.getNeurons().get(i).getDrawPoint();
                Point2D c2 = sourceLayer.getNeurons().get(j).getDrawPoint();
                
                double weight = this.getNeurons().get(i).getWeights().get(j);
                if(weight > 0) {
                     g2d.setColor(new Color(0, (float)weight, 0));
                }
                else {
                     g2d.setColor(new Color(-(float)weight, 0, 0));
                }
                
                g2d.drawLine((int)c1.x(), (int)c1.y(), (int)c2.x(), (int)c2.y());
            }
        }
    }
    
}
