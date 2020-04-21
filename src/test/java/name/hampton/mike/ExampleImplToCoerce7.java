package name.hampton.mike;

/**
 * Created by michaelhampton on 4/13/20.
 */
class ExampleImplToCoerce7 {
    public Object getString() {
        return "ExampleImplToCoerce";
    }

    public int getInt() throws MyCheckedException {
        throw new MyCheckedException("MyCheckedException thrown from method that DOES declare it in proxy");
    }

    public boolean getBoolean() {
        return true;
    }
}
