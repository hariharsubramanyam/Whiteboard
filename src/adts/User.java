package adts;

/**
 *  Represents a user with a name and id
 */
public class User {
    /**
     * The id of the user, this does not change!
     */
    private final int id;
    
    /**
     * The name of the user, this could change
     */
    private String name;
    
    /**
     * Constructs the user with the given id and name
     * @param id the id of the user
     * @param name the name of the user
     */
    public User(int id, String name){
        this.id = id;
        this.name = name;
    }
    
    /**
     * Constructs a user with the given id. The name is set to be "User" + id (ex. if id=2, the name would be "User2")
     * @param id the id of the user
     */
    public User(int id){
        this(id, "User"+id);
    }
    
    /**
     * @return the id of this user
     */
    public int getID(){
        return this.id;
    }
    
    /**
     * @return the name of this user
     */
    public String getName(){
        return this.name;
    }
    
    /**
     * @param name the name of this user
     */
    public void setName(String name){
        this.name = name;
    }
    
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString();
    }
    
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return super.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        return super.equals(obj);
    }
}
