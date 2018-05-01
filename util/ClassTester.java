import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class ClassTester extends Tester {

    protected static ClassTester tester;

    protected Class<?> expected;
    protected Class<?> actual;

    protected HashMap<String, MethodTestCase> methodMap = new HashMap<String, MethodTestCase>();
    protected HashMap<String, ConstructorTestCase> constructorMap = new HashMap<String, ConstructorTestCase>();
    protected HashMap<String, FieldTestCase> fieldMap = new HashMap<String, FieldTestCase>();
    protected final HashMap<String, Object[]> testVariables = new HashMap<String, Object[]>();
    private List<TestCaseGroup> groups = new ArrayList<TestCaseGroup>();

    protected List<String> actions = new ArrayList<String>();
    private String className;

    public ClassTester(String expectedClass, String actualClass) {

        tester = this;

        try {
            expected = Class.forName(expectedClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Expected class not found.");
        }

        try {
            actual = Class.forName(actualClass);
        } catch (ClassNotFoundException e) {
            setSingleMessageResult("Class misnamed.", "Class name expected: " + actualClass + ".", false);
            failedToForm = true;
            return;
        }

        // Get modifiers but ignore static
        int expectedModifiers = expected.getModifiers() & (~Modifier.STATIC);

        if (expectedModifiers != actual.getModifiers()) {

            setSingleMessageResult("Class not found.",
                    "Class expected: " + Modifier.toString(expectedModifiers) + " class " + actualClass + ".", false);
            failedToForm = true;
            return;
        }

        className = actualClass;

        Method[] methods = expected.getDeclaredMethods();
        Method[] actualMethods = actual.getDeclaredMethods();
        Constructor[] constructors = expected.getDeclaredConstructors();
        Constructor[] actualConstructors = actual.getDeclaredConstructors();
        Field[] fields = expected.getDeclaredFields();
        Field[] actualFields = actual.getDeclaredFields();

        for (Method m : methods) {
            for (Annotation annot : m.getAnnotations()) {

                if (annot instanceof TestedMember) {

                    TestedMember annotation = (TestedMember) annot;

                    int[] paramIsClass = annotation.paramIsClass();
                    boolean returnIsClass = annotation.returnIsClass();

                    Method actualMethod = null;
                    for (Method meth : actualMethods) {
                        if (TestUtils.methodEquals(m, meth, returnIsClass, paramIsClass, actual)) {
                            actualMethod = meth;
                            break;
                        }
                    }

                    if (actualMethod == null) {
                        setSingleMessageResult("Method not found.",
                                String.format("Method expected: %s.", TestUtils.methodToString(m.getModifiers(),
                                        m.getReturnType(), m.getName(), m.getParameterTypes())),
                                false);
                        failedToForm = true;
                        return;
                    } else {
                        String annotName = annotation.name();
                        if (annotName.equals("")) {
                            annotName = actualMethod.getName();
                        }

                        methodMap.put(annotName, new MethodTestCase(annotName, m, actualMethod, annotation.equality(),
                                annotation.stringConverter(), annotation.paramIsClass(), annotation.returnIsClass()));
                    }
                }
            }
        }

        for (Constructor c : constructors) {
            for (Annotation annot : c.getAnnotations()) {

                if (annot instanceof TestedMember) {

                    Constructor actualConstructor = null;
                    for (Constructor con : actualConstructors) {
                        if (TestUtils.constructorEquals(con, c)) {
                            actualConstructor = con;
                            break;
                        }
                    }

                    if (actualConstructor == null) {
                        setSingleMessageResult("Constructor not found.",
                                String.format("Constructor expected: %s.", TestUtils.constructorToString(c)), false);
                        failedToForm = true;
                    } else {

                        TestedMember annotation = (TestedMember) annot;

                        String annotName = annotation.name();
                        if (annotName.equals("")) {
                            annotName = "constructor";
                        }

                        constructorMap.put(annotName, new ConstructorTestCase(annotName, c, actualConstructor,
                                annotation.equality(), annotation.stringConverter()));
                    }
                }
            }
        }

        for (Field f : fields) {
            for (Annotation annot : f.getAnnotations()) {

                if (annot instanceof TestedMember) {

                    Field actualField = null;
                    for (Field field : actualFields) {
                        if (TestUtils.fieldEquals(field, f)) {
                            actualField = field;
                            break;
                        }
                    }

                    if (actualField == null) {
                        setSingleMessageResult("Field not found.", String.format("Field expected: %s.",
                                TestUtils.fieldToString(f.getModifiers(), f.getType(), f.getName())), false);
                        failedToForm = true;
                    } else {

                        TestedMember annotation = (TestedMember) annot;

                        String annotName = annotation.name();
                        if (annotName.equals("")) {
                            annotName = actualField.getName();
                        }

                        fieldMap.put(annotName,
                                new FieldTestCase(f, actualField, annotation.equality(), annotation.stringConverter()));
                    }
                }
            }
        }
    }

    protected boolean fieldsEqual(Object expected, Object actual) {
        try {
            for (FieldTestCase testCase : fieldMap.values()) {
                if (!testCase.fieldsEqual(expected, actual)) {
                    return false;
                }
            }
            return true;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(
                    "Failed when trying to test equality of fields in expected class and actual class: "
                            + e.getMessage());
        }
    }

    private String fieldsToString(Object obj, boolean expected, List<FieldTestCase> list) {
        StringBuilder builder = new StringBuilder();

        if (obj != null && obj.getClass().isArray()) {
            Object[] objArray = (Object[]) obj;
            builder.append("[");
            for (int i = 0; i < objArray.length; i++) {

                builder.append(fieldsToString(objArray[i], expected, list));

                if (i < objArray.length - 1) {
                    builder.append(", ");
                }
            }
            builder.append("]");

        } else {

            try {

                if (obj == null) {
                    builder.append("null");
                } else {
                    builder.append(className + "{");
                    for (int i = 0; i < list.size(); i++) {
                        Field field;
                        if (expected) {
                            field = list.get(i).field;
                        } else {
                            field = list.get(i).actualField;
                        }
                        builder.append(field.getName() + ": ");
                        builder.append(field.get(obj));

                        if (i < list.size() - 1) {
                            builder.append(", ");
                        }
                    }
                    builder.append("}");
                }

            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Unable to convert class's fields to string");
            }
        }

        return builder.toString();

    }

    private String[] fieldsToString(Object expected, Object actual) {

        List<FieldTestCase> list = new ArrayList<FieldTestCase>(fieldMap.values());

        try {
            return new String[] { fieldsToString(expected, true, list), fieldsToString(actual, false, list) };
        } catch (IllegalArgumentException e) {
            setSingleMessageResult("Class Field String conversion failed.",
                    "Failed when trying to convert class's fields to Strings.", false);
            return null;
        }
    }

    protected void startGroup(String label) {
        groups.add(new TestCaseGroup(label));
    }

    protected void addConstructor(String cName, String action, Object... args) {
        addConstructor(cName, action, args);
    }

    protected void addFormatConstructor(String cName, String action, String returnStoreName, Object... args) {
        int ind = action.indexOf("%s") + 2;
        String temp = action.substring(0, ind);
        temp = String.format(temp, returnStoreName);
        String temp2 = action.substring(ind);
        temp2 = String.format(temp2, args);
        addConstructor(cName, temp + temp2, returnStoreName, args);
    }

    protected void addConstructor(String cName, String action, String returnStoreName, Object... args) {

        final ConstructorTestCase testCase = constructorMap.get(cName);
        if (testCase == null) {
            throw new RuntimeException("Test Case not found in constructorCase map.");
        }
        groups.get(groups.size() - 1).addChunk((time) -> {
            return testCase.run(time, action, null, returnStoreName, testCase.toString, args);
        });
    }

    protected void addStaticChunk(String testName, String action, Object... args) {
        addChunk(testName, action, null, null, args);
    }

    protected void addStaticFormatChunk(String testName, String action, Object... args) {
        action = String.format(action, args);
        addChunk(testName, action, null, null, args);
    }

    protected void addChunk(String testName, String action, String objName, Object... args) {
        addChunk(testName, action, objName, null, args);
    }

    protected void addFormatChunk(String testName, String action, String objName, Object... args) {
        int ind = action.indexOf("%s") + 2;
        String temp = action.substring(0, ind);
        temp = String.format(temp, objName);
        String temp2 = action.substring(ind);
        temp2 = String.format(temp2, args);
        addChunk(testName, temp + temp2, objName, null, args);
    }

    private void addChunk(String testName, String action, String objName, String returnStoreName, Object... args) {

        final MethodTestCase testCase = methodMap.get(testName);
        if (testCase == null) {
            throw new RuntimeException("Test Case not found in method map.");
        }
        groups.get(groups.size() - 1).addChunk((time) -> {
            return testCase.run(time, action, objName, returnStoreName, testCase.toString, args);
        });
    }

    protected void addFieldTest(String objName, String... fieldNames) {
        Arrays.stream(fieldNames).map(fieldMap::get).forEach(testCase -> {
            if (testCase == null) {
                throw new RuntimeException("Test Case not found in field map.");
            }
            groups.get(groups.size() - 1).addChunk((time) -> {
                return testCase.run(time, objName);
            });
        });
    }

    protected void addAction(String str, Object... args) {
        groups.get(groups.size() - 1).addChunk((time) -> {
            actions.add(String.format(str, args));
            return Optional.empty();
        });
    }

    protected Object getTestVariable(String key) {
        return testVariables.get(key);
    }

    protected void runTests(long limit) {

        final AtomicInteger numTest = new AtomicInteger();

        for (int i = 0; i < groups.size(); i++) {

            numTest.set(i);
            results.add(null);
            TestCaseGroup group = groups.get(i);

            group.startTime = System.currentTimeMillis();

            AtomicBoolean testFinished = new AtomicBoolean(false);

            Runnable testRunner = new Runnable() {
                public void run() {

                    Exception exception = null;
                    try {
                        // Call the method that gives actual results
                        for (Invoker invoker : group.chunks) {
                            Optional<TestResult> res = invoker.get(group.startTime);
                            if (res.isPresent()) {
                                results.set(numTest.get(), res.get());
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        if (e.getCause() instanceof Exception) {
                            exception = (Exception) e.getCause();
                        } else if (e.getCause() instanceof ThreadDeath) {
                            // We timed out
                            return;
                        } else {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        exception = e;
                    }

                    if (exception != null) {

                        // Get the exceptions Stack Trace as a String
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        exception.printStackTrace(pw);
                        String stackTrace = sw.toString();

                        String consoleOut = getStandarOut();

                        results.set(numTest.get(), new MessageTestResult(actionsToString(),
                                System.currentTimeMillis() - group.startTime, stackTrace, consoleOut));
                    }

                    testFinished.set(true);
                }
            };

            Thread t = new Thread(testRunner);
            t.start();

            try {
                t.join(limit);
                t.stop();

                // Timed out
                if (!testFinished.get()) {
                    String consoleOut = getStandarOut();
                    clearStandardOut();
                    results.set(numTest.get(), new MessageTestResult("A timeout occurred", actionsToString(), false,
                            System.currentTimeMillis() - group.startTime, consoleOut));

                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (results.get(numTest.get()) == null) {
                String consoleOut = getStandarOut();
                results.set(numTest.get(), new MessageTestResult(group.label, actionsToString(), true,
                        System.currentTimeMillis() - group.startTime, consoleOut));
            }

            actions.clear();
        }

        resultHandler.accept(results);
    }

    protected List<String> getActions() {
        return actions;
    }

    protected String actionsToString() {
        return actionsToString(actions.size());
    }

    protected String actionsToString(int numActions) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numActions; i++) {
            if (i < numActions - 1) {
                sb.append(actions.get(i) + "\n");
            } else {
                sb.append(actions.get(i));
            }
        }

        return sb.toString();
    }

    protected class TestCaseGroup {
        private List<Invoker> chunks = new ArrayList<Invoker>();
        private long startTime;
        private String label;

        public TestCaseGroup(String label) {
            this.label = label;
        }

        private void addChunk(Invoker chunk) {
            chunks.add(chunk);
        }
    }

    protected abstract class ClassTestCase {
        protected String label;
        protected EqualityTester equalityTester;
        protected Stringifier toString;

        public ClassTestCase(String label, EqualityTester equalityTester, Stringifier toString) {
            this.label = label;
            this.equalityTester = equalityTester;
            this.toString = toString;
        }

        public abstract Optional<TestResult> run(long time, String actionMessage, String objName,
                String returnStoreName, Stringifier toString, Object... args)
                throws IllegalAccessException, InvocationTargetException, InstantiationException;
    }

    protected class ConstructorTestCase extends ClassTestCase {
        private Constructor constructor;
        private Constructor actualConstructor;

        public ConstructorTestCase(String label, Constructor constructor, Constructor actualConstructor,
                EqualityTester equalityTester, Stringifier toString) {
            super(label, equalityTester, toString);
            this.constructor = constructor;
            this.actualConstructor = actualConstructor;
        }

        public Optional<TestResult> run(long time, String actionMessage, String objName, String returnStoreName,
                Stringifier toString, Object... args)
                throws IllegalAccessException, InvocationTargetException, InstantiationException {

            actions.add(actionMessage);

            Object expected = constructor.newInstance(args);
            Object actual = actualConstructor.newInstance(args);

            if (returnStoreName != null) {
                testVariables.put(returnStoreName, new Object[] { expected, actual });
            }

            boolean equals = equalityTester.test(expected, actual);

            if (equals) {
                return Optional.empty();
            } else {
                return Optional.of(new ComparativeMessageTestResult(toString.convert(expected),
                        toString.convert(actual), label, actionsToString(), false, System.currentTimeMillis() - time));
            }
        }
    }

    protected class MethodTestCase extends ClassTestCase {
        private Method method;
        private Method actualMethod;
        // -1 = not the class (some other object), 0 = class but not an array,
        // 1 = 1d array of class, 2 = 2d array of class ... n = nd array of class
        private int[] parameterClassArrayDepth;
        private Class<?>[] paramTypes;
        private Class<?>[] actualParamTypes;
        private boolean returnIsClass;

        public MethodTestCase(String label, Method method, Method actualMethod, EqualityTester equalityTester,
                Stringifier toString, int[] paramIsClass, boolean returnIsClass) {
            super(label, equalityTester, toString);
            this.method = method;
            this.actualMethod = actualMethod;
            this.returnIsClass = returnIsClass;

            paramTypes = method.getParameterTypes();
            actualParamTypes = actualMethod.getParameterTypes();
            parameterClassArrayDepth = new int[paramTypes.length];

            for (int i = 0; i < paramTypes.length; i++) {
                final int ii = i;
                if (IntStream.of(paramIsClass).anyMatch(x -> x == ii)) {
                    int expectedDimensionCount = 0;
                    Class<?> expectedArrayClass = paramTypes[i];
                    while (expectedArrayClass.isArray()) {
                        expectedDimensionCount++;
                        expectedArrayClass = expectedArrayClass.getComponentType();
                    }
                    parameterClassArrayDepth[i] = expectedDimensionCount;
                } else {
                    parameterClassArrayDepth[i] = -1;
                }
            }
        }

        public Optional<TestResult> run(long time, String actionMessage, String objName, String returnStoreName,
                Stringifier toString, Object... args)
                throws IllegalAccessException, InvocationTargetException, InstantiationException {

            actions.add(actionMessage);

            Object[] objs = testVariables.get(objName);
            Object expected;
            Object actual;
            Object[] expectedArgs = new Object[args.length];
            Object[] actualArgs = new Object[args.length];

            for (int i = 0; i < args.length; i++) {
                int arrDepth = parameterClassArrayDepth[i];
                if (arrDepth < 0) {
                    expectedArgs[i] = args[i];
                    actualArgs[i] = args[i];
                } else if (arrDepth == 0) {
                    Object[] temp = testVariables.get(args[i]);
                    expectedArgs[i] = temp[0];
                    actualArgs[i] = temp[1];
                } else {
                    try {

                        expectedArgs[i] = recursiveArrayFill(0, (Object[]) args[i], paramTypes[i].getComponentType());
                        actualArgs[i] = recursiveArrayFill(1, (Object[]) args[i],
                                actualParamTypes[i].getComponentType());
                    } catch (ClassCastException e) {
                        throw new RuntimeException(
                                "Got class cast exception trying to convert param: " + i + ". " + e.getMessage());
                    }
                }
            }

            if (objs != null) {
                expected = method.invoke(objs[0], expectedArgs);
                actual = actualMethod.invoke(objs[1], actualArgs);
            } else {
                expected = method.invoke(null, (Object[]) expectedArgs);
                actual = actualMethod.invoke(null, (Object[]) actualArgs);
            }

            if (returnStoreName != null) {
                testVariables.put(returnStoreName, new Object[] { expected, actual });
            }

            boolean equals = equalityTester.test(expected, actual);

            if (equals) {
                return Optional.empty();
            } else {
                String expectedString;
                String actualString;
                if (returnIsClass) {
                    String[] temp = fieldsToString(expected, actual);
                    expectedString = temp[0];
                    actualString = temp[1];
                } else {
                    expectedString = toString.convert(expected);
                    actualString = toString.convert(actual);
                }

                return Optional.of(new ComparativeMessageTestResult(expectedString, actualString, label,
                        actionsToString(), false, System.currentTimeMillis() - time));
            }
        }

        /*
            Args will look like {{"a1", "a2"}, {"a1", "a3", "a4"}} for 2 dimensions and testVariableIndex = 0
            return will look like 
            {{testVariables[a1][0], testVariables[a2][0]}, {testVariables[a1][0], testVariables[a3][0], testVariables[a4][0]}}
        */
        private Object[] recursiveArrayFill(int testVariableIndex, Object[] args, Class<?> arrType) {

            Object[] ret = (Object[]) Array.newInstance(arrType, args.length);

            for (int i = 0; i < args.length; i++) {
                if (!arrType.isArray()) {
                    ret[i] = testVariables.get(args[i])[testVariableIndex];
                } else {
                    ret = recursiveArrayFill(testVariableIndex, (Object[]) args[i], arrType.getComponentType());
                }
            }

            return ret;
        }
    }

    protected class FieldTestCase {
        private Field field;
        private Field actualField;
        private EqualityTester equalityTester;
        private Stringifier toString;

        public FieldTestCase(Field field, Field actualField, EqualityTester equalityTester, Stringifier toString) {
            this.field = field;
            this.actualField = actualField;
            this.equalityTester = equalityTester;
            this.toString = toString;

            field.setAccessible(true);
            actualField.setAccessible(true);
        }

        public boolean fieldsEqual(Object expected, Object actual) throws IllegalAccessException {
            return equalityTester.test(field.get(expected), actualField.get(actual));
        }

        public Optional<TestResult> run(long time, String objName) throws IllegalAccessException {
            Object[] objs = testVariables.get(objName);
            Object expectedObj = objs[0];
            Object actualObj = objs[1];

            if (!fieldsEqual(expectedObj, actualObj)) {
                String testingLabel = "Testing " + field.getName() + " field";
                actions.add("// " + testingLabel + " ... Set incorrectly.");
                return Optional.of(new ComparativeMessageTestResult(toString.convert(field.get(expectedObj)),
                        toString.convert(actualField.get(actualObj)), testingLabel, actionsToString(), false,
                        System.currentTimeMillis() - time));
            } else {
                return Optional.empty();
            }

        }
    }

    protected interface Invoker {
        Optional<TestResult> get(long time)
                throws IllegalAccessException, InvocationTargetException, InstantiationException;
    }
}