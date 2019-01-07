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
import java.awt.Point;
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
    private ArrayList<CarAI> frozen;
    private ArrayList<CarAI> carsToDelete;
    
    private boolean carFeedbackSensor;
    private int popCount;
    private int generation;
    private int mutationRate;
    private int timerDelay;
    private Timer timer;
    
    private Color avgColor;
    private Color bestColor;
    
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
        frozen = new ArrayList<>();
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

    public ArrayList<CarAI> getFrozen() {
        return frozen;
    }

    public void setFrozen(ArrayList<CarAI> frozen) {
        this.frozen = frozen;
    }

    public int getMutationRate() {
        return mutationRate;
    }

    public void setMutationRate(int mutationRate) {
        this.mutationRate = mutationRate;
    }
    //End of getters and setters
    
    //Vytvoří nové auto :)
    public void newCar(CarAI car) {
        individuals.add(car);
    }
    
    //Náhodná populace
    public void generateRandomPopulation() { 
        for(int i = 0; i < this.popCount; i++)
           newCar(new CarAI(brainTemplate, pg, pg.getSpawn().getSpawnpoint(), carFeedbackSensor));
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

        ArrayList<CarAI> picked = new ArrayList<>();
        int pickCount = (int)Math.ceil(this.popCount/20);
       
        for(CarAI car : frozen) {
            fitnessSum += car.getFitness();
        }

        int i = 0;
        while(picked.size() < pickCount) {
            if(i >= frozen.size()) {
                i = 0;
            }
            CarAI car = frozen.get(i);
            
            double chance = car.getFitness()/fitnessSum;
            double randChance = rand.nextDouble()*100;

            if(randChance < chance) {
                //PICKED!
                picked.add(car);
            }
            
            i++;
        }
        
        picked = bubbleSortByFitness(picked);
        
        for(CarAI car : picked) {
            System.out.println(car.getFitness());
        }
        
        individuals.clear();

        //Náhodné páření (z 1/10 nejlepších) do populace
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
        timer = new Timer("test");
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
        System.out.println("Individuals: "+individuals.size()+" Frozen: "+frozen.size()+" Combined:"+(individuals.size()+frozen.size()));
        System.out.println("-------------------------------");
        
        stater = 2;
        if(!individuals.isEmpty()) {
            for(CarAI car : individuals) {
                car.setFrozen(true);
                frozen.add(car);
            }
        }
        individuals.clear();

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
        

        frozen.clear();
        
        startTest();
    }
    
    public void paint(Graphics gr) {
        //Iteruj pro všechny individualy
        if(!frozen.isEmpty()) {
            for(CarAI car : frozen) {
                car.paint(gr);
            }
        }
        
        if(!individuals.isEmpty() && stater == 1) {         
            individuals.get(0).paintBrain(gr);
            individuals.get(0).setFillColor(bestColor);
            
            for(CarAI car : individuals) {
                car.paint(gr);
                car.calculateFitness();
                
                if(car.detectCollision(pg.getFinish().getArea())) {
                    car.setFrozen(true);
                    car.setFillColor(Color.PINK);
                    frozen.add(car);
                    carsToDelete.add(car);
                    
                    car.setFitness(Math.pow(car.getFitness(),4));
                    System.out.println("Solution found!");
                }
                
                //Nabourání do bariery
                for(Barrier br : pg.getBarriers()){
                    //Kolize s autem              
                    if(car.detectCollision(br.getArea())) {
                        car.setFrozen(true);
                        car.setFillColor(Color.cyan);
                        carsToDelete.add(car);
                        frozen.add(car);
                        break;
                    }
                }
            }
            
            for(CarAI car : carsToDelete) {
                individuals.remove(car);
            }
            carsToDelete.clear();
            
        }
        else {
            endTest();
        }
    }
}
