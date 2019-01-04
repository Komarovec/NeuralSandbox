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
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Denis Kurka
 */
public class Barrier extends GameObject { 
    private int length, width;

    public Barrier(int length, int width, Playground pg, Point pos, Color fillColor, Color cirColor, double angle) {
        super(pg, pos, fillColor, cirColor, angle);
        this.length = pg.getScaledValue(length);
        this.width = pg.getScaledValue(width);
    }
    
    public Barrier(int length, int width, Playground pg, Point pos, double angle) {
        this(length, width, pg, pos, Color.GRAY, Color.BLACK, angle);
    }
    
    public Barrier(int length, int width, Playground pg, Point pos, Color fillColor, Color cirColor) {
        this(length, width, pg, pos, fillColor, cirColor, 0);
    }
    
    public Barrier(int length, int width, Playground pg, Point pos) {
        this(length, width, pg, pos, Color.GRAY, Color.BLACK, 0);
    }   
    
    public Barrier(Barrier b) {
        this(b.getLength(),b.getWidth(),b.getPg(),b.getPos(),b.getFillColor(),b.getCirColor(),b.getAngle());
    }
    
    public Barrier(Playground pg,Point a, Point b, double angle) {
        this(
             pg.getTrueValue(Math.abs(a.x - b.x)),
             pg.getTrueValue(Math.abs(a.y - b.y)), 
             pg, new Point(pg.getTrueValue((a.x+b.x)/2),pg.getTrueValue((a.y+b.y)/2)), 
             Color.GRAY, 
             Color.BLACK, 
             angle
        );        
    }
    
    //Getters and Setters
    public BarrierSkeleton getSkeleton() {
        return new BarrierSkeleton(new Point(pg.getTrueValue(pos.x), pg.getTrueValue(pos.y)), pg.getTrueValue(this.length), pg.getTrueValue(this.width),this.angle);
    }
    
    public int getLength() {
        return length;
    }

    public final void setLength(int length) {
        this.length = length;
    }

    public int getWidth() {
        return width;
    }

    public final void setWidth(int width) {
        this.width = width;
    }
    //End of Getters and Setters
    
    @Override
    public void paint(Graphics gr) {
        Graphics2D g2d = (Graphics2D)gr;
        g2d.setColor(fillColor);
        AffineTransform at = AffineTransform.getRotateInstance(this.angle, pos.x, pos.y);
        area = new Area(at.createTransformedShape(new Rectangle2D.Double(pos.x - length/2, pos.y - width/2, length, width)));
        //area = new Area(new Rectangle2D.Double(pos.x - length/2, pos.y - width/2, length, width));
        g2d.fill(area);
        
        g2d.setColor(cirColor);
        g2d.draw(area);
    }
}
