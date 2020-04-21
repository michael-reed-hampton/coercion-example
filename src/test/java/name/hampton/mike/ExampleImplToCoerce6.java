package name.hampton.mike;

/**
 * Created by michaelhampton on 4/13/20.
 */
class ExampleImplToCoerce6 {
    public Object getString() {
        return "ExampleImplToCoerce";
    }

    public int getInt() {
        return 5;
    }

    public boolean getBoolean() throws MyCheckedException {
        throw new MyCheckedException("MyCheckedException thrown from method that does not declare it in proxy");
    }
}
