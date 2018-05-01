import java.lang.reflect.InvocationTargetException;

public interface ActualMethodInvoker<O> {
    O apply(Object cls, Object[] args) throws IllegalAccessException, InvocationTargetException;
}