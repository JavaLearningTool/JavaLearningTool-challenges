import java.io.File;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This Tester is used to parse students' code for various requirements and restrictions.
 * Examples of this are:
 *  - limits on the use of certain code constructs (like if/else blocks or lambdas)
 *  - checks that a certain method is called
 */
public class ParseTester extends Tester {
    
    /**
     * Compilation Unit for the file being tested
     */
    private CompilationUnit testedFile;

    /**
     * @param fileName the name of the java file to examine
     */
    public ParseTester(String fileName) {
        try {
            // Load in java file to test
            testedFile = JavaParser.parse(new File(fileName));
        } catch(FileNotFoundException fnfe) {
            failedToForm = true;
            setSingleMessageResult("File misnamed!", "Looking for " + fileName + " but did not find it.", false);
        } catch (ParseProblemException ppe) {
            failedToForm = true;
            setSingleMessageResult("Problem parsing file!", "Failed to parse " + fileName + ". Contact administrator!", false);            
        }
    }
    
    /**
     * Checks if the tester properly formed. If not, throws RuntimeException
     */
    private void checkFormed() {
        if (failedToForm) {
            throw new RuntimeException("Can't use a tester that didn't fully form.");
        }
    }

    /**
     * Visits a method in the file, applies a methodVisitor to it.
     *
     * @param methodName the name of the method to visit
     * @param methodVisitor the VoidVisitorAdapter that will be applied to the method once found.
     *        It will traverse the body of the method.
     */
    private void visitMethod(String methodName, VoidVisitorAdapter<Void> methodVisitor) {
        checkFormed(); 

        // Visit all methods in the file
        testedFile.accept(new VoidVisitorAdapter<Void>(){
            public void visit(MethodDeclaration method, Void arg) {
                // If the name of the method matches, visit it
                if (method.getName().asString().equals(methodName)) {
                    method.accept(methodVisitor, arg);

                    super.visit(method, arg);
                }
            }
        }, null);
    }

    /**
     * Requires that a variable has been declared in a given method
     *
     * @param methodName name of the method to check in.
     * @param variableType type of the variable to look for
     * @param variableName name of the variable to look for
     */
    public void requireVariableDeclaration(String methodName, String variableType, String variableName) {
        checkFormed();        

        final String format = "%s variable declaration: `%s %s` in method named %s.";
        final AtomicBoolean foundDeclaration = new AtomicBoolean(false);

        // Visit all Variable declaration in the specified method
        visitMethod(methodName, new VoidVisitorAdapter<Void>() {
            public void visit(VariableDeclarator declaration, Void arg) {
                // If declaration matches parameters, we've found a declaration
                if (declaration.getName().asString().equals(variableName)
                    && declaration.getType().asString().equals(variableType)) {

                    foundDeclaration.set(true);
                }

                super.visit(declaration, arg);
            }
        });

        // Set test result based on if we found a matching declaration
        if (foundDeclaration.get()) {
            results.add(new MessageTestResult("Variable Declaration", String.format(format, "Found", variableType, variableName, methodName), true, 0));
        } else {
            results.add(new MessageTestResult("Variable Declaration", String.format(format, "Missing", variableType, variableName, methodName), false, 0));
        }
    }
}