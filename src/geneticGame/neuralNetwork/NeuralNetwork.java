/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticGame.neuralNetwork;

import geneticGame.CarAI;
import geneticGame.Playground;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.util.ArrayList;
/**
 *
 * @author Denis Kurka
 */
public class NeuralNetwork {
    private CarAI car;
    
    private ArrayList<ArrayList<ArrayList<Double>>> brainData;

    private ArrayList<NeuronLayer> neuronLayers;
    
    private ArrayList<Double> outputActs;
    
    
    public NeuralNetwork(CarAI car, ArrayList<ArrayList<ArrayList<Double>>> brainData) {
        this.brainData = brainData;
        this.car = car;
        
        neuronLayers = new ArrayList<>();
        
        neuronLayers.add(new NeuronLayer(car.getSensors().size()));
        for(int i = 1; i <= brainData.size(); i++) {
            neuronLayers.add(new NeuronLayer(neuronLayers.get(i-1), brainData.get(i-1)));
        }
    }

    //Getters and Setters
    public CarAI getCar() {
        return car;
    }

    public void setCar(CarAI car) {
        this.car = car;
    }

    public ArrayList<ArrayList<ArrayList<Double>>> getBrainData() {
        return brainData;
    }

    public void setBrainData(ArrayList<ArrayList<ArrayList<Double>>> brainData) {
        this.brainData = brainData;
    }

    public ArrayList<NeuronLayer> getNeuronLayers() {
        return neuronLayers;
    }

    public void setNeuronLayers(ArrayList<NeuronLayer> neuronLayers) {
        this.neuronLayers = neuronLayers;
    }
    //End of Getters and Setters
    
    //Prožene data neuronovou sítí a zjistí její tip
    public void think(ArrayList<Double> inputs) {
        neuronLayers.get(0).setActivations(inputs);
        
        for(NeuronLayer nl : neuronLayers) {
            if(nl.isIsInput()) continue;
            
            nl.calculateActivations();
        }
        
        outputActs = new ArrayList<>();
        outputActs = neuronLayers.get(brainData.size()).getActivations();
        
        car.applyBrainOutput(outputActs);
    }
    
    //Vykreslení cele sítě graficky
    public void paint(Graphics gr, Playground pg) {
        Graphics2D g2d = (Graphics2D)gr;
        
        for(int i = 0; i < brainData.size()+1; i++) {
            neuronLayers.get(i).paintNeurons(g2d, pg, i*30);
        }
        
        for(NeuronLayer nl : neuronLayers) {
            if(nl.isIsInput()) continue;
            
            nl.paintWeights(g2d, pg);
        }
    }
}
