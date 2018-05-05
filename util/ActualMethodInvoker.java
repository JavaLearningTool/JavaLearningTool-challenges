import java.lang.reflect.InvocationTargetException;

/**
 * Functional interface that is used with FunctionReturnTesters. An instance of
 * this interface can be used to call the actual method in some class with some
 * arguments
 * 
 * Actual method is the method the student wrote.
 * 
 * @param <O> the return type of the actual method
 */
public interface ActualMethodInvoker<O> {
    /**
     * Call the actual method on some class with some arguments. Used to call the
     * actual method in a FunctionReturnTester
     * 
     * @param obj  The object to call the actual method on
     * @param args The arguments to pass into the actual method
     * @return The return from the actual method.
     */
    O apply(Object obj, Object[] args) throws IllegalAccessException, InvocationTargetException;
}