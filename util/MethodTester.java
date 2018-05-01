import java.util.function.BiPredicate;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

public class MethodTester<O> extends Tester {

    protected BiPredicate<O, O> equalityTester;
    protected Method method;
    protected Class<?> paramTypes[];
    protected boolean staticMethod;

    protected Class<?> cls;
    protected String className;
    protected Constructor<?> constructor;

    public MethodTester(String className, String methodName, int modifier, Class<?> returnType, Class<?>...paramTypes) {

        if (!failedToForm) {

            cls = TestUtils.classExists(className);
            if (cls == null || TestUtils.classIsAbstract(cls) || TestUtils.classIsEnum(cls)
                || TestUtils.classIsFinal(cls) || TestUtils.classIsInterface(cls)) {
                setSingleMessageResult("Class not found.",
                    "Class expected: public class " + className + ".",
                    false);
                failedToForm = true;
                return;
            }
            this.className = className;

            method = TestUtils.accessibleDeclaredMethodExists(cls, methodName, paramTypes);
            if (method == null) {
                setSingleMessageResult("Method not found.",
                    String.format("Method expected: %s.", 
                                TestUtils.methodToString(modifier, returnType, methodName, paramTypes)), false);
                failedToForm = true;
            } else if (!TestUtils.methodHasReturnType(method, returnType)) {
                setSingleMessageResult("Method not found.", 
                    String.format("Method expected: %s.", TestUtils.methodToString(modifier, returnType, methodName, paramTypes)), false);
                failedToForm = true;
            } else if (TestUtils.methodIsAbstract(method)) {
                setSingleMessageResult("Method is abstract.",
                    String.format("Method expected: %s.", TestUtils.methodToString(modifier, returnType, methodName, paramTypes)), false);
                failedToForm = true;
            } else if (!TestUtils.methodHasModifiers(method, modifier)) {
                setSingleMessageResult("Method not found.", String.format("Method expected: %s.",
                        TestUtils.methodToString(modifier, returnType, methodName, paramTypes)), false);
                failedToForm = true;
            } else {
    
                staticMethod = TestUtils.methodIsStatic(method);
                this.paramTypes = paramTypes;
            
            }
            
        }

    }

    public void noArgsConstructor() {
        constructor = TestUtils.hasAccessibleConstructor(cls, new Class<?>[] {});
        if (constructor == null) {
            setSingleMessageResult("Constructor not found.", "No args constructor not found.", false);
            failedToForm = true;
        }
    }

    public Class<?> getCls() {
        return cls;
    }

    public void setEqualityTester(BiPredicate<O, O> equalityTester) {
        this.equalityTester = equalityTester;
    }

    public Method getMethod() {
        return method;
    }

    public static void useLoosDoubleEquality(MethodTester<Double> t, double epsilon) {
        t.equalityTester = (expected, actual) -> {

            if (actual == null || expected == null || !(expected instanceof Double) || !(actual instanceof Double)) {
                return false;
            }

            double exp = (Double) expected;
            double act = (Double) actual;

            if (act + epsilon > exp && act - epsilon < exp) {
                return true;
            }

            return false;
        };
    }

    public static void useLooseStringEquality(MethodTester<String> t) {
        useLooseStringEquality(t, true, true);
    }

    public static void useLooseStringEquality(MethodTester<String> t, boolean onWhiteSpace, boolean onNewLine) {
        t.equalityTester = (expected, actual) -> {

            if (actual == null || expected == null) {
                return false;
            }
            
            // This must come first because new lines count as white space
            if (onNewLine) {
                expected = expected.replaceAll("\\r\\n|\\r|\\n", "");
                actual = actual.replaceAll("\\r\\n|\\r|\\n", "");                
            }
            if (onWhiteSpace) {
                expected = expected.replaceAll("\\s+", "");
                actual = actual.replaceAll("\\s+", "");
            }

            return actual.matches(expected);
        };
    }
}