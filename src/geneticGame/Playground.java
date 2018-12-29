/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticGame;

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
    private ArrayList<CarAI> cars;
    private ArrayList<CarAI> carsToRemove;
    
    private Car controled;
    private Spawn spawn;
    
    private final Dimension screenSize;
    private double scaleIndexX; //Přepokládá že poměr stran je 16:9
    
    private Point mousePos;
    
    private Point newBar;
    private Barrier tempBarrier;
    
    private int stater; //0 - Wait; 1 - Create Mode; 2 - Delete Mode
    
    
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
        
        timer = new Timer(10,this);
        timer.start();
        barriers = new ArrayList();
        cars = new ArrayList();
        carsToRemove = new ArrayList();
        
        spawn = new Spawn(this, new Point(getScaledValue(50),getScaledValue(50)));

        newBar = new Point(-1,-1);
        
        stater = 0;
        
        newCar(new CarAI(this, spawn.getSpawnpoint()));
    }

    public ArrayList<Barrier> getBarriers() {
        return barriers;
    }

    public void setBarriers(ArrayList<Barrier> barriers) {
        this.barriers = barriers;
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
    
    public void inFinish(CarAI car) {
        carsToRemove.add(car);
    }
    
    //Zajištuje korektní přidání do pole individuálu
    public void newCar(CarAI car) {
        cars.add(car);
        controled = car;
    }
    
    public boolean deleteBarrierFromPoint(Point a) {
        if(barriers.isEmpty())
            return false;
        
        for(Barrier item : barriers) {
            if(item.getArea().contains(a)) {
                barriers.remove(item);
                return true;
            }
        }
        return false;
    }
    
    //Zapne mod likvidaci barier
    public void deleteBarrier() {
        if(stater == 2) return;
        stater = 2;
    }
     
    //Zapne mod tvoření barier
    public void createBarrier() {
        if(stater == 1) return;
        stater = 1;
    }
     
    @Override
    protected void paintComponent(Graphics gr) {
        super.paintComponent(gr); //To change body of generated methods, choose Tools | Templates.  
        
        //Vykresli spawn a finish
        spawn.paint(gr);
        
        //Vykresli bariery
        if(!barriers.isEmpty()) {
            barriers.forEach((item) -> {
                item.paint(gr);
            });
        }
        
        if(tempBarrier != null) {
            tempBarrier.paint(gr);
        }
        
        //Iteruj pro všechny individualy
        if(!cars.isEmpty()) {
            cars.forEach((CarAI car) -> {
                car.paint(gr);
                
                /*Defaultni barva senzoru
                car.getS1().setFillColor(Color.CYAN);
                car.getS2().setFillColor(Color.CYAN);
                */
                
                //Nabourání do bariery
                barriers.forEach((br) -> {
                    //Kolize s autem
                    if(car.detectCollision(br.getArea()))
                        carsToRemove.add(car);
                    
                    /*
                    //Sepnuti senzorů
                    if(car.getS1().detectCollision(br.getArea()))
                        car.getS1().setFillColor(Color.RED);
                    
                    if(car.getS2().detectCollision(br.getArea()))
                        car.getS2().setFillColor(Color.RED);
                    */
                });         
            });
        }
        else {
            //Žadní individuálové ve hře
            newCar(new CarAI(this, spawn.getSpawnpoint()));
        }
        
        if(!carsToRemove.isEmpty()) {
            carsToRemove.forEach((car) -> {
                cars.remove(car);
            });
        }
    }
    
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
            tempBarrier = new Barrier(this ,newBar, e.getPoint(), 0);
            System.out.println("Creating new barrier ");
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePos = e.getPoint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //Vytváření bariery
        if(stater == 1)
            newBar = e.getPoint();
        
        //Mazaní bariery
        else if(stater == 2)
            deleteBarrierFromPoint(e.getPoint());

        System.out.println("Pressed: "+newBar);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(stater == 1) {
            //Vytvoř barieru na pevno
            barriers.add(tempBarrier);
            tempBarrier = null;
            
            //Reset
            newBar = new Point(-1,-1);
            stater = 0;
        }
        else if(stater == 2) {
            //Reset
            stater = 0;
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
