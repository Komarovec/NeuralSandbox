/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuralSandbox;

import neuralSandbox.genetics.Population;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import static java.lang.Character.toLowerCase;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author Denis Kurka
 */
public class Playground extends JPanel implements ActionListener, KeyListener, MouseMotionListener, MouseListener {
    private final MainFrame mf;
    
    private Timer timer;
    private int frames;
    private ArrayList<Barrier> barriers;
    private ArrayList<Barrier> barriersToDelete;
    
    private CarAI controled;
    private Spawn spawn;
    private Finish finish;
    
    private Population population;
    private ArrayList<Integer> brainTemplate;
    private int mutationRate;
    private int popCount;
    private boolean carFeedbackSensor;
    private int populationTimerDelay;
    
    private final Dimension screenSize;
    private double scaleIndexX; //Přepokládá že poměr stran je 16:9
    
    private Point mousePos;
    
    private Point newBar;
    private Barrier tempBarrier;
    
    private int stater; //0 - Wait; 1 - Create Mode; 2 - Change angle mode; 3 - Delete Mode; 4 - Change positions; 5 - Change position spawn; 6 - Change position finish
    private int viewMod; //0 - Free look; 1 - Follow mod
    private boolean learning; 
    
    private boolean isAlt;
    private Point pivot;
    private Point pgPivot;
    
    
    public Playground(MainFrame mf) {
        this.mf = mf;
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        init();
    }
    
    private void init() {
        //Škálovat vše podle FullHD (Nevýhoda toho mít 4K monitor .. musíte vše škálovat xD)
        scaleIndexX = screenSize.getWidth()/1920;
        this.setSize(getScaledValue(4000),getScaledValue(4000));
        this.setLocation(new Point(0,0));
        this.setBackground(Color.white);
        this.setFocusable(true);
        
        this.addKeyListener(this);
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
        
        timer = new Timer(0,this);
        timer.start();
        barriers = new ArrayList();
        barriersToDelete = new ArrayList();
        brainTemplate = new ArrayList<>(Arrays.asList(3,2));
        
        spawn = new Spawn(this, new Point(50,50));
        finish = new Finish(this, new Point(200,200));

        newBar = new Point(-1,-1);
        
        stater = 0;
        
        population = null;
        controled = null;
        isAlt = false;
        frames = 0;
        
        //Default settings
        mutationRate = 5;
        popCount = 200;
        populationTimerDelay = 15000;
        carFeedbackSensor = false;
        viewMod = 0;
        learning = true;
        
        //Debug
        //spawnPlayer();
    }

    //Getters and setters
    public MainFrame getMf() {
        return mf;
    }

    public ArrayList<Barrier> getBarriers() {
        return barriers;
    }

    public void setBarriers(ArrayList<Barrier> barriers) {
        this.barriers = barriers;
    }

    public Spawn getSpawn() {
        return spawn;
    }

    public void setSpawn(Spawn spawn) {
        this.spawn = spawn;
    }

    public Finish getFinish() {
        return finish;
    }

    public void setFinish(Finish finish) {
        this.finish = finish;
    }

    public ArrayList<Integer> getBrainTemplate() {
        return brainTemplate;
    }

    public void setBrainTemplate(ArrayList<Integer> brainTemplate) {
        if(brainTemplate == null) return;
        System.out.println("Brain template: "+brainTemplate.toString());
        this.brainTemplate = brainTemplate;
    }

    public int getMutationRate() {
        return mutationRate;
    }

    public void setMutationRate(int mutationRate) {
        this.mutationRate = mutationRate;
    }

    public int getPopCount() {
        return popCount;
    }

    public void setPopCount(int popCount) {
        this.popCount = popCount;
    }

    public boolean isCarFeedbackSensor() {
        return carFeedbackSensor;
    }

    public void setCarFeedbackSensor(boolean carFeedbackSensor) {
        this.carFeedbackSensor = carFeedbackSensor;
    }

    public int getPopulationTimerDelay() {
        return populationTimerDelay;
    }

    public void setPopulationTimerDelay(int populationTimerDelay) {
        this.populationTimerDelay = populationTimerDelay;
    }

    public Population getPopulation() {
        return population;
    }

    public void setPopulation(Population population) {
        if(population != null) {
            endEvolution();
        }
        startEvolution(population);
    }

    public int getViewMod() {
        return viewMod;
    }

    public void setViewMod(int viewMod) {
        this.viewMod = viewMod;
    }

    public int getFrames() {
        return frames;
    }

    public void setFrames(int frames) {
        this.frames = frames;
    }

    public boolean isLearning() {
        return learning;
    }

    public void setLearning(boolean learning) {
        this.learning = learning;
        if(population != null) {
            population.setLearning(learning);
        }
    }
    //End of getters end setters
    
    //Funkce zajištující škálování
    public Dimension getScreenSize() {
        return screenSize;
    }
    
    public int getScaledValue(int val) {
        return (int)Math.round(val*scaleIndexX);
    }
    
    public double getScaledValue(double val) {
        return val*scaleIndexX;
    }
    
    public int getTrueValue(int val) {
        return (int)Math.round(val/scaleIndexX);
    }
    
    public double getTrueValue(double val) {
        return val/scaleIndexX;
    }
    
    public double getScaleIndexX() {
        return scaleIndexX;
    }
    //Konec funkci zajištující škálování

    
    //Smaž barieru pokud je na pointu
    public boolean deleteBarrierFromPoint(Point a) {
        Barrier del = getBarrierFromPoint(a);
        if(del == null) {
            return false;
        }
        else {
            barriers.remove(del);
            return true;
        }
    }
    
    //Vrať barrieru na pointu nebo null
    public Barrier getBarrierFromPoint(Point a) {
        if(barriers.isEmpty())
            return null;
        
        for(Barrier item : barriers) {
            if(item.getArea().contains(a)) {
                return item;
            }
        }
        return null;
    }
    
    //Zapne mod likvidaci barier
    public void deleteBarrier() {
        if(stater == 3) return;
        stater = 3;
    }
     
    //Zapne mod tvoření barier
    public void createBarrier() {
        if(stater == 1) return;
        stater = 1;
    }
     
    //Zapne mod pro menění barier
    public void changeBarrier() {
        if(stater == 4) return;
        stater = 4;
    }
    
    @Override
    protected void paintComponent(Graphics gr) {
        if(!learning) return;
        super.paintComponent(gr);
        
        //Počítá snímky
        frames++;
        
        //Vykresli spawn
        spawn.paint(gr);
        
        //Vykresli finish
        finish.paint(gr);
        
        //Vykresli bariery
        if(!barriers.isEmpty()) {
            barriers.forEach((item) -> {
                if(item != null) {
                    item.paint(gr);
                }
                    
                else {
                    barriersToDelete.add(item);
                }
            });
        }
        
        if(!barriersToDelete.isEmpty()) {
            for(Barrier b : barriersToDelete) {
                barriers.remove(b);
            }
            barriersToDelete.clear();
        }
        
        //Vykresli tvořící se barierů
        if(tempBarrier != null) {
            tempBarrier.paint(gr);
        }
        
        //Vykresli populaci
        if(population != null) {
            population.paint(gr);
        }
        
        if(viewMod == 1) {
            if(population == null || population.getIndividuals().isEmpty()) {
                this.setLocation(0,0);
            }
            else {
                Point bestPos = population.getIndividuals().get(population.getBestFitCurrentIndex()).getPos();
                this.setLocation((-bestPos.x) + mf.getPlaygroundPanel().getWidth()/2, -bestPos.y + mf.getPlaygroundPanel().getHeight()/2);
            }
        }
        
        //Vykresli hrače <><>WIP<><> DEBUG ONLY
        if(controled != null) {
            controled.paint(gr);
            controled.paintBrain(gr);
        }
    }
    
    //Začne evolvovat populaci
    public void startEvolution(Population pop) {
        controled = null;
        mf.setVisibleStatus(true);
        mf.setFitnessValue(0);
        mf.setPopAliveValue(0);
        if(population != null)
            population.clearPopulation();
        
        if(pop == null) {
            mf.setGenerationValue(0);
            population = new Population(this, popCount, mutationRate, brainTemplate, carFeedbackSensor, populationTimerDelay);
        }
        else {
            mf.setGenerationValue(pop.getGeneration());
            population = pop;
        }
    }
    
    //Ukončí evoluci a SMAŽE populaci!
    public void endEvolution() {
        if(population == null) return;
        population.clearPopulation();
        population = null;
    }
    
    //Preskočí testovanou generaci
    public void nextgen() {
        if(population != null) {
            population.endTest();
        }
    }
    
    //Spawne hráče <><>WIP<><> DEBUG ONLY
    public void spawnPlayer() {
        population = null;
        controled = new CarAI(this, spawn.getSpawnpoint());
        controled.setPlayerControl(true);
        controled.setShowSensors(true);
    }
    
    //Loop funkce
    @Override
    public void actionPerformed(ActionEvent ae) {
        this.repaint();
    }

    //Manuální ovládání
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(controled != null) {
            switch(toLowerCase(e.getKeyChar())) {
                case 'w':
                    controled.setForce(1);
                    break;
                case 's':
                    controled.setForce(-1);
                    break;
                case 'a':
                    controled.startRotation(true);
                    break;
                case 'd':
                    controled.startRotation(false);
                    break;
            }
        }

        if(e.getKeyCode() == 18) {
            isAlt = true;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(controled != null) {
            switch(toLowerCase(e.getKeyChar())) {
                case 'w':
                    controled.stopForce();
                    break;
                case 's':
                    controled.stopForce();
                    break;
                case 'a':
                    controled.stopRotation();
                    break;
                case 'd':
                    controled.stopRotation();
                    break;
            }
        }
        
        if(e.getKeyCode() == 18) {
            isAlt = false;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(isAlt) {
            this.setLocation(pgPivot.x-(pivot.x-e.getLocationOnScreen().x), pgPivot.y-(pivot.y-e.getLocationOnScreen().y));
        }
        //Posuvná bariera
        if(stater == 1 && newBar.x != -1) {
            if(newBar.y != e.getPoint().y || newBar.x != e.getPoint().x) {
                tempBarrier = new Barrier(this ,newBar, e.getPoint(), 0);
                System.out.println("Creating new barrier ");
            }
            else {
                tempBarrier = null;
                System.out.println("Cant create barrier points too close! ");
            }
        }
        
        else if(stater == 4 && tempBarrier != null) {
            tempBarrier.setPos(e.getPoint());
        }
        else if(stater == 5) {
            spawn.setSpawnpoint(e.getPoint());
        }
        else if(stater == 6) {
            finish.setFinishpoint(e.getPoint());
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePos = e.getPoint();
        
        //Otáčej barieru
        if(stater == 2 && newBar.x != -1) {
            tempBarrier.setAngle(((double)e.getPoint().y/(double)mf.getHeight())*2*Math.PI);
            System.out.println("Rotating new barrier.");
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //Vytváření bariery
        if(isAlt) {
            pivot = e.getLocationOnScreen();
            pgPivot = this.getLocation();
        }
        
        //První pozice tvoření bariery
        if(stater == 1) {
            newBar = e.getPoint();
            tempBarrier = null;
        }
        
        //Otáčení
        else if(stater == 2) {
            //Přestaň vytvářet
            if(tempBarrier != null) {
                barriers.add(tempBarrier);
                tempBarrier = null;
            }

            //Reset
            newBar = new Point(-1,-1);
            stater = 0;
        }
        
        //Mazaní bariery
        else if(stater == 3)
            deleteBarrierFromPoint(e.getPoint());

        //Měnění pozice bariery
        else if(stater == 4) {
            tempBarrier = getBarrierFromPoint(e.getPoint());
            
            //Měnění pozice startu či cíle
            if(tempBarrier == null && spawn.getArea().contains(e.getPoint())) {
                stater = 5;
            }
            else if(tempBarrier == null && finish.getArea().contains(e.getPoint())) {
                stater = 6;
            }
            barriers.remove(tempBarrier);
        }
        
        System.out.println("Pressed: "+newBar);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        switch (stater) {
            case 1:
                //Začni otáčet
                stater = 2;
                break;
            case 3:
                //Reset Delete
                stater = 0;
                break;
            case 4:
                //Reset Pozice
                barriers.add(tempBarrier);
                stater = 0;
                tempBarrier = null;
                break;
            case 5:
                //Reset Spawn
                stater = 0;
                tempBarrier = null;
                break;
            case 6:
                //Reset Finish
                stater = 0;
                tempBarrier = null;
                break;
            default:
                break;
        }
        
        
        System.out.println("Released");
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }  
}
