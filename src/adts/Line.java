package adts;

import java.awt.Color;

public class Line {
    
    /**
     * The first x coordinate
     */
    private final float x1;
    
    /**
     * The first y coordinate
     */
    private final float y1;
    
    /**
     * The second x coordinate
     */
    private final float x2;
    
    /**
     * The second y coordinate
     */
    private final float y2;
    
    /**
     * The stroke thickness
     */
    private final float strokeThickness;
    
    /**
     * The alpha value
     */
    private final float a;
    
    /**
     * The red value
     */
    private final float r;

    /**
     * The green value
     */
    private final float g;
    
    /**
     * The blue value
     */
    private final float b;
    
    /**
     * @param x1 The first x coordinate
     * @param y1 The first y coordinate
     * @param x2 The second x coordinate
     * @param y2 The second y coordinate
     * @param strokeThickness The stroke thickness
     * @param a The alpha value
     * @param r The red value
     * @param g The green value
     * @param b The blue value
     */
    public Line(float x1, float y1, float x2, float y2, float strokeThickness, float a, float r, float g, float b){
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.strokeThickness = strokeThickness;
        this.a = a;
        this.r = r;
        this.g = g;
        this.b = b;
    }
    
    public Color getColor(){
        return new Color(r, g, b, a);
    }

    /**
     * @return The first x coordinate
     */
    public float getX1() {
        return x1;
    }

    /**
     * @return The first y coordinate
     */
    public float getY1() {
        return y1;
    }

    /**
     * @return The second x coordinate
     */
    public float getX2() {
        return x2;
    }

    /**
     * @return The second y coordinate
     */
    public float getY2() {
        return y2;
    }

    /**
     * @return The stroke thickness
     */
    public float getStrokeThickness() {
        return strokeThickness;
    }

    /**
     * @return The alpha value
     */
    public float getA() {
        return a;
    }

    /**
     * @return The red value
     */
    public float getR() {
        return r;
    }

    /**
     * @return The green value
     */
    public float getG() {
        return g;
    }

    /**
     * @return The blue value
     */
    public float getB() {
        return b;
    }
    
    @Override
    public String toString() {
        return String.format("%f %f %f %f %f %f %f %f %f",this.getX1(),
                this.getY1(),
                this.getX2(),
                this.getY2(),
                this.getStrokeThickness(),
                this.getR(),
                this.getG(),
                this.getB(),
                this.getA()
                );
    }
    
}
