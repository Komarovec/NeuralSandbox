/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticGame;

import geneticGame.genetics.Population;
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
    
    private int stater; //0 - Wait; 1 - Create Mode; 2 - Change angle mode; 3 - Delete Mode; 4 - Change positions; 5 - Change position spawn
    
    
    public Playground(MainFrame mf) {
        this.mf = mf;
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        init();
    }
    
    private void init() {
        //Škálovat vše podle FullHD
        scaleIndexX = screenSize.getWidth()/1920;
        this.setSize(getScaledValue(1100),getScaledValue(800));
        this.setLocation(new Point(0,30));
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
        finish = new Finish(this, new Point(this.getTrueValue(this.getWidth())-100, this.getTrueValue(this.getHeight())-100));

        newBar = new Point(-1,-1);
        
        stater = 0;
        
        population = null;
        mutationRate = 5;
        popCount = 200;
        populationTimerDelay = 20000;
        carFeedbackSensor = false;
        
        controled = null;
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
        super.paintComponent(gr);
        
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
        
        //Vykresli hrače
        if(controled != null) {
            controled.paint(gr);
            controled.paintBrain(gr);
        }
    }
    
    public void startEvolution() {
        controled = null;
        population = new Population(this, popCount, mutationRate, brainTemplate, carFeedbackSensor, populationTimerDelay);
    }
    
    public void nextgen() {
        if(population != null) {
            population.endTest();
        }
    }
    
    public void spawnPlayer() {
        population = null;
        controled = new CarAI(this, spawn.getSpawnpoint());
        controled.setPlayerControl(true);
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
    }

    @Override
    public void mouseDragged(MouseEvent e) {
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
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePos = e.getPoint();
        
        //Otáčej barieru
        if(stater == 2 && newBar.x != -1) {
            tempBarrier.setAngle(((double)e.getPoint().y/(double)this.getHeight())*2*Math.PI);
            System.out.println("Rotating new barrier.");
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //Vytváření bariery
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
            if(tempBarrier == null && spawn.getArea().contains(e.getPoint())) {
                stater = 5;
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
                //Reset
                stater = 0;
                break;
            case 4:
                //Reset
                barriers.add(tempBarrier);
                stater = 0;
                tempBarrier = null;
                break;
            case 5:
                //Reset
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
