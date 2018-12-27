/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Area;
import java.io.Serializable;

/**
 *
 * @author Denis Kurka
 */
public abstract class GameObject {
    protected Playground pg;
    
    protected Point pos;
    protected Color fillColor, cirColor;
    protected double angle;
    
    protected Area area;

    public GameObject(Playground pg, Point pos, Color fillColor, Color cirColor, double angle) {
        this.pg = pg;
        this.pos = pos;
        this.fillColor = fillColor;
        this.cirColor = cirColor;
        this.angle = angle;
    }
    
    public GameObject(Playground pg, Point pos, double angle) {
        this(pg, pos, Color.GRAY, Color.BLACK, angle);
    }
    
    public GameObject(Playground pg, Point pos, Color fillColor, Color cirColor) {
        this(pg, pos, fillColor, cirColor, 0);
    }
    
    public GameObject(Playground pg, Point pos) {
        this(pg, pos, Color.GRAY, Color.BLACK, 0);
    }   
        
    
    //Getters and Setters
    public Playground getPg() {
        return pg;
    }

    public final void setPg(Playground pg) {
        this.pg = pg;
    }
    
    public Point getPos() {
        return pos;
    }

    public final void setPos(Point pos) {
        this.pos = pos;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public final void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public Color getCirColor() {
        return cirColor;
    }

    public final void setCirColor(Color cirColor) {
        this.cirColor = cirColor;
    }

    public double getAngle() {
        return angle;
    }

    public final void setAngle(double angle) {
        this.angle = angle;
    }

    public Area getArea() {
        return area;
    }

    public final void setArea(Area area) {
        this.area = area;
    }
    //End of Getters and Setters
    
    
    abstract public void paint(Graphics gr);
}
