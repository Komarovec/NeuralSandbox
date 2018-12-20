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
import static java.lang.Character.toLowerCase;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author Denis Kurka
 */
public class Playground extends JPanel implements ActionListener, KeyListener {
    private final MainFrame mf;
    
    private Timer timer;
    private ArrayList<Barrier> barriers;
    private ArrayList<Car> cars;
    private ArrayList<Car> carsToRemove;
    
    private Car controled;
    
    private final Dimension screenSize;
    private double scaleIndexX; //Přepokládá že poměr stran je 16:9

    
    public Playground(MainFrame mf) {
        this.mf = mf;
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        init();
    }
    
    private void init() {
        //Škálovat vše podle FullHD
        scaleIndexX = screenSize.getWidth()/1920;
        
        this.setSize(getScaledValue(1200),getScaledValue(1000));
        this.setLocation(new Point(0,30));
        this.setBackground(Color.white);
        this.setFocusable(true);
        this.addKeyListener(this);
        
        timer = new Timer(10,this);
        timer.start();
        barriers = new ArrayList();
        cars = new ArrayList();
        carsToRemove = new ArrayList();
        
        barriers.add(new Barrier(1000, 20, this, new Point(getScaledValue(600),getScaledValue(200)), Math.PI/8));
        
        
        newCar(new Car(this, new Point(getWidth()/8, getHeight()/2)));
    }

    public Dimension getScreenSize() {
        return screenSize;
    }
    
    public int getScaledValue(int val) {
        return (int)Math.round(val*scaleIndexX);
    }
    
    public double getScaledValue(double val) {
        return val*scaleIndexX;
    }
    
    public double getScaleIndexX() {
        return scaleIndexX;
    }
    
    public void newCar(Car car) {
        cars.add(car);
        controled = car;
    }
    
    @Override
    protected void paintComponent(Graphics gr) {
        super.paintComponent(gr); //To change body of generated methods, choose Tools | Templates.  
        
        if(!barriers.isEmpty()) {
            barriers.forEach((item) -> {
                item.paint(gr);
            });
        }
        
        if(!cars.isEmpty()) {
            cars.forEach((Car car) -> {
                car.paint(gr);

                barriers.forEach((br) -> {
                    if(car.detectCollision(br.getArea()))
                        carsToRemove.add(car);
                });
            });
        }
        else {
            newCar(new Car(this));
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

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(controled != null) {
            switch(toLowerCase(e.getKeyChar())) {
                case 'w':
                    controled.addForce(false);
                    break;
                case 's':
                    controled.addForce(true);
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
    
}
