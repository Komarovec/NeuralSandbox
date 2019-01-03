/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticGame.neuralNetwork;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Denis Kurka
 */
public class Neuron {
    private NeuronLayer nl;
    
    private double activation;
    private ArrayList<Double> weights;
    
    public Neuron(NeuronLayer nl) {
        this.nl = nl;
        this.activation = 0;
        
        weights = new ArrayList();
    }

    //Getters and Setters
    public ArrayList<Double> getWeights() {
        return weights;
    }

    public void setWeights(ArrayList<Double> weights) {
        this.weights = weights;
    }

    public double getActivation() {
        return activation;
    }

    public void setActivation(double activation) {
        this.activation = activation;
    }

    public NeuronLayer getNl() {
        return nl;
    }
    //End of Getters and Setters
    
    public double activationFunction(double x) {
        //Aktivační funkce --> Mat. fce Sigmoid
        return (1/( 1 + Math.pow(Math.E,(-1*x))));
    }
    
    public void activate(ArrayList<Double> sourceActivations) {
        if(sourceActivations.isEmpty() || weights.isEmpty()) {
            System.out.println("Can't activate! No weights!");
            activation = 0;
            return;
        }
        
        double act = 0;
        int i = 0;
        for(Double s : sourceActivations) {
            act += (s * weights.get(i));
            i++;
        }
        
        activation = activationFunction(act);
    }
    
    public void randomWeights(int consCount) {
        weights.clear();
        
        Random rand = new Random();
        for (int i = 0; i < consCount; i++) {
            weights.add((rand.nextDouble()*2)-1);
        }
    }
}
