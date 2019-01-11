/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuralSandbox;

import java.awt.Point;

/**
 *
 * @author Denis Kurka
 */
public class BarrierSkeleton {
    private Point s;
    private int lenght, width;
    private double angle;

    public BarrierSkeleton(Point s, int lenght, int width, double angle) {
        this.s = s;
        this.lenght = lenght;
        this.width = width;
        this.angle = angle;
    }
    
    public Point getS() {
        return s;
    }

    public void setS(Point s) {
        this.s = s;
    }

    public int getLenght() {
        return lenght;
    }

    public void setLenght(int lenght) {
        this.lenght = lenght;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
}
