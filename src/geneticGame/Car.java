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
    protected Playground pg;
    
    
    //Car vars
    protected int length, width;
    protected Point pos;
    protected Color fillColor, cirColor;
    protected double speed;
    protected boolean accNeg;
    protected double angle;
    protected boolean rotating;
    protected boolean rotNeg;
    protected double[] forceToAdd;
    protected Area area;
    protected boolean isAccelerating;
    
    
    
    
    protected int maxSpeed;
    protected double acceleration;
    
    public Car(Playground pg, Point pos, int length, int width, Color fillColor, Color cirColor, int angle) {    
        this.setPg(pg);
        this.setPos(pos);
        this.setLength(pg.getScaledValue(length));
        this.setWidth(pg.getScaledValue(width));
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
        
        this.acceleration = pg.getScaledValue(0.2);
        this.maxSpeed = (int)Math.round(pg.getScaledValue(3));
    }
    
    public Car(Playground pg, Point pos, int length, int width, Color fillColor, Color cirColor) {
        this(pg, pos, length, width, fillColor, cirColor, 0);
    }
    
    public Car(Playground pg, Point pos, int length, int width) {
        this(pg, pos, length, width, Color.RED, Color.BLACK, 0);
    }
    
    public Car(Playground pg, Point pos) {
        this(pg, pos, 20, 8, Color.RED, Color.BLACK, 0);
    }
    
    public Car(Playground pg) {
        this(pg, new Point(pg.getWidth()/2,pg.getHeight()/2), pg.getScaledValue(20), pg.getScaledValue(8), Color.RED, Color.BLACK, 0);
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
    
    //Zjištije kolizi s dannou Areou
    public boolean detectCollision(Area colArea) {
        return (area.getBounds2D().intersects(colArea.getBounds2D()));
    }
    
    //Přidá sílu k rozložení do os a následnemu zrychlení
    public void addForce(boolean isNegative) {
        if(isAccelerating) return;
        isAccelerating = true;
        accNeg = isNegative;
    }
    
    //Zastaví zrychlení
    public void stopForce() {
        isAccelerating = false;
    }
    
    //Povoli rotaci
    public void startRotation(boolean isNegative) {
        if(rotating) return;
        rotating = true;
        rotNeg = isNegative;
    }
    
    //Zastaví rotaci
    public void stopRotation() {
        rotating = false;
    }
    
    //Postupne otáčení
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
      
       
        //Zrychlení reaaly smooooth
        if(isAccelerating && speed < maxSpeed && speed > -maxSpeed) {
            if(accNeg)
                speed -= acceleration/2;
            else
                speed += acceleration/2;
        }
        else if(speed != 0) {
            if(speed > acceleration)
                speed -= acceleration;
            else if(speed < -acceleration)
                speed += acceleration;
            else
                speed = 0;
        }
        
        
        //Rozložení sil podle sinovy a cosinovy věty
        double spx = speed*cos(angle);
        double spy = speed*sin(angle);
        
        //Out of bounds
        if(pos.x > pg.getWidth())
            spx = -1;
        else if(pos.x < 0)
            spx = 1;
        
        if(pos.y+width/2 > pg.getHeight())
            spy = -1;
        else if(pos.y < 0)
            spy = 1;
        
        
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

        //System.out.println("forceToAdd["+forceToAdd[0]+", "+forceToAdd[1]+"] Speed: "+speed);
        
        Point newPos = new Point(this.getPos().x+(int)spx,this.getPos().y+(int)spy);
        this.setPos(newPos);
    }
    
    public void paint(Graphics gr) {
        Graphics2D g2d = (Graphics2D) gr;
        /*
                AffineTransform2D at = AffineTransform2D.createRotation(pos.x, pos.y, this.angle);
        Rectangle2D cartangle = new Rectangle2D(pos.x - length/2, pos.y - width/2, length, width);
        SimplePolygon2D carToDraw = cartangle.transform(at);
        
        g2d.setColor(fillColor);
        carToDraw.fill(g2d);
        g2d.setColor(cirColor);
        carToDraw.draw(g2d);
         */

        //Car
        AffineTransform at = AffineTransform.getRotateInstance(this.angle, pos.x, pos.y);
        area = new Area(at.createTransformedShape(new Rectangle2D.Double(pos.x - length/2, pos.y - width/2, length, width)));
        
        g2d.setColor(fillColor);
        g2d.fill(area);
        g2d.setColor(cirColor);
        g2d.draw(area);
  
        this.move();
        this.rotate();
    }
}
