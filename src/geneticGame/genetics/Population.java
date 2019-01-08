/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticGame.genetics;

import geneticGame.Barrier;
import geneticGame.CarAI;
import geneticGame.Playground;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Denis Kurka
 */
public final class Population {
    private Playground pg;
    
    private ArrayList<Integer> brainTemplate;
    private ArrayList<CarAI> individuals;
    private ArrayList<CarAI> carsToDelete;
    
    private boolean carFeedbackSensor;
    private int popCount;
    private int generation;
    private int mutationRate;
    private int timerDelay;
    private Timer timer;
    
    private Color avgColor;
    private Color bestColor;
    
    private CarAI bestFit;
    private int bestFitCurrentIndex;
    
    
    private int stater; // 0 -- Waiting; 1 -- Testing; 2 -- Calculating/Mutating/Evolving  
    
    public Population(Playground pg, int popCount, int mutationRate, ArrayList<Integer> brainTemplate, boolean carFeedbackSensor, int timerDelay) {
        this.pg = pg;
        this.popCount = popCount;
        this.brainTemplate = brainTemplate;
        this.carFeedbackSensor = carFeedbackSensor;
        this.timerDelay = timerDelay;
        generation = 0;
        
        timer = new Timer("test");
        
        individuals = new ArrayList<>();
        carsToDelete = new ArrayList<>();
        
        avgColor = Color.red;
        bestColor = Color.yellow;
        
        this.mutationRate = mutationRate;
        
        generateRandomPopulation();
        startTest();
    }

    //Getters and setters
    public ArrayList<CarAI> getIndividuals() {
        return individuals;
    }

    public int getPopCount() {
        return popCount;
    }
    
    public int getGeneration() {
        return generation;
    }

    public int getMutationRate() {
        return mutationRate;
    }

    public void setMutationRate(int mutationRate) {
        this.mutationRate = mutationRate;
    }

    public CarAI getBestFit() {
        return bestFit;
    }

    public int getBestFitCurrentIndex() {
        return bestFitCurrentIndex;
    }
    //End of getters and setters
    
    //Vytvoří nové auto :)
    public void newCar(CarAI car) {
        individuals.add(car);
    }
    
    //Náhodná populace
    public void generateRandomPopulation() { 
        for(int i = 0; i < this.popCount; i++) {
           newCar(new CarAI(brainTemplate, pg, pg.getSpawn().getSpawnpoint(), carFeedbackSensor));
        }
        //Inicializace bestFit
        bestFit = individuals.get(0);
        bestFitCurrentIndex = 0;
    }
    
    //Sort
    public static ArrayList<CarAI> bubbleSortByFitness(ArrayList<CarAI> cars){
        for (int i = 0; i < cars.size() - 1; i++) {
            for (int j = 0; j < cars.size() - i - 1; j++) {
                if(cars.get(j).getFitness() < cars.get(j+1).getFitness()){
                    CarAI tmp = cars.get(j);
                    cars.set(j, cars.get(j+1));
                    cars.set(j+1, tmp);
                }
            }
        }
        return cars;
    }   
    
    public final ArrayList<ArrayList<ArrayList<Double>>> mergeBrains(ArrayList<ArrayList<ArrayList<Double>>> brain1, ArrayList<ArrayList<ArrayList<Double>>> brain2) {
        ArrayList<ArrayList<ArrayList<Double>>> brainData = new ArrayList<>();
        
        Random rand = new Random();
        
        for(int i = 0; i < brain1.size(); i++) {
            brainData.add(new ArrayList<>());
            
            for(int j = 0; j < brain1.get(i).size(); j++) {
                brainData.get(i).add(new ArrayList<>());
                
                for(int k = 0; k < brain1.get(i).get(j).size(); k++) {
                    //Mutace
                    if(rand.nextInt(100) < mutationRate) {
                        //System.out.println("MUTATION!");
                        brainData.get(i).get(j).add((rand.nextDouble()*2)-1);
                    }
                    else if(k % 2 == 0) {
                       brainData.get(i).get(j).add(brain1.get(i).get(j).get(k));
                    }
                    else {
                       brainData.get(i).get(j).add(brain2.get(i).get(j).get(k));
                    }
                }
            }
        }
         
        return brainData;
    }
    
    //Zkrizit geny nejlepsich z generace
    public void crossover() {
        double fitnessSum = 0;
        Random rand = new Random();
        
        //Vypočita kolik z populace bude max vybrano
        ArrayList<CarAI> picked = new ArrayList<>();
        int pickCount = (int)Math.ceil(this.popCount/20);
       
        //Suma populacniho fitnessu
        for(CarAI car : individuals) {
            fitnessSum += car.getFitness();
        }

        //Vybirani jedincu k pareni
        int i = 0;
        while(picked.size() < pickCount) {
            if(i >= individuals.size()) {
                i = 0;
            }
            CarAI car = individuals.get(i);
            
            double chance = car.getFitness()/fitnessSum;
            double randChance = rand.nextDouble()*100;

            if(randChance < chance) {
                //PICKED!
                picked.add(car);
            }
            
            i++;
        }
        
        //Sort podle fitnessu
        picked = bubbleSortByFitness(picked);

        for(CarAI car : picked) {
            if(car.getFitness() > bestFit.getFitness()) {
                pg.getMf().setFitnessValue(bestFit.getFitness());
                bestFit = car;
            }
            System.out.println(car.getFitness());
        }
        
        individuals.clear();

        //Náhodné páření (z 1/20 nejlepších) do populace
        for(int j = 0; j < this.popCount; j++) {
            int randomPick = rand.nextInt(pickCount);
            CarAI parent1 = picked.get(randomPick);
            
            randomPick = rand.nextInt(pickCount);
            CarAI parent2 = picked.get(randomPick);
            
            CarAI temp = new CarAI(pg, pg.getSpawn().getSpawnpoint(), mergeBrains(parent1.getBrain().getBrainData(), parent2.getBrain().getBrainData()), brainTemplate, carFeedbackSensor);
            newCar(temp);
        }
    }
   
    //Spusti testování populace
    public void startTest() {
        stater = 1;    
        
        pg.getMf().setGenerationValue(generation);
        
        timer.cancel();
        timer = new Timer("TestTimer");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                endTest();
            }
        }, timerDelay);
    }
    
    //Ukonci testovani populace a zhodnoti jejich výsledky
    public void endTest() {
        if(stater == 2) return;
        timer.cancel();
        
        System.out.println("-------------------------------");
        System.out.println("Individuals: "+individuals.size());
        System.out.println("-------------------------------");
        
        stater = 2;

        //Refresh hodnot z UI
        this.popCount = pg.getPopCount();
        this.mutationRate = pg.getMutationRate();
        this.timerDelay = pg.getPopulationTimerDelay();
        
        //Do crossover, mutation and create offstrings frozen-->individuals
        crossover();
        generation++;
        
        System.out.println(individuals.size());
        System.out.println("Generation: "+generation);
        System.out.println("-------------------------------");
        
        startTest();
    }
    
    public void paint(Graphics gr) {
        //Iteruj pro všechny individualy
        
        if(stater == 1) {  
            //Vykreslí mozek pouze od nejlepšího z generace 
            int frozenCount = 0;
            for(CarAI car : individuals) {
                car.paint(gr);
                
                if(car.isFrozen()) {
                    frozenCount++;
                    continue;
                }
                car.calculateFitness();
                
                //Najdi nejlepšiho jedince v generaci
                if(car.getFitness() > individuals.get(bestFitCurrentIndex).getFitness() || individuals.get(bestFitCurrentIndex).isFrozen()) {
                    individuals.get(bestFitCurrentIndex).setFillColor(individuals.get(bestFitCurrentIndex).isFrozen() ? Color.cyan : avgColor);
                    bestFitCurrentIndex = individuals.indexOf(car);
                }
                
                //Dojel co cíle
                if(car.detectCollision(pg.getFinish().getArea())) {
                    car.setFrozen(true);
                    car.setFillColor(Color.PINK);
                    car.setFitness(Math.pow(car.getFitness(),4));
                }
                
                //Nabourání do bariery
                for(Barrier br : pg.getBarriers()){       
                    if(car.detectCollision(br.getArea())) {
                        car.setFrozen(true);
                        car.setFillColor(Color.cyan);
                        break;
                    }
                }
            }
            
            pg.getMf().setPopAliveValue(popCount-frozenCount);
            
            if(individuals.get(bestFitCurrentIndex).isFrozen()) {
                individuals.get(bestFitCurrentIndex).paintBrain(gr);
            }
            else {
                individuals.get(bestFitCurrentIndex).paintBrain(gr);
                individuals.get(bestFitCurrentIndex).setFillColor(bestColor);
            }
            
            if(frozenCount == individuals.size()) {
                endTest();
            }
        }
    }
}
