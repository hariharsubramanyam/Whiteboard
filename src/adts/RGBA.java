package adts;

/**
 * ADT representing a color with a red, green, blue, and alpha component 
 */

public class RGBA {
    
    /**
     * The red component of the color
     */
    private final int red;
    
    /**
     * The green component of the color
     */
    private final int green;
    
    /**
     * The blue component of the color
     */
    private final int blue;
    
    /**
     * The alpha component of the color
     */
    private final int alpha;
    
    /**
     * Creates a color with the given red, green, blue, and alpha values
     * @param red the red component (must be between 0 and 255 inclusive)
     * @param green the green component (must be between 0 and 255 inclusive)
     * @param blue the blue component (must be between 0 and 255 inclusive)
     * @param alpha the alpha component (must be between 0 and 255 inclusive)
     */
    public RGBA(int red, int green, int blue, int alpha){
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }
    
    /**
     * @return The red component of the color 
     */
    public int getRed(){
        return this.red;
    }
    
    /**
     * @return The green component of the color
     */
    public int getGreen(){
        return this.green;
    }
    
    /**
     * @return The blue component of the color
     */
    public int getBlue(){
        return this.blue;
    }
    
    /**
     * @return The alpha component of the color
     */
    public int getAlpha(){
        return this.alpha;
    }
    
    /**
     * @return A string of the (r=%d, g=%d, b=%d, a=%d) 
     */
    @Override
    public String toString() {
        return String.format("(r=%d, g=%d, b=%d, a=%d)", this.red, this.green, this.blue, this.alpha);
    }
    
    /**
     * @param obj the object to compare this to
     * @return true if the colors have the same components (red, green, blue, alpha) and obj is a RGBA
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RGBA))
            return false;
        RGBA other = (RGBA)obj;
        return (this.red == other.getRed() && this.green == other.getGreen() && this.blue == other.getBlue() && this.alpha == other.getAlpha());
    }
    
    /**
     * @return The hashcode of the toString of this method 
     */
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
