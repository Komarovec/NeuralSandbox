/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticGame.neuralNetwork;

import geneticGame.CarAI;

import java.util.ArrayList;

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
}
