package name.hampton.mike;

/**
 * Created by michaelhampton on 4/13/20.
 */
class ExampleImplToCoerce3 {
    public String getString() {
        return "ExampleImplToCoerce";
    }

    public int getInt() {
        return 5;
    }

    private boolean getBoolean() {
        // there is code to make this accessible to the proxy
        return true;
    }
}
