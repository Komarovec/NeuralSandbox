/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticGame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author Denis Kurka
 */
public class Playground extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private Car test;
    
    private final Set<Character> pressed = new HashSet<Character>();

    
    public Playground() {
        init();
    }
    
    private void init() {
        this.setSize(new Dimension(1200, 1000));
        this.setBackground(Color.white);
        this.setFocusable(true);
        this.addKeyListener(this);
        timer = new Timer(10,this);
        timer.start();
        
        test = new Car(this);
    }
    
    @Override
    protected void paintComponent(Graphics gr) {
        super.paintComponent(gr); //To change body of generated methods, choose Tools | Templates.   
        test.paint(gr);
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
        switch(e.getKeyChar()) {
            case 'w':
                test.addForce(false);
                break;
            case 's':
                test.addForce(true);
                break;
            case 'a':
                test.startRotation(true);
                break;
            case 'd':
                test.startRotation(false);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch(e.getKeyChar()) {
            case 'w':
                test.stopForce();
                break;
            case 's':
                test.stopForce();
                break;
            case 'a':
                test.stopRotation();
                break;
            case 'd':
                test.stopRotation();
                break;
        }
    }
    
}
