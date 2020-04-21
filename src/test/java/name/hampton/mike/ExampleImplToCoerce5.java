package name.hampton.mike;

/**
 * Created by michaelhampton on 4/13/20.
 */
class ExampleImplToCoerce5 {
    public String getString() {
        return "ExampleImplToCoerce";
    }

    public int getInt() {
        return 5;
    }

    public boolean getBoolean() {
        throw new NullPointerException("Crap");
    }
}
