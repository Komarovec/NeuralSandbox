/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuralSandbox;

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
    
    protected double shearFriction;
    protected double accelConstant;
    protected double angleConstant;
    
    protected double maxSpeed;
    protected double maxSpeedConstant;
    
    protected boolean frozen;
    
    public Car(Playground pg, Point pos, int length, int width, Color fillColor, Color cirColor, int angle) {    
        this.setPg(pg);
        this.setPos(pos);
        this.setLength(pg.getScaledValue(length));
        this.setWidth(pg.getScaledValue(width));
        this.setFillColor(fillColor);
        this.setCirColor(cirColor);
        this.setAngle(angle);
        
        frozen = false;
        
        forceToAdd = new double[2];
        forceToAdd[0] = 0;
        forceToAdd[1] = 0;
        
        this.rotating = false;
        this.rotNeg = false;
        this.isAccelerating = false;
        this.accNeg = false;
        this.speed = 0;
        this.angle = 0;
        
        this.shearFriction = pg.getScaledValue(0.05);
        this.accelConstant = pg.getScaledValue(0.2); // --> MAX 1/5 --> 0.2 Experimentálně určeno
        this.angleConstant = 0.08; //cca Math.PI/40
        
        this.maxSpeed = pg.getScaledValue(3);
        this.maxSpeedConstant = pg.getScaledValue(3);
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

    public final void setAngle(double angle) {
        this.angle = angle;
    }

    public Area getArea() {
        return area;
    }
    
    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
        this.fillColor = Color.cyan;
    }
    // End of Getters and Setters
    
    
    //Zjištije kolizi s dannou Areou
    public boolean detectCollision(Area colArea) {
        return (this.area.intersects(colArea.getBounds2D()) && colArea.intersects(this.area.getBounds2D()));
    }
    
    
    // --- Ovládání binární {0,1} ---
    //Určuje sílu k zrychlení
    public void setForce(boolean isNegative) {
        this.maxSpeed = (isNegative) ? -maxSpeedConstant : maxSpeedConstant;
    }
    
    
    //Zastaví zrychlení
    public void stopForce() {
        maxSpeed = 0;
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
    
    // --- Ovládání analog <-1; 1> ---
    //Pouze --> <-1; 1>
    public void setForce(double accel) {
        if(Math.abs(accel) > 1) return;
        maxSpeed = pg.getScaledValue(accel);
    }
    
    //Ovladací funkce - rotace --> Pouze <0; 1>
    public void setRotation(double angle) {
        if(Math.abs(angle) > 1) return;
        
        angle = (angle/6.25)-0.08; //Přeškálování  z <0;1> do Úhlů <-0.08;0.08>
        
        this.angle += angle;
    }
    
    
    
    //Postupne otáčení pouze v případě klávesnice
    public void rotateManual() {
        if(rotating) {
            angle += (rotNeg) ? -angleConstant : angleConstant;
        }
    }
    
    
    public void move() {
        //if(speed == 0 && !isAccelerating) return;
      
       
        //Zrychlení reaaly smooooth
        if(Math.abs(speed) < maxSpeed) {
            speed += accelConstant;
        }
        


        if(speed != 0) {
            if(Math.abs(speed) < shearFriction)
                speed = 0;
            else
                speed += (speed > 0) ? -shearFriction : shearFriction;
        }
       
        
        
        //Rozložení sil podle sinovy a cosinovy věty
        double spx = speed*cos(angle);
        double spy = speed*sin(angle);
        
        //Out of bounds
        if(pos.x > pg.getWidth() || pos.x < 0 || pos.y+width/2 > pg.getHeight() || pos.y < 0) {
            this.setFrozen(true);
        }
        
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

        //Car
        AffineTransform at = AffineTransform.getRotateInstance(this.angle, pos.x, pos.y);
        area = new Area(at.createTransformedShape(new Rectangle2D.Double(pos.x - length/2, pos.y - width/2, length, width)));
        
        g2d.setColor(fillColor);
        g2d.fill(area);
        g2d.setColor(cirColor);
        g2d.draw(area);
        
        if(!frozen) {
            this.move();
            this.rotateManual();
        }
    }
}
