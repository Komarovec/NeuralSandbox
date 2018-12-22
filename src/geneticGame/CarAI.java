/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

/**
 *
 * @author Denis Kurka
 */
public class CarAI extends Car {
    private Sensor s1, s2;
    
    public CarAI(Playground pg, Point pos, int length, int width, Color fillColor, Color cirColor, int angle) {
        super(pg, pos, length, width, fillColor, cirColor, angle);
        
        s1 = new Sensor(this, 50, 8, 10, Color.CYAN, Color.BLACK, -Math.PI/4);
        s2 = new Sensor(this, 50, 8, 10, Color.CYAN, Color.BLACK, Math.PI/4);
    }
    
    public CarAI(Playground pg, Point pos) {
        this(pg, pos, pg.getScaledValue(20), pg.getScaledValue(8), Color.RED, Color.BLACK, 0);
    }
    
    public CarAI(Playground pg) {
        this(pg, new Point(pg.getWidth()/2,pg.getHeight()/2), pg.getScaledValue(20), pg.getScaledValue(8), Color.RED, Color.BLACK, 0);
    }
    
    public Sensor getS1() {
        return s1;
    }

    public Sensor getS2() {
        return s2;
    }
    
    public void paint(Graphics gr) {
        super.paint(gr);
        
        s1.paint(gr);
        s2.paint(gr);
        
    }
}
