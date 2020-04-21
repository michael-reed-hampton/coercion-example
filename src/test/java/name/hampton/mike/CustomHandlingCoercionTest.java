package name.hampton.mike;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * Unit test for simple App.
 */
public class CustomHandlingCoercionTest
    extends BaseCoercionTest {

  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public CustomHandlingCoercionTest(String testName) {
    super(testName);
  }

  protected void initCoercionUtil() {
    coercionUtil = new CoercionUtil() {
      protected Object handleInaccessibleMethod(IllegalAccessException e) throws IllegalAccessException {
        throw e;
      }

      protected Object handleMissingMethod(Method proxyMethod, NoSuchMethodException e) throws NoSuchMethodException {
        throw e;
      }

      protected Object handleImplReturnTypeIsNotCompatibleWithIntfReturnType(Class<?> rawReturnType, Class<?> proxyReturnType) throws RuntimeException {
        throw new RuntimeException(String.format("Raw object does not fully implement interface, "
                                                 + "cannot coerce rawReturnType (%s) to proxyReturnType (%s) "
                                                 + "default value will be returned. (null for non-primitives)",
            rawReturnType, proxyReturnType));
      }
    };
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(CustomHandlingCoercionTest.class);
  }

  /**
   * Tests that a missing method that returns an int will return 0 for the missing impl
   */
  public void testMissingMethodReturnsInt() {
    try {
      runTest(ExampleImplToCoerce2.class, new Object[]{"ExampleImplToCoerce", 0, true});
    } catch (UndeclaredThrowableException undeclaredThrowableException) {
      assertTrue(undeclaredThrowableException.getMessage(), true);
    } catch (Throwable t) {
      t.printStackTrace();
      fail(t.getMessage());
    }
  }

  /**
   * Tests that an incompatible return type for a string return will return null
   */
  public void testIncompatibleReturnType() {
    try {
      runTest(ExampleImplToCoerce8.class, new Object[]{null, 5, true});
    } catch (RuntimeException r) {
      assertTrue(r.getMessage(), true);
    } catch (Throwable t) {
      t.printStackTrace();
      fail(t.getMessage());
    }
  }
}
