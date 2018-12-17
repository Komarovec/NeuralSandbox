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
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 *
 * @author Denis Kurka
 */
public class Car {
    private Playground pg;
    
    private int length, width;
    private Point pos;
    private Color fillColor, cirColor;
    private double speed;
    private boolean accNeg;
    private double angle;
    private boolean rotating;
    private boolean rotNeg;
    private double[] forceToAdd;
    private Area area;
    private boolean isAccelerating;
    
    public Car(Playground pg, Point pos, int length, int width, Color fillColor, Color cirColor, int angle) {
        this.setPg(pg);
        this.setPos(pos);
        this.setLength(length);
        this.setWidth(width);
        this.setFillColor(fillColor);
        this.setCirColor(cirColor);
        this.setAngle(angle);
        
        forceToAdd = new double[2];
        forceToAdd[0] = 0;
        forceToAdd[1] = 0;
        
        this.rotating = false;
        this.rotNeg = false;
        this.isAccelerating = false;
        this.accNeg = false;
        this.speed = 0;
        this.angle = 0;
    }
    
    public Car(Playground pg, Point pos, int length, int width, Color fillColor, Color cirColor) {
        this(pg, pos, length, width, fillColor, cirColor, 0);
    }
    
    public Car(Playground pg, Point pos, int length, int width) {
        this(pg, pos, length, width, Color.RED, Color.BLACK, 0);
    }
    
    public Car(Playground pg, Point pos) {
        this(pg, pos, 50, 20, Color.RED, Color.BLACK, 0);
    }
    
    public Car(Playground pg) {
        this(pg, new Point(pg.getWidth()/2,pg.getHeight()/2), 50, 20, Color.RED, Color.BLACK, 0);
    }

    //Getters and Setters
    public Playground getPg() {
        return pg;
    }

    public final void setPg(Playground pg) {
        this.pg = pg;
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

    public final void setAngle(int angle) {
        this.angle = angle;
    }

    public Area getArea() {
        return area;
    }
    // End of Getters and Setters
    
    public void addForce(boolean isNegative) {
        if(isAccelerating) return;
        isAccelerating = true;
        accNeg = isNegative;
    }
    
    public void stopForce() {
        isAccelerating = false;
    }
    
    public void startRotation(boolean isNegative) {
        if(rotating) return;
        rotating = true;
        rotNeg = isNegative;
    }
    
    public void stopRotation() {
        rotating = false;
    }
    
    public void rotate() {
        if(rotating) {
            if(rotNeg)
                angle -= Math.PI/40;
            else 
                angle += Math.PI/40;
        }
    }
    
    public void move() {
        //if(speed == 0 && !isAccelerating) return;
        
        //Out of bounds
        if(pos.x > pg.getWidth() || pos.y > pg.getHeight() || pos.x < 0 || pos.y < 0)
            this.setPos(new Point(pg.getWidth()/2, pg.getHeight()/2));
       
        //Zrychlení reaaly smooooth
        if(isAccelerating && speed < 5 && speed > -5) {
            if(accNeg)
                speed -= 0.1;
            else
                speed += 0.1;
        }
        else if(speed != 0) {
            if(speed > 0.2)
                speed -= 0.2;
            else if(speed < -0.2)
                speed += 0.2;
            else
                speed = 0;
        }
        
        
        //Rozložení sil podle sinovy a cosinovy věty
        double spx = speed*cos(angle);
        double spy = speed*sin(angle);
        
        
        /*   --- Pohyb menší než 1 pixel ---
        * Pokud je Speed menší než jeden pixel (1)
        * Tak ho ulož a až se naskupí víc než 1
        * Tak ho aplikuj
        */

        //Přidej k kompenzaci nepřesnost ze zrychlení a z výpočtu sin, cos
        forceToAdd[0] += spx - Math.floor(spx);
        forceToAdd[1] += spy - Math.floor(spy);
        spx = Math.floor(spx);
        spy = Math.floor(spy);
        
        //Kompenzace osy X
        if(forceToAdd[0] >= 1) {
            spx++;
            forceToAdd[0]--;
        }
        else if(forceToAdd[0] <= -1) {
            spx--;
            forceToAdd[0]++;
        }
        
        //Kompenzace osy Y
        if(forceToAdd[1] >= 1) {
            spy++;
            forceToAdd[1]--;
        }
        else if(forceToAdd[1] <= -1) {
            spy--;
            forceToAdd[1]++;
        }

        System.out.println("forceToAdd["+forceToAdd[0]+", "+forceToAdd[1]+"] Speed: "+speed);
        
        Point newPos = new Point(this.getPos().x+(int)spx,this.getPos().y+(int)spy);
        this.setPos(newPos);
    }
    
    public void paint(Graphics gr) {
        Graphics2D g2d = (Graphics2D) gr;
        g2d.setColor(fillColor);
        AffineTransform at = AffineTransform.getRotateInstance(this.angle, pos.x, pos.y);
        area = new Area(at.createTransformedShape(new Rectangle2D.Double(pos.x - length/2, pos.y - width/2, length, width)));
        //area = new Area(new Rectangle2D.Double(pos.x - length/2, pos.y - width/2, length, width));
        g2d.fill(area);
        
        g2d.setColor(cirColor);
        g2d.draw(area);
        
        this.move();
        this.rotate();
    }
}
