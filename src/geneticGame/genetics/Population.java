/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticGame.genetics;

import geneticGame.Car;
import java.util.ArrayList;

/**
 *
 * @author Denis Kurka
 */
public class Population {
    private ArrayList<Car> individuals;
    private int popCount;
    private int generation;
    private int mutationRate;
    
    public Population() {
        popCount = 0;
        generation = 0;
    }

    //Getters
    public ArrayList<Car> getIndividuals() {
        return individuals;
    }

    public int getPopCount() {
        return popCount;
    }
    
    public int getGeneration() {
        return generation;
    }
    
    
    public void generateRandomPopulation() {
        //Vygenerovat nahodne DNA
    }
    
    public void crossover() {
        //Zkrizit geny nejlepsich z generace
    }
    
    public void mutation() {
        //Provest mutaci vsech z populace (1%)
    }
}
