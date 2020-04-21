package name.hampton.mike;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility to coerce an object into implementing a particular interface.
 *
 * This is useful for cases where an object cannot be changed to directly implement a specific interface, but
 * it does implement the interface by convention.
 *
 * Created by michaelhampton on 4/9/20.
 */
public class CoercionUtil {

  private Logger logger = Logger.getLogger(DelegateInvocationHandler.class.getSimpleName());

  @SuppressWarnings("unchecked")
  public <T> T generateInterfaceImplementation(Object raw, Class<T> interfaceClass) {
    // If the passed raw object already implements or is a subclass of the passed interface,
    // then just directly return it.
    if (interfaceClass.isAssignableFrom(raw.getClass())) {
      return (T)raw;
    }
    // Otherwise wrap it in the interface.
    InvocationHandler handler = new DelegateInvocationHandler(raw);
    return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
        new Class[]{interfaceClass},
        handler);
  }

  class DelegateInvocationHandler implements InvocationHandler {
    private final Object raw;
    public DelegateInvocationHandler(Object raw) {
      this.raw = raw;
    }

    @Override
    public Object invoke(Object proxy, Method proxyMethod, Object[] args) throws Throwable {
      Method rawMethod;
      try {
        rawMethod = raw.getClass().getDeclaredMethod(proxyMethod.getName(), proxyMethod.getParameterTypes());
        // Check the return type. This could result in a class cast exception during runtime if not assignable
        Class<?> rawReturnType = rawMethod.getReturnType();
        Class<?> proxyReturnType = proxyMethod.getReturnType();
        if (!rawReturnType.isAssignableFrom(proxyReturnType)) {
          return handleImplReturnTypeIsNotCompatibleWithIntfReturnType(rawReturnType, proxyReturnType);
        }
        // This lets us get to private methods.
        rawMethod.setAccessible(true);
      } catch (NoSuchMethodException e) {
        // a matching method is not found
        // or if the name is "&lt;init&gt;"or "&lt;clinit&gt;".
        return handleMissingMethod(proxyMethod, e);
      }
      try {
        return rawMethod.invoke(raw, args);
      } catch (IllegalAccessException e) {
        return handleInaccessibleMethod(e);
      } catch (InvocationTargetException e) {
        // if the underlying method
        // throws an exception.
        Class<?>[] exceptionTypes = proxyMethod.getExceptionTypes();
        // Lets see if the exception can be thrown as is.  We will go through the
        // Exception types declared by the interface method and if the exception that was
        // Thrown is one of them, or a subclass of one of them, then we can throw it.
        // Alternatively, if the interface method declares that it throws 'InvocationTargetException' itself (or its
        // base class 'ReflectiveOperationException') we can throw as well.
        Throwable rawThrown = e.getCause();
        if (exceptionTypes.length > 0) {
          boolean canThrowInvocationTargetException = false;

          for (Class<?> exType : exceptionTypes) {
            if (exType.isAssignableFrom(rawThrown.getClass())) {
              throw rawThrown;
            }
            if (exType.isAssignableFrom(InvocationTargetException.class)) {
              canThrowInvocationTargetException = true;
            }
          }
          // Only throw this if none of the others can be thrown.
          if (canThrowInvocationTargetException) {
            throw e;
          }
        }
        if (RuntimeException.class.isAssignableFrom(rawThrown.getClass())) {
          // Can always throw a runtime exception
          throw rawThrown;
        }
        // Ok stuff went bad and the caller needs to know it,  We have to throw something, so
        // wrap the exception in a runtime exception.
        String message = "Exception thrown from raw object that cannot be thrown in type safe manner."
                         + "Generic RuntimeException will be thrown with cause set to original cause in "
                         + "InvocationTargetException.";
        logger.log(Level.INFO, e, () -> message);
        throw new RuntimeException(message, e.getCause());
      }
    }
  }

  protected Object handleInaccessibleMethod(IllegalAccessException e) throws IllegalAccessException {
    // if this {@code Method} object
    // is enforcing Java language access control and the underlying
    // method is inaccessible.
    logger.log(Level.INFO, e, () -> "Java language access control is enforced on this method and the underlying "
                                    + "method is inaccessible. Raw object method cannot be accessed, null will be returned.");
    return null;
  }

  protected Object handleMissingMethod(Method proxyMethod, NoSuchMethodException e) throws NoSuchMethodException {
    logger.log(Level.INFO, e, () -> "Raw object does not fully implement interface, default value will be returned. (null for non-primitives)");
    Class<?> returnType = proxyMethod.getReturnType();
    if (returnType.isPrimitive()) {
      return getDefaultValue(returnType);
    }
    return null;
  }

  protected Object handleImplReturnTypeIsNotCompatibleWithIntfReturnType(Class<?> rawReturnType, Class<?> proxyReturnType) throws RuntimeException {
    logger.log(Level.INFO, String.format("Raw object does not fully implement interface, "
                                         + "cannot coerce rawReturnType (%s) to proxyReturnType (%s) "
                                         + "default value will be returned. (null for non-primitives)",
        rawReturnType, proxyReturnType));
    // The return types are not compatible.
    // Decided to return default here.
    if (proxyReturnType.isPrimitive()) {
      return getDefaultValue(proxyReturnType);
    }
    return null;
  }


  /**
   * Some versions of Java do not make it easy to get a default value of a primitive.
   * This is a bit of a hack, but it works in all versions I have found.
   *
   * @param clazz type to get the default value of
   * @param <T> generic of the type we want the default of.
   * @return the default value for the type T
   */
  @SuppressWarnings("unchecked")
  protected <T> T getDefaultValue(Class<T> clazz) {
    return (T) Array.get(Array.newInstance(clazz, 1), 0);
  }
}
