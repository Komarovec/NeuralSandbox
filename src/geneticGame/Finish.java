/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

/**
 *
 * @author Denis Kurka
 */
public class Finish extends GameObject { 
    private int radius;

    public Finish(int radius, Playground pg, Point pos, Color fillColor, Color cirColor, double angle) {
        super(pg, pos, fillColor, cirColor, angle);
        this.radius = radius;
    }
    
    public Finish(int radius, Playground pg, Point pos, double angle) {
        this(radius, pg, pos, Color.GREEN, Color.BLACK, angle);
    }
    
    public Finish(int radius, Playground pg, Point pos, Color fillColor, Color cirColor) {
        this(radius, pg, pos, fillColor, cirColor, 0);
    }
    
    public Finish(int radius, Playground pg, Point pos) {
        this(radius, pg, pos, Color.GREEN, Color.BLACK, 0);
    }
    
    public Finish(Playground pg, Point pos) {
        this(pg.getScaledValue(30), pg, pos, Color.GREEN, Color.BLACK, 0);
    }
    

    //Getters and Setters
    public Point getFinishpoint() {
        return new Point((int)Math.round(pos.x+radius*1.5),(int)Math.round(pos.y+radius*1.5));
    }
    
    public int getRadius() {
        return radius;
    }
    
    public void setRadius(int radius) {
        this.radius = radius;
    }
    //End of Getters and Setters
   
    
    @Override
    public void paint(Graphics gr) {
        Graphics2D g2d = (Graphics2D)gr;
        g2d.setColor(fillColor);
        AffineTransform at = AffineTransform.getRotateInstance(this.angle, pos.x, pos.y);
        //area = new Area(at.createTransformedShape(new Ellipse2D.Double(radius,radius,pos.x-radius,pos.y-radius)));
        area = new Area(new Ellipse2D.Double(pos.x+radius,pos.y+radius, radius, radius));
        g2d.fill(area);
        
        g2d.setColor(cirColor);
        g2d.draw(area);
    }
}
