package name.hampton.mike;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class BaseCoercionTest
    extends TestCase {

  protected CoercionUtil coercionUtil;

  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public BaseCoercionTest(String testName) {
    super(testName);
    initCoercionUtil();
  }

  protected void initCoercionUtil() {
    coercionUtil = new CoercionUtil();
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(BaseCoercionTest.class);
  }

  @SuppressWarnings({"rawtypes", "deprecation"})
  protected void runTest(Class classToTest, Object[] expectedValues) throws Throwable {
    ExampleInterface coercedTest = null;
    try {
      coercedTest = coercionUtil.generateInterfaceImplementation(classToTest.newInstance(), ExampleInterface.class);
    } catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
    Object[] actualValues = new Object[3];
    actualValues[0] = coercedTest.getString();
    actualValues[1] = coercedTest.getInt();
    actualValues[2] = coercedTest.getBoolean();
    int idx = 0;
    for (Object actualValue : actualValues) {
      assertEquals(expectedValues[idx++], actualValue);
    }
  }

  @SuppressWarnings("rawtypes")
  protected void runTest(Class classToTest) throws Throwable {
    runTest(classToTest, new Object[]{"ExampleImplToCoerce", 5, true});
  }

  public void testCompleteProperImpl() {
    try {
      runTest(ExampleImplToCoerce1.class);
    } catch (Throwable t) {
      t.printStackTrace();
      fail(t.getMessage());
    }
  }

  /**
   * Tests that a missing method that returns an int will return 0 for the missing impl
   */
  public void testMissingMethodReturnsInt() {
    try {
      runTest(ExampleImplToCoerce2.class, new Object[]{"ExampleImplToCoerce", 0, true});
    } catch (Throwable t) {
      t.printStackTrace();
      fail(t.getMessage());
    }
  }

  public void testPrivateMethod() {
    try {
      runTest(ExampleImplToCoerce3.class);
    } catch (Throwable t) {
      t.printStackTrace();
      fail(t.getMessage());
    }
  }

  public void testMoreGenericReturntype() {
    try {
      runTest(ExampleImplToCoerce4.class);
    } catch (Throwable t) {
      t.printStackTrace();
      fail(t.getMessage());
    }
  }

  public void testThrowRuntimeException() {
    try {
      runTest(ExampleImplToCoerce5.class);
    } catch (Throwable t) {
      assertTrue("Proper exception thrown, no cause set", t instanceof RuntimeException && t.getCause() == null);
    }
  }

  public void testThrowCheckedExceptionInImplNotInIntf() {
    try {
      runTest(ExampleImplToCoerce6.class);
    } catch (Throwable t) {
      assertTrue("Proper exception thrown, cause set to checked exception", t instanceof RuntimeException && t.getCause() != null && !(t.getCause() instanceof RuntimeException));
    }
  }

  public void testThrowCheckedExceptionInImplAndIntf() {
    try {
      runTest(ExampleImplToCoerce7.class);
    } catch (Throwable t) {
      assertTrue("Proper exception thrown, cause set to checked exception", !(t instanceof RuntimeException) && t.getCause() == null);
    }
  }

  /**
   * Tests that an incompatible return type for a string return will return null
   */
  public void testIncompatibleReturnType() {
    try {
      runTest(ExampleImplToCoerce8.class, new Object[]{null, 5, true});
    } catch (Throwable t) {
      t.printStackTrace();
      fail(t.getMessage());
    }
  }

  public void testActualIntfImpl() {
    try {
      runTest(ExampleImplToCoerce9.class);
    } catch (Throwable t) {
      t.printStackTrace();
      fail(t.getMessage());
    }
  }
}
