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
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

/**
 * This Tester is made for testing entire classes.
 * 
 * You provide an expected and actual version of the class. These two classes
 * can be tested with methods, field, and constructor based tests.
 * 
 * This class is usually extended by the specific Tester for a challenge.
 * 
 * For example: HouseClassBuildingTest extends ClassTester and holds the logic
 * for the House Class Building challenge
 * 
 * This class contains an Inner class that is used as an abstraction of a Test
 * group. A test group loosely correlates to a test case
 * 
 * This class also contains Inner classes made to be abstractions for Fields,
 * Constructors, and Methods that are testable
 * 
 * This class contains a Function Interface, Invoker, which exists as an
 * abstraction for a TestCase or a chunk of a TestCase
 */
public class ClassTester extends Tester {

    // Singleton
    protected static ClassTester tester;

    // The Class object for expected and actual versions of the tested Class
    protected Class<?> expected;
    protected Class<?> actual;

    // Holds all testable methods
    protected HashMap<String, TestableMethod> methodMap = new HashMap<String, TestableMethod>();

    // Holds all testable constructors
    protected HashMap<String, TestableConstructor> constructorMap = new HashMap<String, TestableConstructor>();

    // Holds all testable fields
    protected HashMap<String, TestableField> fieldMap = new HashMap<String, TestableField>();

    /*
     * Holds all of the variable created by the tests
     *
     * Maps variable name => [expected var, actual var]
     */
    protected final HashMap<String, Object[]> testVariables = new HashMap<String, Object[]>();

    /*
     * Holds each test group.
     * 
     * A test group is a group of chunks that make up one test case
     */
    private List<TestCaseGroup> groups = new ArrayList<TestCaseGroup>();

    // The string version of the actions(chunks) that are taking place
    protected List<String> actions = new ArrayList<String>();

    // Name of the class being tested
    private String className;

    /**
     * The constructor for ClassTester.
     * 
     * expectedClass and actualClass normally look something like:
     * "HouseClassBuildingTest$House" and "House"
     * 
     * expectedClass is in this case, an inner class of HouseClassBuildingTest named
     * House and actualClass is the name of the class that the student submitted
     * 
     * @param expectedClass the name of the expected class
     * @param actualClass   the name of the actual class
     */
    public ClassTester(String expectedClass, String actualClass) {

        if (failedToForm) {
            return;
        }

        // assign singleton
        tester = this;

        // Try to find the expected class
        try {
            expected = Class.forName(expectedClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Expected class not found.");
        }

        // Try to find the actual class
        try {
            actual = Class.forName(actualClass);
        } catch (ClassNotFoundException e) {
            setSingleMessageResult("Class misnamed.", "Class name expected: " + actualClass + ".", false);
            failedToForm = true;
            return;
        }

        /*
         * Get modifiers but ignore static.
         * 
         * We ignore static because more often than not, the expected class will be a
         * static inner class. The actual class will not.
         */
        int expectedModifiers = expected.getModifiers() & (~Modifier.STATIC);

        // Check to see if the actual class has the same modifiers as the expected class
        if (expectedModifiers != actual.getModifiers()) {
            setSingleMessageResult("Class not found.",
                    "Class expected: " + Modifier.toString(expectedModifiers) + " class " + actualClass + ".", false);
            failedToForm = true;
            return;
        }

        // Store class name
        className = actualClass;

        Method[] methods = expected.getDeclaredMethods();
        Method[] actualMethods = actual.getDeclaredMethods();
        Constructor[] constructors = expected.getDeclaredConstructors();
        Constructor[] actualConstructors = actual.getDeclaredConstructors();

        /**
         * We're looking for fields in tested class and the super classes of tested
         * class.
         */
        Field[] fields = expected.getDeclaredFields();
        Field[] actualFields = actual.getDeclaredFields();
        List<Field> parentFields = TestUtils.getAllSuperFields(expected);
        List<Field> actualAllFields = TestUtils.getAllSuperFields(expected);
        actualAllFields.addAll(Arrays.asList(actualFields));

        // Extract all methods annotated as being a TestedMember from expected class
        for (Method m : methods) {

            // Loop through each method's annotations
            for (Annotation annot : m.getAnnotations()) {

                // If the annotation is of type TestedMember
                if (annot instanceof TestedMember) {

                    TestedMember annotation = (TestedMember) annot;

                    /*
                     * Tested member paramIsClass and returnIsClass is set based on whether or not
                     * the parameters are the same type as the tested class.
                     * 
                     * See TestedMember for more detail
                     */
                    int[] paramIsClass = annotation.paramIsClass();
                    boolean returnIsClass = annotation.returnIsClass();

                    // Look for the actual method in actual class
                    Method actualMethod = null;
                    for (Method meth : actualMethods) {
                        if (TestUtils.methodEquals(m, meth, returnIsClass, paramIsClass, actual)) {
                            actualMethod = meth;
                            break;
                        }
                    }

                    // Check to make sure we found the actual method
                    if (actualMethod == null) {
                        setSingleMessageResult("Method not found.",
                                String.format("Method expected: %s.", TestUtils.methodToString(m.getModifiers(),
                                        m.getReturnType(), m.getName(), m.getParameterTypes())),
                                false);
                        failedToForm = true;
                        return;
                    } else {
                        // If we found it, add this method to the methodMap
                        String annotName = annotation.name();
                        if (annotName.equals("")) {
                            // Default annotName is just the name of the method
                            annotName = actualMethod.getName();
                        }

                        // Create TestableMethod and put it in the method map
                        methodMap.put(annotName, new TestableMethod(annotName, m, actualMethod, annotation.equality(),
                                annotation.stringConverter(), annotation.paramIsClass(), annotation.returnIsClass()));
                    }
                }
            }
        }

        // Extract constructors annotated as being a TestedMember from expected class
        for (Constructor c : constructors) {
            // Loop through all of the constructor's annotations
            for (Annotation annot : c.getAnnotations()) {

                // If the constructor is annotated as a TestedMember
                if (annot instanceof TestedMember) {

                    // Find the actualConstructor
                    Constructor actualConstructor = null;
                    for (Constructor con : actualConstructors) {
                        if (TestUtils.constructorEquals(con, c)) {
                            actualConstructor = con;
                            break;
                        }
                    }

                    // Check to make sure we found the actual constructor
                    if (actualConstructor == null) {
                        setSingleMessageResult("Constructor not found.",
                                String.format("Constructor expected: %s.", TestUtils.constructorToString(c)), false);
                        failedToForm = true;
                    } else {
                        // Add the constructor to the constructorMap
                        TestedMember annotation = (TestedMember) annot;

                        String annotName = annotation.name();
                        if (annotName.equals("")) {
                            // default annotName for constructors is just constructor
                            annotName = "constructor";
                        }

                        // Create a TestableConstructor and put it in the methodMap
                        constructorMap.put(annotName, new TestableConstructor(annotName, c, actualConstructor,
                                annotation.equality(), annotation.stringConverter()));
                    }
                }
            }
        }

        BiConsumer<Field, Boolean> fieldHandler = (f, isFromSuper) -> {
            // Loop through each of the field's annotations
            for (Annotation annot : f.getAnnotations()) {
                // If one of the annotations is TestedMember
                if (annot instanceof TestedMember) {
                    // Find the matching field from the actual class or super class of actual class
                    Field actualField = null;
                    for (Field field : actualAllFields) {
                        if (TestUtils.fieldEquals(field, f)) {
                            actualField = field;
                            break;
                        }
                    }

                    // Check to make sure we found the actual field
                    if (actualField == null) {
                        setSingleMessageResult("Field not found.", String.format("Field expected: %s.",
                                TestUtils.fieldToString(f.getModifiers(), f.getType(), f.getName())), false);
                        failedToForm = true;
                    } else {
                        // Add the field to the fieldMap
                        TestedMember annotation = (TestedMember) annot;

                        String annotName = annotation.name();
                        if (annotName.equals("")) {
                            // default annotName is the field's name
                            annotName = actualField.getName();
                        }

                        // Create a TestableField and add it to the field map
                        fieldMap.put(annotName, new TestableField(f, actualField, annotation.equality(),
                                annotation.stringConverter(), isFromSuper));
                    }
                }
            }
        };

        // Extract fields annotated as TestedMembers from expected class
        for (Field f : fields) {
            fieldHandler.accept(f, false);
        }

        /*
         * Extract fields annotated as TestedMembers from super classes to the expected
         * class
         */
        for (Field f : parentFields) {
            fieldHandler.accept(f, true);
        }
    }

    /**
     * Tests to see if submitted code has limit or fewer fields
     * 
     * If you're going to call this method it should be called before you check if
     * the tester has been formed.
     * 
     * @param limit how many fields student code is allowed to have.
     */
    public void setFieldLimit(int limit) {
        if (failedToForm) {
            return;
        }

        Field[] actualFields = actual.getDeclaredFields();
        if (actualFields.length > limit) {
            failedToForm = true;
            if (limit == 0) {
                setSingleMessageResult("Too many fields.", "You may not use any fields in this challenge.", false);
            } else if (limit == 1) {
                setSingleMessageResult("Too many fields.", "You may only use 1 field in this challenge.", false);
            } else {
                setSingleMessageResult("Too many fields.", "You may only use " + limit + " fields in this challenge.",
                        false);
            }

        }
    }

    /**
     * Tests all of the fields that were specified as TestedMembers if they are
     * equal between expected and actual
     * 
     * @param expected an instance of the expected class
     * @param actual   an instance of the actual class
     * @return true if all fields that were specified as TestedMembers are equal
     *         between expected and actual. Equality is defined by the
     *         EqualityTester specified in the TestedMember annotation
     */
    protected boolean fieldsEqual(Object expected, Object actual) {
        try {
            for (TestableField testCase : fieldMap.values()) {
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

    /**
     * This method is a helper method used to get a String version of the passed in
     * Object by looking at all the fields of the object.
     * 
     * It uses recursion to deal with multi-dimensional arrays
     * 
     * @param obj       The object to get the String version of
     * @param expected  true if the object is an instance of the expected class.
     *                  false if the object is an instance of the actual class.
     * @param fieldList A list of the TestableFields to use when turning this object
     *                  into a String
     */
    private String fieldsToString(Object obj, boolean expected, List<TestableField> fieldList) {
        StringBuilder builder = new StringBuilder();

        // If the object is an array (This is this recursive case)
        if (obj != null && obj.getClass().isArray()) {

            Object[] objArray = (Object[]) obj;
            builder.append("[");
            // Go through each element of the array and append it to the String.
            for (int i = 0; i < objArray.length; i++) {

                /*
                 * call fieldsToString with each element. If the element is an array,
                 * fieldsToString will deal with it.
                 */
                builder.append(fieldsToString(objArray[i], expected, fieldList));

                if (i < objArray.length - 1) {
                    builder.append(", ");
                }
            }
            builder.append("]");

        } else { // If the object is not an array or null (This is the base case)

            try {

                if (obj == null) {
                    // If the object is null
                    builder.append("null");
                } else {
                    // Append a string version of the field (name of field: value)
                    builder.append(className + "{");
                    for (int i = 0; i < fieldList.size(); i++) {
                        Field field;
                        if (expected) {
                            field = fieldList.get(i).field;
                        } else {
                            field = fieldList.get(i).actualField;
                        }
                        builder.append(field.getName() + ": ");
                        builder.append(field.get(obj));

                        if (i < fieldList.size() - 1) {
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

    /**
     * fieldsToString is a method that returns an array of Strings structured like
     * [expectedString, actualString] where expectedString is the String version of
     * the expected object and actualString is the String version of the actual
     * object
     * 
     * This method uses a helper method to do that
     * 
     * expected must be an instance of the expected class actual must be an instance
     * of the actual class
     * 
     * @param expected the expected object to convert to a String
     * @param actual   the actual object to convert to a String
     * @return an array of Strings structured like [expectedString, actualString]
     */
    private String[] fieldsToString(Object expected, Object actual) {

        List<TestableField> fieldList = new ArrayList<>(fieldMap.values());

        try {
            return new String[] { fieldsToString(expected, true, fieldList), fieldsToString(actual, false, fieldList) };
        } catch (IllegalArgumentException e) {
            setSingleMessageResult("Class Field String conversion failed.",
                    "Failed when trying to convert class's fields to Strings.", false);
            return null;
        }
    }

    /**
     * Starts a new testing group. Testing groups correspond to test cases.
     * 
     * @param label this is the label for the group. The label is a description
     *              about what the group is testing
     */
    protected void startGroup(String label) {
        groups.add(new TestCaseGroup(label));
    }

    /**
     * Adds a constructor chunk to the current group.
     * 
     * Action is modified using String.format() to insert returnStoreName and args
     * into the action
     * 
     * action = String.format(action, returnStoreName, args)
     * 
     * @param cName           name of the constructor (used for looking up in
     *                        constructorMap)
     * @param action          the string version of this chunk. See how it is
     *                        converted above
     * @param returnStoreName how the newly created object will be referenced in the
     *                        future.
     * @param args            the arguments to pass the constructor
     */
    protected void addFormatConstructor(String cName, String action, String returnStoreName, Object... args) {
        // Format action
        int ind = action.indexOf("%s") + 2;
        String temp = action.substring(0, ind);
        temp = String.format(temp, returnStoreName);
        String temp2 = action.substring(ind);
        temp2 = String.format(temp2, args);

        // Call addConstructor with formatted action
        addConstructor(cName, temp + temp2, returnStoreName, args);
    }

    /**
     * Adds a constructor chunk to the current group.
     * 
     * @param cName           name of the constructor (used for looking up in
     *                        constructorMap)
     * @param action          the string version of this chunk.
     * @param returnStoreName how the newly created object will be referenced in the
     *                        future.
     * @param args            the arguments to pass the constructor
     */
    protected void addConstructor(String cName, String action, String returnStoreName, Object... args) {
        // Find the testableConstructor
        final TestableConstructor testableConstructor = constructorMap.get(cName);
        if (testableConstructor == null) {
            throw new RuntimeException("Constructor was not found in constructorMap.");
        }

        // Add a test for the constructor to the current group
        groups.get(groups.size() - 1).addChunk((time) -> {
            return testableConstructor.run(time, action, null, returnStoreName, testableConstructor.toString, args);
        });
    }

    /**
     * Adds a call to a static method to the current group.
     * 
     * @param testName name of the static method (used for looking up in methodMap)
     * @param action   the string version of this chunk.
     * @param args     the arguments to pass the static method
     */
    protected void addStaticChunk(String testName, String action, Object... args) {
        addChunk(testName, action, null, null, args);
    }

    /**
     * Adds a call to a static method to the current group.
     * 
     * Action is modified using String.format() to insert args into the action
     * 
     * action = String.format(action, args)
     * 
     * @param testName name of the static method (used for looking up in methodMap)
     * @param action   the string version of this chunk.
     * @param args     the arguments to pass the static method
     */
    protected void addStaticFormatChunk(String testName, String action, Object... args) {
        action = String.format(action, args);
        addChunk(testName, action, null, null, args);
    }

    /**
     * Adds a call to a static method to the current group.
     * 
     * @param testName        name of the static method (used for looking up in
     *                        methodMap)
     * @param action          the string version of this chunk.
     * @param returnStoreName how the return from the method will be referenced in
     *                        the future.
     * @param args            the arguments to pass the static method
     */
    protected void addStaticChunk(String testName, String action, String returnStoreName, Object... args) {
        addChunk(testName, action, null, returnStoreName, args);
    }

    /**
     * Adds a call to a method to the current group.
     * 
     * @param testName name of the method (used for looking up in methodMap)
     * @param action   the string version of this chunk.
     * @param objName  name of the object to call the method on
     * @param args     the arguments to pass the method
     */
    protected void addChunk(String testName, String action, String objName, Object... args) {
        addChunk(testName, action, objName, null, args);
    }

    /**
     * Adds a call to a method to the current group.
     * 
     * Action is modified using String.format() to insert objName and args into the
     * action
     * 
     * action = String.format(action, objName, args)
     * 
     * @param testName name of the method (used for looking up in methodMap)
     * @param action   the string version of this chunk.
     * @param objName  name of the object to call the method on
     * @param args     the arguments to pass the method
     */
    protected void addFormatChunk(String testName, String action, String objName, Object... args) {
        // Format action
        int ind = action.indexOf("%s") + 2;
        String temp = action.substring(0, ind);
        temp = String.format(temp, objName);
        String temp2 = action.substring(ind);
        temp2 = String.format(temp2, args);

        // Call addChunk with the formatted action
        addChunk(testName, temp + temp2, objName, null, args);
    }

    /**
     * Adds a call to a method to the current group.
     * 
     * @param testName        name of the method (used for looking up in methodMap)
     * @param action          the string version of this chunk.
     * @param objName         name of the object to call the method on
     * @param returnStoreName how the return from the method will be referenced in
     *                        the future.
     * @param args            the arguments to pass the method
     */
    private void addChunk(String testName, String action, String objName, String returnStoreName, Object... args) {

        // Find the TestableMethod
        final TestableMethod testCase = methodMap.get(testName);
        if (testCase == null) {
            throw new RuntimeException("Test Case not found in method map.");
        }

        // Add a test for this method to the current group
        groups.get(groups.size() - 1).addChunk((time) -> {
            return testCase.run(time, action, objName, returnStoreName, testCase.toString, args);
        });
    }

    /**
     * Adds a test to the current group to see if the specified fields are equal
     * 
     * @param objName    name of the object to test
     * @param fieldNames name of the fields to test
     */
    protected void addFieldTest(String objName, String... fieldNames) {

        // Lol we were teaching streams when I wrote this code

        // Stream all of the field names and map them to the TestableField
        Arrays.stream(fieldNames).map(fieldMap::get).forEach(testCase -> {
            if (testCase == null) {
                throw new RuntimeException("Test Case not found in field map.");
            }

            /*
             * For each specified TestableField, add a test for their equality to the //
             * current chunk
             */
            groups.get(groups.size() - 1).addChunk((time) -> {
                return testCase.run(time, objName);
            });
        });
    }

    /**
     * Adds an action to the chunk. This can be used if you need more code to make
     * the group understandable to the Student
     * 
     * Action will be created using String.format()
     * 
     * action = String.format(str, args)
     * 
     * @param str  The format String that will be used to create the action String
     * @param args The arguments for String.format()
     */
    protected void addAction(String str, Object... args) {
        // Action must be added as part of the group so that it falls in the right spot
        groups.get(groups.size() - 1).addChunk((time) -> {
            actions.add(String.format(str, args));
            return Optional.empty();
        });
    }

    /**
     * Return a test variable from the testVariables map
     * 
     * @param key the name of the variable
     * @return an array formatted like [expected var, actual var] for the given
     *         variable name
     */
    protected Object[] getTestVariable(String key) {
        return testVariables.get(key);
    }

    /**
     * Run all of the groups added so far. All groups are run on a different thread
     * that can eventually timeout
     * 
     * @param limit the length of timeout in milliseconds
     */
    public void runTests(long limit) {
        // I'm sorry about this method...

        if (failedToForm) {
            throw new RuntimeException("Can't use a tester that didn't fully form.");
        }

        // Atomic Integer that stores which group is being tested
        final AtomicInteger numGroup = new AtomicInteger();

        // Loop through all of the groups
        for (int i = 0; i < groups.size(); i++) {

            numGroup.set(i);

            // Add another spot to the results but leave it empty for now
            results.add(null);

            // Get the test group we are about to run and its start time
            TestCaseGroup group = groups.get(i);
            group.startTime = System.currentTimeMillis();

            // Keep track of whether the test finished or it timed out
            AtomicBoolean testFinished = new AtomicBoolean(false);

            // Make a new Runnable that will run the test group
            Runnable testRunner = new Runnable() {
                public void run() {

                    // This will store an Exception if one occurs during executing the group
                    Exception exception = null;
                    try {
                        // Loop through all the chunks of this group
                        for (Invoker invoker : group.chunks) {
                            // Run the chunk
                            Optional<TestResult> res = invoker.get(group.startTime);

                            // If res is present that means the chunk failed
                            if (res.isPresent()) {
                                // Group failed so record the TestResult and stop testing the group
                                results.set(numGroup.get(), res.get());
                                break;
                            }
                        }
                    } catch (IllegalAccessException e) {
                        // Some error with the tester occurred. Programmer error
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        // Some error happened in the Runnable
                        if (e.getCause() instanceof Exception) {
                            /*
                             * Student caused so they failed and we should report the exception to the
                             * student
                             */
                            exception = (Exception) e.getCause();
                        } else if (e.getCause() instanceof ThreadDeath) {
                            // Student code timed out
                            return;
                        } else {
                            // Something else means that the Tester has a bug
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        // Some exception with student code
                        exception = e;
                    }

                    // If the student code caused an Exception
                    if (exception != null) {

                        // Get the exceptions Stack Trace as a String
                        // This crap is ugly but I couldn't find any other way of doing it
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        exception.printStackTrace(pw);
                        String stackTrace = sw.toString();

                        // Get any student print statements
                        String consoleOut = getStandardOut();

                        // Set the test result to one that reflects the exception
                        results.set(numGroup.get(), new MessageTestResult(actionsToString(),
                                System.currentTimeMillis() - group.startTime, stackTrace, consoleOut));
                    }

                    /*
                     * If code doesn't timeout and no exception occurred then the test is finished
                     */
                    testFinished.set(true);
                }
            };

            // Run the student code
            Thread t = new Thread(testRunner);
            t.start();

            try {
                // Wait for the student code thread to finish or timeout
                t.join(limit);

                /*
                 * This is dangerous... but stop the student code thread from running anymore.
                 * We must protect any shared resources from the student because we are stopping
                 * the thread
                 * 
                 * One example is System.out must be protected from student use
                 */
                t.stop();

                // Timed out
                if (!testFinished.get()) {
                    String consoleOut = getStandardOut();
                    clearStandardOut();
                    results.set(numGroup.get(), new MessageTestResult("A timeout occurred", actionsToString(), false,
                            System.currentTimeMillis() - group.startTime, consoleOut));

                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            /*
             * Tests finished so either a TestResult from failure is already present or the
             * tests passed
             */

            /*
             * If the result for this group is null then the tests all passed. Set the test
             * result accordingly
             */
            if (results.get(numGroup.get()) == null) {
                String consoleOut = getStandardOut();
                results.set(numGroup.get(), new MessageTestResult(group.label, actionsToString(), true,
                        System.currentTimeMillis() - group.startTime, consoleOut));
            }

            // Clear the actions list to get ready for the next group
            actions.clear();
        }

        // When all groups have been run, notify result handler
        resultHandler.accept(results);
    }

    /**
     * Returns all of the current actions
     * 
     * @return the actions
     */
    protected List<String> getActions() {
        return actions;
    }

    /**
     * Converts the actions list to a single String
     * 
     * @return the String representation of all of the actions
     */
    protected String actionsToString() {
        return actionsToString(actions.size());
    }

    /**
     * Returns the specified number of actions as a String
     * 
     * @param numActions number of actions to convert to a String
     * @return the String representation of the number of actions specified from the
     *         actions list
     */
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

    /**
     * This inner class represents a TestGroup which roughly corresponds to a single
     * test case
     * 
     * Test groups are made up of chunks which are the individual things to test on
     * in this group
     * 
     * Examples of chunks include: calling a method and seeing if it's return value
     * is correct, checking fields for the correct value, etc.
     */
    protected class TestCaseGroup {
        private List<Invoker> chunks = new ArrayList<Invoker>();
        private long startTime;

        // Label for what this TestCaseGroup is testing
        private String label;

        /**
         * Constructor for a TestCaseGroup
         * 
         * @param label the label for this test group
         */
        public TestCaseGroup(String label) {
            this.label = label;
        }

        /**
         * Adds a chunk to this group
         * 
         * @param chunk the chunk to add to the group
         */
        private void addChunk(Invoker chunk) {
            chunks.add(chunk);
        }
    }

    /**
     * An abstract class that represents Members of the class that can be tested.
     * 
     * Subclasses include TestableMethod, TestableConstructor
     */
    protected abstract class TestableMember {
        // Label describing this Testable member
        protected String label;

        /*
         * equalityTester is used to test equality between expected and actual versions
         * of this TestableMember
         */
        protected EqualityTester equalityTester;

        /*
         * toString it used to convert the value of the TestableMember to a String. In
         * the case of a Constructor or Method it converts the return value to a String
         */
        protected Stringifier toString;

        /**
         * Constructor for TestableMember
         * 
         * @param label          the label describing for this TestableMember
         * @param equalityTester used to test equality between expected and actual
         *                       versions of this TestedMember
         * @param toString       converts the value of the TestableMember to a String.
         *                       In the case of a Constructor or Method it converts the
         *                       return value to a String
         */
        public TestableMember(String label, EqualityTester equalityTester, Stringifier toString) {
            this.label = label;
            this.equalityTester = equalityTester;
            this.toString = toString;
        }

        /**
         * Tests the TestableMember returning an Optional with a TestResult if it fails
         * which details the failure or Optional.empty() if it passes
         * 
         * @param time            the length of the timeout
         * @param actionMessage   the String version of what is being tested
         * @param objName         the name of the variable to run this test on
         * @param returnStoreName whatever is returned from the running of this
         *                        TestableMember will be referenced in the future by
         *                        returnStoreName. In the case of Testing a method, the
         *                        return value will be referenced in the future by this
         *                        name.
         * @param toString        Used to convert the result of the running to a String.
         *                        Most of the time you should just pass in the
         *                        Stringifier object stored by this TestableMethod
         * @param args            the arguments to whatever TestableMember is being used
         * @return Either an empty Optional if the test passes or an Optional with a
         *         TestResult in it describing the failure
         */
        public abstract Optional<TestResult> run(long time, String actionMessage, String objName,
                String returnStoreName, Stringifier toString, Object... args)
                throws IllegalAccessException, InvocationTargetException, InstantiationException;
    }

    /**
     * This class represents a Constructor that can be tested
     */
    protected class TestableConstructor extends TestableMember {
        // Reference to the version of this constructor in the expected class
        private Constructor constructor;
        // Reference to the version of this constructor in the actual class
        private Constructor actualConstructor;

        /**
         * Constructor for TestableConstructor
         * 
         * @param label             the label for this TestableConstructor
         * @param constructor       the version of this constructor in the expected
         *                          class
         * @param actualConstructor the version of this constructor in the actual class
         * @param equalityTester    the way of testing the constructed objects for
         *                          Equality
         * @param toString          the way of converting the constructed objects to a
         *                          String
         */
        public TestableConstructor(String label, Constructor constructor, Constructor actualConstructor,
                EqualityTester equalityTester, Stringifier toString) {
            super(label, equalityTester, toString);
            this.constructor = constructor;
            this.actualConstructor = actualConstructor;
        }

        @Override
        public Optional<TestResult> run(long time, String actionMessage, String objName, String returnStoreName,
                Stringifier toString, Object... args)
                throws IllegalAccessException, InvocationTargetException, InstantiationException {

            // Add the action to actions
            actions.add(actionMessage);

            // Construct expected and actual
            Object expected = constructor.newInstance(args);
            Object actual = actualConstructor.newInstance(args);

            /*
             * If a returnStoreName is given then we need to store expected and actual for
             * later use
             */
            if (returnStoreName != null) {
                testVariables.put(returnStoreName, new Object[] { expected, actual });
            }

            // Test to see if expected and actual are equal
            boolean equals = equalityTester.test(expected, actual);

            if (equals) {
                // We passed!
                return Optional.empty();
            } else {
                // You failed
                return Optional.of(new ComparativeMessageTestResult(toString.convert(expected),
                        toString.convert(actual), label, actionsToString(), false, System.currentTimeMillis() - time));
            }
        }
    }

    /**
     * This class represents a Method that can be tested
     */
    protected class TestableMethod extends TestableMember {
        // The version of this method from the expected class
        private Method method;
        // The version of this method from the actial class
        private Method actualMethod;

        /*
         * parameterClassArrayDepth is used to tell whether or not a parameter to this
         * method is of the type of the tested class. When this is the case we need to
         * treat that parameter specially because it will be different for the expected
         * and actual class.
         * 
         * Also the parameter may be an array of type of this class. That is another
         * special case we must take account for.
         * 
         * The scheme for denoting whether a parameter is of the type of the tested
         * class is below
         * 
         * -1 = not the tested class (some other object), 0 = tested class but not an
         * array, 1 = 1d array of tested class, 2 = 2d array of tested class ... n = nd
         * array of tested class
         */
        private int[] parameterClassArrayDepth;

        // Type of the parameters to the expected version of this method
        private Class<?>[] paramTypes;

        // Type of the parameters to the actual version of this method
        private Class<?>[] actualParamTypes;

        // Whether or not the return type of this method is the tested class
        private boolean returnIsClass;

        /**
         * Constructor for TestableMethod
         * 
         * @param label          the label for this TestableMethod
         * 
         * @param method         the version of this method in the expected class
         * @param actualMethod   the version of this method in the actual class
         * @param equalityTester the way of testing the return of the method for
         *                       Equality
         * @param toString       the way of converting the return of the method to a
         *                       String
         * @param paramIsClass   an array of ints that tell you which parameters are of
         *                       type of the tested class (0 indexed). For example if
         *                       you have a method where the second and third parameters
         *                       are of the type of the tested class then paramIsClass
         *                       will look like {1, 2}
         * @param returnIsClass  whether or not the return type of this method is the
         *                       tested class
         */
        public TestableMethod(String label, Method method, Method actualMethod, EqualityTester equalityTester,
                Stringifier toString, int[] paramIsClass, boolean returnIsClass) {
            super(label, equalityTester, toString);
            this.method = method;
            this.actualMethod = actualMethod;
            this.returnIsClass = returnIsClass;

            // Get the types of the parameters of the expected method
            paramTypes = method.getParameterTypes();
            // Get the types of the parameters of the actual method
            actualParamTypes = actualMethod.getParameterTypes();

            /*
             * Find out the dimensions of any parameters that are arrays that are of the
             * tested class's type
             */
            parameterClassArrayDepth = new int[paramTypes.length];
            // For each parameter
            for (int i = 0; i < paramTypes.length; i++) {
                final int ii = i;
                /*
                 * If this parameter is denoted to be of type of the tested class by the
                 * paramIsClass array
                 */
                if (IntStream.of(paramIsClass).anyMatch(x -> x == ii)) {
                    // Find out how many dimensions this array is
                    int expectedDimensionCount = 0;
                    Class<?> expectedArrayClass = paramTypes[i];
                    while (expectedArrayClass.isArray()) {
                        expectedDimensionCount++;
                        expectedArrayClass = expectedArrayClass.getComponentType();
                    }
                    parameterClassArrayDepth[i] = expectedDimensionCount;
                } else {
                    // If the parameter is not of the type of the tested class
                    parameterClassArrayDepth[i] = -1;
                }
            }
        }

        @Override
        public Optional<TestResult> run(long time, String actionMessage, String objName, String returnStoreName,
                Stringifier toString, Object... args)
                throws IllegalAccessException, InvocationTargetException, InstantiationException {

            // Add the action
            actions.add(actionMessage);

            // Get the expected and actual object to call the method on
            Object[] objs = testVariables.get(objName);
            Object expected;
            Object actual;

            // These arrays will store the expected and actual versions of the arguments
            Object[] expectedArgs = new Object[args.length];
            Object[] actualArgs = new Object[args.length];

            // Loop through all of the args passed in
            for (int i = 0; i < args.length; i++) {
                int arrDepth = parameterClassArrayDepth[i];

                if (arrDepth < 0) {
                    // if arrDepth < 0 then the arg is not of the type of the tested class
                    expectedArgs[i] = args[i];
                    actualArgs[i] = args[i];
                } else if (arrDepth == 0) {
                    // the argument is of the type of the tested class so get it from testVariables
                    Object[] temp = testVariables.get(args[i]);
                    expectedArgs[i] = temp[0];
                    actualArgs[i] = temp[1];
                } else {
                    // We got an array of type of the tested class
                    try {
                        // Get the expected and actual arguments recursively creating the array
                        expectedArgs[i] = recursiveArrayFill(0, (Object[]) args[i], paramTypes[i].getComponentType());
                        actualArgs[i] = recursiveArrayFill(1, (Object[]) args[i],
                                actualParamTypes[i].getComponentType());
                    } catch (ClassCastException e) {
                        throw new RuntimeException(
                                "Got class cast exception trying to convert param: " + i + ". " + e.getMessage());
                    }
                }
            }

            // Call the method on the object with the given arguments
            if (objs != null) {
                // This method isn't static and we have an object to call it on
                expected = method.invoke(objs[0], expectedArgs);
                actual = actualMethod.invoke(objs[1], actualArgs);
            } else {
                // This method is static
                expected = method.invoke(null, (Object[]) expectedArgs);
                actual = actualMethod.invoke(null, (Object[]) actualArgs);
            }

            // Store the return if we are supposed to
            if (returnStoreName != null) {
                testVariables.put(returnStoreName, new Object[] { expected, actual });
            }

            // Figure out if the returns are equal or not
            boolean equals = equalityTester.test(expected, actual);

            if (equals) {
                // We passed the test!
                return Optional.empty();
            } else {
                // We failed the test so return an Optional with a TestResult
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

        /**
         * This method recursively creates an array with objects from previous tests
         * 
         * arrStructure will look something like {{"a1", "a2"}, {"a1", "a3", "a4"}} for
         * 2 dimension array
         * 
         * when testVariableIndex = 0, the expected version of the variable is returned
         * when testVariableIndex = 1, the actual version of the variable is returned
         * 
         * if testVariableIndex = 0 given the above arrStructure, return will look like:
         * 
         * {{testVariables[a1][0], testVariables[a2][0]}, {testVariables[a1][0],
         * testVariables[a3][0], testVariables[a4][0]}}
         * 
         * @param testVariableIndex used to determine whether expected or actual
         *                          variables are being used
         * @param arrStructure      gives the structure of the array to be created
         * @param arrType           the type of the array to create
         */
        private Object[] recursiveArrayFill(int testVariableIndex, Object[] arrStructure, Class<?> arrType) {

            // Create the array to return
            Object[] ret = (Object[]) Array.newInstance(arrType, arrStructure.length);

            // Iterate through each index of the array
            for (int i = 0; i < arrStructure.length; i++) {
                if (!arrType.isArray()) {
                    // Base case. We reached the last level of the multi-dimensional array
                    ret[i] = testVariables.get(arrStructure[i])[testVariableIndex];
                } else {
                    // We are not at the last level of the multi-dimensional array
                    ret[i] = recursiveArrayFill(testVariableIndex, (Object[]) arrStructure[i],
                            arrType.getComponentType());
                }
            }

            return ret;
        }
    }

    /**
     * This class represents a Field that can be tested
     * 
     * Be very careful when testing fields that are of the type of the tested class
     */
    protected class TestableField {
        // Expected and actual versions of the field
        private Field field;
        private Field actualField;

        // Whether or not the field is from a super class of the tested class
        private boolean isFromSuper;

        // Tests the equality of the values of the field
        private EqualityTester equalityTester;

        // Converts the value of the field to a String
        private Stringifier toString;

        /**
         * Constructor for TestableField
         * 
         * @param field          the expected version of this field
         * @param actualField    the actual version of this field
         * @param equalityTester tests the equality of the values of this field
         * @param toString       converts the value of this field to a String
         */
        public TestableField(Field field, Field actualField, EqualityTester equalityTester, Stringifier toString,
                boolean isFromSuper) {
            this.field = field;
            this.actualField = actualField;
            this.equalityTester = equalityTester;
            this.toString = toString;
            this.isFromSuper = isFromSuper;

            /*
             * The following must be done so that we can reflectively set and get the value
             * of the field
             */
            field.setAccessible(true);
            actualField.setAccessible(true);
        }

        /**
         * Tests if the value of the expected and actual version of this field are equal
         * 
         * @param expected the expected object to test this field's value of
         * @param actual   the actual object to test this field's value of
         * @return whether or not this field's values in the expected object and actual
         *         object are equal
         */
        public boolean fieldsEqual(Object expected, Object actual) throws IllegalAccessException {
            return equalityTester.test(field.get(expected), actualField.get(actual));
        }

        /**
         * Runs a test to see if the field's values in the expected object and actual
         * object are equal
         * 
         * @param time    the timeout
         * @param objName the name of the object to test if this field is equal on
         * @return Either an empty Optional if the test passes or an Optional with a
         *         TestResult in it describing the failure
         */
        public Optional<TestResult> run(long time, String objName) throws IllegalAccessException {
            // Retrieve the expected and actual version of the object
            Object[] objs = testVariables.get(objName);
            Object expectedObj = objs[0];
            Object actualObj = objs[1];

            // If they aren't equal, return a TestResult saying so
            if (!fieldsEqual(expectedObj, actualObj)) {
                String testingLabel = "Testing " + field.getName() + " field";
                if (isFromSuper) {
                    testingLabel += " from super class";
                }

                // Make the action look like a comment
                actions.add("// " + testingLabel + " ... Set incorrectly.");
                return Optional.of(new ComparativeMessageTestResult(toString.convert(field.get(expectedObj)),
                        toString.convert(actualField.get(actualObj)), testingLabel, actionsToString(), false,
                        System.currentTimeMillis() - time));
            } else {
                // Successful test
                return Optional.empty();
            }

        }
    }

    /**
     * A functional interface used to make up the chunks of a testing group
     */
    protected interface Invoker {
        /**
         * Run the test.
         * 
         * @param time the timeout
         * @return Either an empty Optional if the test passes or an Optional with a
         *         TestResult in it describing the failure
         */
        Optional<TestResult> get(long time)
                throws IllegalAccessException, InvocationTargetException, InstantiationException;
    }
}