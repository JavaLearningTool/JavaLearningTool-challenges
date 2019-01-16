JavaLearningTool-challenges

# Table of contents

- [Table of contents](#table-of-contents)
- [Getting Started](#getting-started)
  - [File Structure](#file-structure)
  - [Creating your first challenge -- HelloJava](#creating-your-first-challenge----hellojava)
    - [Coding](#coding)
    - [Testing our new challenge](#testing-our-new-challenge)
    - [Adding More](#adding-more)
  - [Creating a FunctionReturnTester -- GridFinder](#creating-a-functionreturntester----gridfinder)
    - [Coding](#coding-1)
    - [Testing our new challenge](#testing-our-new-challenge-1)
- [Detailed Documentation on important classes and concepts](#detailed-documentation-on-important-classes-and-concepts)
  - [Tester](#tester)
    - [Tester output](#tester-output)
    - [Standard Out](#standard-out)
    - [Tester Formation](#tester-formation)
    - [Test Results](#test-results)
    - [Result Handler](#result-handler)
  - [MethodTester](#methodtester)
    - [Equality Tester](#equality-tester)
      - [Loose Double Equality](#loose-double-equality)
      - [Loose String Equality](#loose-string-equality)
  - [CommandLineStandardOutTester](#commandlinestandardouttester)
    - [Creating CommandLineStandardOutTesters](#creating-commandlinestandardouttesters)
    - [Common Uses](#common-uses)
    - [Example](#example)
  - [FunctionReturnTester](#functionreturntester)
    - [To String Converters](#to-string-converters)
      - [inputToStringConverter](#inputtostringconverter)
      - [outputToStringConverter](#outputtostringconverter)
    - [methodInvoker](#methodinvoker)
    - [Creating FunctionReturnTesters](#creating-functionreturntesters)
    - [Example](#example-1)
  - [ClassTester](#classtester)
    - [EqualityTester](#equalitytester)
    - [Stringifier](#stringifier)
    - [TestedMember](#testedmember)
    - [Creating ClassTesters](#creating-classtesters)
    - [Example](#example-2)
  - [Vocabulary](#vocabulary)

# Getting Started

## File Structure

```
JavaLearningTool-challenges
| tester_lib: gradle library with util classes like the different testers are stored in this folder
|
| shared: folder with classes that are given to the Students to be used in the challenges are stored here
|
| ArrayAverage
| | ArrayAverageTest.java
|
| BirthdayMessage
| | BirthdayMessageTest.java
|
| etc.

All other challenges are also stored in their own folder like above
```

## Creating your first challenge -- HelloJava

### Coding

HelloJava challenge will have students write a main method in the class Test.java that prints out Hello {input} where input is the first command line argument. We will use the [CommandLineStandardOutTester](#commandlinestandardouttester) to create this challenge.

1.  First create a folder to hold your Tester. The folder should have the same name as the challenge so we will name it HelloJava.
2.  Create a file inside of that folder named HelloJavaTest.java which will hold the code for the Tester. Tester file must be named "{ChallengeName}Test.java."
3.  Write the approved method:

```Java
public class HelloJavaTest {
    public static void approved(String[] args) {
        // In this test Students should print out Hello {args[0]}
        System.out.println("Hello " + args[0]);
    }
}
```

The approved method is the expected solution to the challenge.

4.  Create the main method inside HelloJavaTest.
5.  Create an instance of [CommandLineStandardOutTester](#commandlinestandardouttester) inside the main method. Provide the constructor a method reference to the expected (approved) method and the name of the class that will hold the actual method. I decided that the Students should make their main method in a class called Test:

```Java
public class HelloJavaTest {
    public static void main(String[] args) {
        CommandLineStandardOutTester tester = new CommandLineStandardOutTester(HelloJavaTest::approved, "Test");
    }

    public static void approved(String[] args) {
        // In this test Students should print out Hello {args[0]}
        System.out.println("Hello " + args[0]);
    }
}
```

6.  Check and make sure the [tester formed properly](#tester-formation)

```Java
public class HelloJavaTest {
    public static void main(String[] args) {
        CommandLineStandardOutTester tester = new CommandLineStandardOutTester(HelloJavaTest::approved, "Test");

        if (!tester.didForm()) {
            // If tester does not form, print out whatever caused it to fail
            tester.printResults();
            return;
        }
    }

    public static void approved(String[] args) {
        // In this test Students should print out Hello {args[0]}
        System.out.println("Hello " + args[0]);
    }
}
```

7.  Add test cases to the tester by calling addArgs. The input to addArgs should be an array of Strings which are the command line args.

```Java
public class HelloJavaTest {
    public static void main(String[] args) {
        CommandLineStandardOutTester tester = new CommandLineStandardOutTester(HelloJavaTest::approved, "Test");

        if (!tester.didForm()) {
            // If tester does not form, print out whatever caused it to fail
            tester.printResults();
            return;
        }

        // Test case 1
        tester.addArgs(new String[]{"World"});
        // Test case 2
        tester.addArgs(new String[]{"Java"});
    }

    public static void approved(String[] args) {
        // In this test Students should print out Hello {args[0]}
        System.out.println("Hello " + args[0]);
    }
}
```

*   Each test case is like another call to the main method of the Student's code.
*   One passes an array of Strings into addArgs where each String is a command line argument.

8.  Call runTests to run all test cases.

```Java
public class HelloJavaTest {
    public static void main(String[] args) {
        CommandLineStandardOutTester tester = new CommandLineStandardOutTester(HelloJavaTest::approved, "Test");

        if (!tester.didForm()) {
            // If tester does not form, print out whatever caused it to fail
            tester.printResults();
            return;
        }

        // Test case 1
        tester.addArgs(new String[]{"World"});
        // Test case 2
        tester.addArgs(new String[]{"Java"});

        tester.runTests();
    }

    public static void approved(String[] args) {
        // In this test Students should print out Hello {args[0]}
        System.out.println("Hello " + args[0]);
    }
}
```

### Testing our new challenge

1.  Create a new file in the same folder as the challenge tester that contains sample Student code. It will be called Test.java. Put into Test.java some sample code that a Student might write to try and solve the challenge.

**Don't commit any files that are being used solely for testing!**

2.  Open HelloJava folder and run the below commands and see if the output is as expected (It should be json detailing the results of the test case)

```
javac -cp .:../tester_lib-all/build/libs/tester_lib-all.jar:../shared *.java
java -cp .:../tester_lib-all/build/libs/tester_lib-all.jar:../shared HelloJavaTest
```

Note that the output will look a little strange. It should be a json array.

### Adding More

You've decided that you want students to print out Hello {input1} {input2} now (where input1 and input2 are the first and second command line arg).

Lets see what changes we have to make to the tester to accommodate this.

1.  We need to update the approved method so that it properly uses the first two commnand line args.

```Java
public class HelloJavaTest {
    public static void main(String[] args) {
        CommandLineStandardOutTester tester = new CommandLineStandardOutTester(HelloJavaTest::approved, "Test");

        if (!tester.didForm()) {
            // If tester does not form, print out whatever caused it to fail
            tester.printResults();
            return;
        }

        // Test case 1
        tester.addArgs(new String[]{"World"});
        // Test case 2
        tester.addArgs(new String[]{"Java"});

        tester.runTests();
    }

    public static void approved(String[] args) {
        // In this test Students should print out Hello {args[0]}
        System.out.println("Hello " + args[0] + " " + args[1]);
    }
}
```

2.  We need to update the calls to tester.addArgs because we have to pass in two command line args now instead of one command line arg. Just for fun we'll add two more test cases too although this is not necessary.

```Java
public class HelloJavaTest {
    public static void main(String[] args) {
        CommandLineStandardOutTester tester = new CommandLineStandardOutTester(HelloJavaTest::approved, "Test");

        if (!tester.didForm()) {
            // If tester does not form, print out whatever caused it to fail
            tester.printResults();
            return;
        }

        // Test case 1
        tester.addArgs(new String[]{"Sweet", "World"});
        // Test case 2
        tester.addArgs(new String[]{"Java", "10"});
        // Test case 3
        tester.addArgs(new String[]{"Fat", "Cat"});
        // Test case 4
        tester.addArgs(new String[]{"TestCase", "4"});

        tester.runTests();
    }

    public static void approved(String[] args) {
        // In this test Students should print out Hello {args[0]}
        System.out.println("Hello " + args[0] + " " + args[1]);
    }
}
```

## Creating a FunctionReturnTester -- GridFinder

### Coding

GridFinder challenge will have students write a method named findNumInGrid in the class Test.java that returns the number of occurrences of a specified int in a 2D array of ints. We will use the [FunctionReturnTester](#functionreturntester) to create this challenge.

1.  First create a folder to hold your Tester. The folder should have the same name as the challenge so we will name it GridFinder.
2.  Create a file inside of that folder named GridFinderTest.java which will hold the code for the Tester. (Tester file must be named "{ChallengeName}Test.java)."
3.  Write the approved method:

    The approved method will be used to get the expected output from the method being tested based on the input. In our case the input will be an int[][] called grid and an int called seek. Parameters are always passed to the approved method as an Object[] where each index in the array is an argument to the method being tested. We only care about the first two indexes because we have only two arguments. We will have to cast the first index of the Object[] to an int[][] (grid) and the second to an Integer (seek).

```Java
public class GridFinderTest {

    public static int approved(Object[] args) {

        // Get parameters out of Object[]
        int[][] grid = (int[][]) args[0];

        // Must cast as Integer because you can't cast an Object to an int
        int seek = (Integer) args[1];

        // Logic for accomplishing challenge
        int count = 0;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == seek) {
                    count++;
                }
            }
        }

        return count;
    }
}
```

The approved method is the expected solution to the challenge.

4.  Create the main method inside GridFinderTest.
5.  Create an instance of [FuntionReturnTester](#functionreturntester) inside the main method:

    FunctionReturnTester is a generic class. You give it the type of what the tested method should return. findNumInGrid will return an int so we give the FunctionReturnTester the type of Integer.

    `FunctionReturnTester<Integer> tester`

    Provide the constructor a method reference to the expected (approved) method, the name of the class that will hold the actual method, the name of the actual method, the return type and finally the parameter types. I decided that the Students should make their findNumInGrid method in a class called Test.

```Java
public class GridFinderTest {

    public static void main(String[] args) {

        // Make tester for this Challenge
        FunctionReturnTester<Integer> tester = new FunctionReturnTester<>(GridFinderTest::approved, // Expected method
                "Test", // Name of class
                "findNumInGrid", // Name of method being tested
                int.class, // Return type
                int[][].class, // Type of first parameter
                int.class // Type of second parameter
        );
    }

    public static int approved(Object[] args) {

        // Get parameters out of Object[]
        int[][] grid = (int[][]) args[0];

        // Must cast as Integer because you can't cast an Object to an int
        int seek = (Integer) args[1];

        // Logic for accomplishing challenge
        int count = 0;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == seek) {
                    count++;
                }
            }
        }

        return count;
    }
}
```

6.  If you want the students to write a non-static method, in this case I do, then call noArgsConstructor method on the tester to make an instance of the class to test on.

```Java
import java.util.Arrays;

public class GridFinderTest {

    public static void main(String[] args) {

        // Make tester for this Challenge
        FunctionReturnTester<Integer> tester = new FunctionReturnTester<>(GridFinderTest::approved, // Expected method
                "Test", // Name of class
                "findNumInGrid", // Name of method being tested
                int.class, // Return type
                int[][].class, // Type of first parameter
                int.class // Type of second parameter
        );
        tester.noArgsConstructor();
    }

    public static int approved(Object[] args) {

        // Get parameters out of Object[]
        int[][] grid = (int[][]) args[0];

        // Must cast as Integer because you can't cast an Object to an int
        int seek = (Integer) args[1];

        // Logic for accomplishing challenge
        int count = 0;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == seek) {
                    count++;
                }
            }
        }

        return count;
    }
}
```

7.  Add code that checks to make sure the [tester formed properly](#tester-formation).

```Java
import java.util.Arrays;

public class GridFinderTest {

    public static void main(String[] args) {

        // Make tester for this Challenge
        FunctionReturnTester<Integer> tester = new FunctionReturnTester<>(GridFinderTest::approved, // Expected method
                "Test", // Name of class
                "findNumInGrid", // Name of method being tested
                int.class, // Return type
                int[][].class, // Type of first parameter
                int.class // Type of second parameter
        );
        tester.noArgsConstructor();

        if (!tester.didForm()) {
            tester.printResults();
            return;
        }
    }

    public static int approved(Object[] args) {

        // Get parameters out of Object[]
        int[][] grid = (int[][]) args[0];

        // Must cast as Integer because you can't cast an Object to an int
        int seek = (Integer) args[1];

        // Logic for accomplishing challenge
        int count = 0;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == seek) {
                    count++;
                }
            }
        }

        return count;
    }
}
```

8.  Next we have to give the tester some basic information about how to accomplish the following tasks:

    *   Test equality between the return values of the expected and actual methods.
        ```Java
        // Use Integer's equal method to test return values from expected and actual class
        tester.setEqualityTester(Integer::equals);
        ```
    *   Convert the input of a test case to a String

        ```Java
        // Provide tester with a way to turn arguments into a String.
        tester.setInputToStringConverter((arg) -> {
            // Parameter one is the grid
            int[][] arg1 = (int[][]) arg[0];
            // Parameter two is the number to seek
            int arg2 = (Integer) arg[1];

            return "grid: " + Arrays.deepToString(arg1) + " seek: " + arg2;
        });
        ```

    *   Convert the output of a test case to a String.
        ```Java
        // Convert return value to a String
        tester.setOutputToStringConverter(out -> out + "");
        ```
    *   Call the method based on an array of the arguments

        ```Java
        // Show the tester how to parse args and call the actual version of the method
        tester.setMethodInvoker((obj, arg) -> {
            // Parameter one is the grid
            int[][] arg1 = (int[][]) arg[0];
            // Parameter two is the number to seek
            int arg2 = (Integer) arg[1];

            // tester.getMethod() will return t
            return tester.getMethod().invoke(obj, arg1, arg2);
        });
        ```

    All together:

    ```Java
    import java.util.Arrays;

    public class GridFinderTest {

        public static void main(String[] args) {

            // Make tester for this Challenge
            FunctionReturnTester<Integer> tester = new FunctionReturnTester<>(GridFinderTest::approved, // Expected method
                    "Test", // Name of class
                    "findNumInGrid", // Name of method being tested
                    int.class, // Return type
                    int[][].class, // Type of first parameter
                    int.class // Type of second parameter
            );
            tester.noArgsConstructor();

            if (!tester.didForm()) {
                tester.printResults();
                return;
            }

            // Use Integer's equals to test return values from expected and actual method
            tester.setEqualityTester(Integer::equals);

            // Provide tester with a way to turn arguments into a String
            tester.setInputToStringConverter((arg) -> {
                // Parameter one is the grid
                int[][] arg1 = (int[][]) arg[0];
                // Parameter two is the number to seek
                int arg2 = (Integer) arg[1];

                return "grid: " + Arrays.deepToString(arg1) + " seek: " + arg2;
            });

            // Convert return value to a String
            tester.setOutputToStringConverter(out -> out + "");

            // Show the tester how to parse args and call the actual version of the method
            tester.setMethodInvoker((obj, arg) -> {
                // Parameter one is the grid
                int[][] arg1 = (int[][]) arg[0];
                // Parameter two is the number to seek
                int arg2 = (Integer) arg[1];

                // tester.getMethod() will return the actual method
                return tester.getMethod().invoke(obj, arg1, arg2);
            });
        }

        public static int approved(Object[] args) {

            // Get parameters out of Object[]
            int[][] grid = (int[][]) args[0];

            // Must cast as Integer because you can't cast an Object to an int
            int seek = (Integer) args[1];

            // Logic for accomplishing challenge
            int count = 0;

            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    if (grid[i][j] == seek) {
                        count++;
                    }
                }
            }

            return count;
        }
    }
    ```

9 .  Add test cases to the tester by calling addArgs. The input to addArgs should be an array of Objects which are the parameters to the method.

    First we're gonna make some grids that we can reuse as arguments them we'll add the test cases.

```Java
import java.util.Arrays;

public class GridFinderTest {

    public static void main(String[] args) {

        // Make tester for this Challenge
        FunctionReturnTester<Integer> tester = new FunctionReturnTester<>(GridFinderTest::approved, // Expected method
                "Test", // Name of class
                "findNumInGrid", // Name of method being tested
                int.class, // Return type
                int[][].class, // Type of first parameter
                int.class // Type of second parameter
        );
        tester.noArgsConstructor();

        if (!tester.didForm()) {
            tester.printResults();
            return;
        }

        // Use Integer's equals to test return values from expected and actual method
        tester.setEqualityTester(Integer::equals);

        // Provide tester with a way to turn arguments into a String
        tester.setInputToStringConverter((arg) -> {
            // Parameter one is the grid
            int[][] arg1 = (int[][]) arg[0];
            // Parameter two is the number to seek
            int arg2 = (Integer) arg[1];

            return "grid: " + Arrays.deepToString(arg1) + " seek: " + arg2;
        });

        // Convert return value to a String
        tester.setOutputToStringConverter(out -> out + "");

        // Show the tester how to parse args and call the actual version of the method
        tester.setMethodInvoker((obj, arg) -> {
            // Parameter one is the grid
            int[][] arg1 = (int[][]) arg[0];
            // Parameter two is the number to seek
            int arg2 = (Integer) arg[1];

            // tester.getMethod() will return the actual method
            return tester.getMethod().invoke(obj, arg1, arg2);
        });

        // Even grid to use for testing
        int[][] evenArray = new int[][] { { 1, 2, 3, 4 }, { 5, 6, 7, 8 }, { 9, 1, 1, 3 } };
        // Jagged grid to use for testing
        int[][] jaggedArray = new int[][] { { 1, 2, 3, 4 }, { 5 }, { 9, 1, 1, 3, 5, 6 } };

        // test cases

        // Count the number of 1s in evenArray
        tester.addArgs(new Object[] { evenArray, 1 });

        // Count the number of 3s in evenArray
        tester.addArgs(new Object[] { evenArray, 3 });

        // Count the number of 12s in evenArray
        tester.addArgs(new Object[] { evenArray, 12 });

        // Count the number of 5s in jaggedArray
        tester.addArgs(new Object[] { jaggedArray, 5 });

        // Count the number of 4s in jaggedArray
        tester.addArgs(new Object[] { jaggedArray, 4 });
    }

    public static int approved(Object[] args) {

        // Get parameters out of Object[]
        int[][] grid = (int[][]) args[0];

        // Must cast as Integer because you can't cast an Object to an int
        int seek = (Integer) args[1];

        // Logic for accomplishing challenge
        int count = 0;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == seek) {
                    count++;
                }
            }
        }

        return count;
    }
}
```

*   Each test case is like another call to the main method of the Student's code.
*   One passes an array of Strings into addArgs where each String is a command line argument.

10. Call runTests to run all test cases.

```Java
import java.util.Arrays;

public class GridFinderTest {

    public static void main(String[] args) {

        // Make tester for this Challenge
        FunctionReturnTester<Integer> tester = new FunctionReturnTester<>(GridFinderTest::approved, // Expected method
                "Test", // Name of class
                "findNumInGrid", // Name of method being tested
                int.class, // Return type
                int[][].class, // Type of first parameter
                int.class // Type of second parameter
        );
        tester.noArgsConstructor();

        if (!tester.didForm()) {
            tester.printResults();
            return;
        }

        // Use Integer's equals to test return values from expected and actual method
        tester.setEqualityTester(Integer::equals);

        // Provide tester with a way to turn arguments into a String
        tester.setInputToStringConverter((arg) -> {
            // Parameter one is the grid
            int[][] arg1 = (int[][]) arg[0];
            // Parameter two is the number to seek
            int arg2 = (Integer) arg[1];

            return "grid: " + Arrays.deepToString(arg1) + " seek: " + arg2;
        });

        // Convert return value to a String
        tester.setOutputToStringConverter(out -> out + "");

        // Show the tester how to parse args and call the actual version of the method
        tester.setMethodInvoker((obj, arg) -> {
            // Parameter one is the grid
            int[][] arg1 = (int[][]) arg[0];
            // Parameter two is the number to seek
            int arg2 = (Integer) arg[1];

            // tester.getMethod() will return the actual method
            return tester.getMethod().invoke(obj, arg1, arg2);
        });

        // Even grid to use for testing
        int[][] evenArray = new int[][] { { 1, 2, 3, 4 }, { 5, 6, 7, 8 }, { 9, 1, 1, 3 } };
        // Jagged grid to use for testing
        int[][] jaggedArray = new int[][] { { 1, 2, 3, 4 }, { 5 }, { 9, 1, 1, 3, 5, 6 } };

        // test cases

        // Count the number of 1s in evenArray
        tester.addArgs(new Object[] { evenArray, 1 });

        // Count the number of 3s in evenArray
        tester.addArgs(new Object[] { evenArray, 3 });

        // Count the number of 12s in evenArray
        tester.addArgs(new Object[] { evenArray, 12 });

        // Count the number of 5s in jaggedArray
        tester.addArgs(new Object[] { jaggedArray, 5 });

        // Count the number of 4s in jaggedArray
        tester.addArgs(new Object[] { jaggedArray, 4 });

        tester.runTests();
    }

    public static int approved(Object[] args) {

        // Get parameters out of Object[]
        int[][] grid = (int[][]) args[0];

        // Must cast as Integer because you can't cast an Object to an int
        int seek = (Integer) args[1];

        // Logic for accomplishing challenge
        int count = 0;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == seek) {
                    count++;
                }
            }
        }

        return count;
    }
}
```

### Testing our new challenge

1.  Create a new file in the same folder as the challenge tester that contains sample Student code. It will be called Test.java. Put into Test.java some sample code that a Student might write to try and solve the challenge.

    Possible Student Code:

```Java
public class Test {
    public int findNumInGrid(int[][] grid, int seek) {
        int count = 0;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == seek) {
                    count++;
                }
            }
        }

        return count;
    }
}
```

**Don't commit any files that are being used solely for testing!**

1.  Open GridFinder folder and run the below commands and see if the output is as expected (It should be json detailing the results of the test case)

```
javac -cp .:../tester_lib-all/build/libs/tester_lib-all.jar:../shared *.java
java -cp .:../tester_lib-all/build/libs/tester_lib-all.jar:../shared GridFinderTest
```

Note that the output will look a little strange. It should be a json array.

# Detailed Documentation on important classes and concepts

## Tester

Tester is a class that helps to handle testing challenges. You will not use Tester but rather one of its subclasses.

### Tester output

**The only thing that should be printed to standard out should be valid JSON because that is what the compiler app expects!!!**

Tester has methods that handle printing out results as valid JSON. You should use those.

### Standard Out

Tester will capture and protect Standard out. This is done by storing System.out whenever the Tester is instantiated and setting System.out to be an alternative PrintStream so that whenever Student code calls System.out.println() our PrintSteam is edited instead of Standard out.

There are methods inside of Tester that interact with the alternative PrintStream.

**Because Standard out is captured by the Tester, you will be unable to do print statements by calling System.out.println()**

### Tester Formation

Different things can go wrong when creating a Tester. For example, if you expect the Student code to contain a certain method that isn't present then the Tester won't form correctly. When this happens the method `didForm()` will return false. Make sure to always check `didForm()` after instantiating a Tester.

If a Tester fails to form it should have one or more TestResults detailing why the Tester failed to form. The common way to handle a Tester that doesn't form properly is to call `tester.printResults()`. Be sure not to do anything else with the tester.

### Test Results

Each test case that is part of a Tester has its results stored in a TestResult object. TestResult will also hold other information about the test case and what happened. Tester stores a List of TestResults to store the results of each test case.

There are methods in Tester that handle retrieving TestResults and methods that handle printing the results to Standard out in the correct format.

### Result Handler

A Tester's resultHandler is called whenever a call to runTests has finished.

By default a Tester will print the jsonString of the test results whenever runTests is finished. If you wish to change this you can call `setResultHandler()`.

**As of right now runTests is synchronous but this might not always be the case so program as if it is asynchronous.**

## MethodTester

MethodTester is a type of Tester where one method will be called. You will only work with MethodTester's subclasses (examples: CommandLineStandardOutTester, FunctionReturnTester).

### Equality Tester

MethodTesters use a BiPredicate as a way of testing the equality between expected and actual version of the outputs from whatever method they are testing.

The equalityTester can be set by calling `setEqualityTester`

#### Loose Double Equality

Doubles and Floats can experience rounding errors. Because of this, the Student could write a solution that is different but equivalent to the expected solution and have different output because of rounding errors.

To combat this, when you have a MethodTester that is testing floating point numbers, you should use loose double equality. Basically this works by seeing how close the expected and actual answers are and if they are close enough the Student is told that they are correct.

MethodTester has a method called `useLooseDoubleEquality()` which will set the equalityTester to use loose double equality.

#### Loose String Equality

When working with, creating, or printing Strings it is easy to add or forget a space or new line character.

To combat this, when you have a MethodTester that is testing Strings, you can use loose string equality. This will Strip all spaces and new lines from the Strings you are testing.

MethodTester has a method called `useLooseStringEquality` which will set the equalityTester to use loose String equality.

## CommandLineStandardOutTester

This is a type of MethodTester. These are the simplest kind of tests. The student writes the main method. Input is supplied as Command Line args and the Student makes print statements based on that input. The print statements are tested for accuracy.

### Creating CommandLineStandardOutTesters

The creation of these tests generally follow these steps:

1.  Write a method that defines the correct functionality for the challenge (This method is by convention called approved).
2.  Write the main method which should
    1.  Create an instance of CommandLineStandardOutTester.
    2.  Check and make sure the tester formed properly
    3.  Add test cases to the tester by calling addArgs (optionally you can create an array of args for each test case and pass it in when calling runTests)
    4.  Call runTests

### Common Uses

*   The most common way to use these tests is to simple have the Student's write the main method and test their output. See HelloWorld
*   It is common to give the student some of the logic for parsing input as part of provided code (provided code is defined in the admin route of the website).

### Example

See HelloWorldTest.java

## FunctionReturnTester

This is a type of MethodTester. The student writes a method. Input is supplied as parameters to the method and the Student return something based on that input. The returned value is tested for accuracy.

### To String Converters

#### inputToStringConverter

This provides a way for a FunctionReturnTester to convert the input for a test case to a String

#### outputToStringConverter

This provides a way for a FunctionReturnTester to convert the output for a test case to a String

### methodInvoker

FunctionReturnTesters store the method to be tested as a Method object. methodInvoker is given an array of arguments and an object and calls the method to be tested on the given object with the given arguments.

Shortly: methodInvoker tells FunctionReturnTester how to call the method that is being tested.

### Creating FunctionReturnTesters

The creation of these tests generally follow these steps:

1.  Write a method that defines the correct functionality for the challenge (This method is by convention called approved).
2.  Write the main method which should
    1.  Create an instance of FunctionReturnTester
    2.  Check and make sure the tester formed properly
    3.  Set the tester's equalityTester
    4.  Set the tester's inputToStringConverter
    5.  Set the tester's outputToStringConverter
    6.  Set the tester's methodInvoker
    7.  Add test cases to the tester by calling addArgs (optionally you can create an array of args for each test case and pass it in when calling runTests)
    8.  Call runTests

### Example

See TheGreatestTest.java

## ClassTester

ClassTesters are used to create challenges that test an entire class, including multiple methods, constructors, and fields. For every challenge, you will extend ClassTester to create the tester for the challenge.

### EqualityTester

EqualityTester is an enum in the context of ClassTesters (unlike in the context of MethodTesters). Each instance of the enum is a way to tell if two objects are equal.

Most important EqualityTesters

*   `EqualityTester.NONE`: Objects are always equal. Basically saying ignore testing equality on this TestedMember.
*   `EqualityTester.OBJECT`: Use equals method on the Objects.
*   `EqualityTester.FLOATING_POINT`: Use loose floating point equality.
*   `EqualityTester.ARRAY`: Use Arrays.equals()
*   See EqualityTester.java for more or to add more.

### Stringifier

Stringifier is an enum used when making ClassTesters. Each instance of the enum is a way of converting an Object to a String.

Most important Stringifiers

*   `Stringifier.OBJECT`: use toString() method
*   `Stringifier.ARRAY`: use Arrays.toString() method
*   `Stringifier.DEEP_ARRAY` use Arrays.deepToString()
*   See Stringifer.java for more or to add more.

### TestedMember

TestedMember is an annotation that can be applied to members of the tested class. TestedMember consists of the following fields:

*   `name`: the name of the tested member. For fields and methods the default for name will be the name of the field or method. For constructors the default name is constructor.
*   `equality`: the EqualityTester to use on this tested member. Default is EqualityTester.NONE which would mean that the equality isn't tested for this TestedMember, we just need to be able to refer to it (This is useful for void methods).
*   `stringConverter`: the Stringifier for the TestedMember. This will be used for String conversions. Default is Stringifier.OBJECT which converts Objects to Strings using their toString method.
*   `paramIsClass` This field is only important if the TestedMember is a method. It is an array of ints that tell you which parameters are of type of the tested class (0 indexed). For example if you have a method where the second and third parameters are of the type of the tested class then paramIsClass will look like {1, 2}
*   `returnIsClass` This field is only important if the TestedMember is a method. It is whether or not the return type of the method is the tested class.

### Creating ClassTesters

1.  Make a class that extends ClassTester.
2.  Make an inner class that defines the correct functionality for the challenge.
3.  Annotate each member of that inner class that you wish to test with the TestedMember annotation.
4.  Write the main method which should create an instance of the class that extends ClassTester.
5.  Create a constructor for the class that extends ClassTester which:
    1.  Adds groups to the tester.
    2.  Calls runTests.

### Example

See PersonClassBuildingTest.java

## Vocabulary

*   **test case**: This is a loose term referring to different parts of a challenge that are being tested. Test cases are created and added to Testers in different ways for different types of Testers. Test cases can also be very simple or more complicated depending on the type of Tester.
