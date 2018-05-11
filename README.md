# JavaLearningTool challenges

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

### Creation

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

### Creation

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

### Creation

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
