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
import math.geom2d.Box2D;
import math.geom2d.Point2D;

/**
 *
 * @author Denis Kurka
 */
public class Sensor {
    private final Playground pg;
    private final Car car;
    
    private Area area;
    private final double angleOffset;
    private final int length;
    private final int width;
    private final int offset;
    private Color fillColor;
    private Color cirColor;
    
    private Point pos;
    
    protected double distanceToBarrier;
    
    public Sensor(Car car, int length, int width, int offset, Color fillColor, Color cirColor, double angleOffset) {
        this.pg = car.getPg();
        
        this.car = car;
        this.length = pg.getScaledValue(length);
        this.width = pg.getScaledValue(width);
        this.offset = pg.getScaledValue(offset);
        this.fillColor = fillColor;
        this.cirColor = cirColor;
        this.angleOffset = angleOffset;
        
        this.distanceToBarrier = this.length;
    }
    
    //Getters and setters
    public Playground getPg() {
        return pg;
    }

    public Car getCar() {
        return car;
    }

    public Area getArea() {
        return area;
    }

    public double getAngleOffset() {
        return angleOffset;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public int getOffset() {
        return offset;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }
    
    public Color getCirColor() {
        return cirColor;
    }

    public void setCirColor(Color cirColor) {
        this.cirColor = cirColor;
    }
    
    public Point getPos() {
        return pos;
    }

    public double getDistanceToBarrier() {
        return distanceToBarrier;
    }
    //End of getters and setters

    public boolean detectCollision(Area colArea) {
        if(this.area == null || colArea == null) return false;
        
        return (this.area.intersects(colArea.getBounds2D()) && colArea.intersects(this.area.getBounds2D()));
    }
    
    public double distanceFromNearestBarrier(Graphics2D g2d) {
        double minDistance = this.length;
        
        for(Barrier b : pg.getBarriers()) {
            if(detectCollision(b.getArea())) {
                Area intersection = new Area(b.getArea());
                intersection.intersect(this.area);
                
                if(intersection.isEmpty()) continue;
                
                Rectangle2D intersectRect = intersection.getBounds2D();
                
                Box2D barPrecise = new Box2D(intersectRect);
                
                double distance = barPrecise.asRectangle().distance(new Point2D(car.getPos().x, car.getPos().y));
                //barPrecise.fill(g2d);
                
                if(distance < minDistance) {
                    minDistance = distance;
                }
            }
        }
        
        minDistance = 0.9*Math.log10(minDistance/this.length)+1;
        
        if(minDistance < 0) minDistance = 0;
        
        return minDistance;
    }
    
    void paint(Graphics gr, boolean showSensor) {
        Graphics2D g2d = (Graphics2D) gr;
        
        int x = (int)Math.round(car.getPos().x + offset);
        int y = (int)Math.round(car.getPos().y);
        pos = new Point(x,y);
        
        AffineTransform s1 = AffineTransform.getRotateInstance(car.getAngle()+angleOffset, car.getPos().x, car.getPos().y);
        area = new Area(s1.createTransformedShape(new Rectangle2D.Double(pos.x, pos.y - width/2, length, width)));
        
        distanceToBarrier = distanceFromNearestBarrier(g2d);
        
        if(showSensor) {
          g2d.setColor(fillColor);
          g2d.fill(area);
          g2d.setColor(cirColor);
          g2d.draw(area);  
        }
    }
}
