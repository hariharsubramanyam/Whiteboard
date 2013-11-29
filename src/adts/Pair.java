package adts;

/**
 * ADT representing a pair of integers of the form (x,y)
 */

public class Pair {
    /**
     * The x coordinate
     */
    private final int x;
    
    /**
     * The y coordinate
     */
    private final int y;
    
    /**
     * Constructs a pair (x,y) given the x and y coordinates
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public Pair(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    /**
     * @return The x coordinate
     */
    public int getX(){
        return this.x;
    }
    
    /**
     * @return The y coordinate
     */
    public int getY(){
        return this.y;
    }
    
    /**
     * @return (x,y)
     */
    @Override
    public String toString() {
        return String.format("(%d,%d)", this.x, this.y);
    }
    
    /**
     * @return the hashcode of the string (x,y) where x and y are the coordinates of this Pair
     */
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
    
    /**
     * @param obj the object to compare this to
     * @return true is this Pair has the same x and y coordinates as obj
     */
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Pair))
            return false;
        Pair otherPair = (Pair)obj;
        return (this.x == otherPair.getX() && this.y == otherPair.getY());
    }

}
