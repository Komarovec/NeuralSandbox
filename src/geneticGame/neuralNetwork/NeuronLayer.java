/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticGame.neuralNetwork;

import java.util.ArrayList;

/**
 *
 * @author Denis Kurka
 */
public class NeuronLayer {
    private ArrayList<Neuron> neurons;
    private NeuronLayer sourceLayer;
    private boolean isInput;
    
    public NeuronLayer(int neuronCount, NeuronLayer sourceLayer) {
        this.sourceLayer = sourceLayer;
        
        neurons = new ArrayList();
        
        for(int i = 0; i < neuronCount; i++) {
            neurons.add(new Neuron(this));
            neurons.get(i).randomWeights(sourceLayer.getNeuronCount());
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
    
    public void calculateActivations() {
        ArrayList<Double> sourceActivations = new ArrayList();
        
        for(Neuron sn : sourceLayer.getNeurons()) {
            sourceActivations.add(sn.getActivation());
        }
        
        for(Neuron n : neurons) {        
            n.activate(sourceActivations);
        }
    }
    
    public void setWeights() {
        //Set DNA
    }
    
    public void setActivations(ArrayList<Double> acts) {
        int i = 0;
        for(Neuron n : neurons) {
            n.setActivation(acts.get(i));
            i++;
        }
    }
    
    public ArrayList<Double> getActivations() {
        ArrayList<Double> acts = new ArrayList();
        for(Neuron n : neurons) {
            acts.add(n.getActivation());
        }
        
        return acts;
    }
}
