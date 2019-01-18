import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.Optional;

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
     * Requires that a variable has been declared in a given method. Creates a test
     * result for both the failure and success case.
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

    /**
     * Counts the number of if statements in the file.
     * 
     * The below code would return 2 if statements.
     * if (...) {
     * 
     * } else if (...) {
     * 
     * } else {
     * 
     * }
     */
    private int countIfStatements() {
        checkFormed(); 

        AtomicInteger numIfs = new AtomicInteger(0);


        testedFile.accept(new VoidVisitorAdapter<Void>() {
            public void visit(IfStmt ifStmt, Void arg) {
                numIfs.set(numIfs.get() + 1);
                
                // Check for else-if chain
                Optional<Statement> elseStatement = ifStmt.getElseStmt();
                boolean doneFlag = false;
                while (!doneFlag && elseStatement.isPresent()) {
                    if (elseStatement.get().isIfStmt()) {
                        numIfs.set(numIfs.get() + 1);
                        elseStatement = elseStatement.get().asIfStmt().getElseStmt();
                    } else {
                        doneFlag = true;
                    }
                }
            }
        }, null);

        return numIfs.get();
    }

    /**
     * Requires at least a certain number of if statements. Creates a test result
     * only for the failure case.
     * 
     * @param amount the minimum number of if statements allowed.
     */
    public void requireIfStatements(int amount) {
        checkFormed(); 

        int numIfs = countIfStatements();
        if (numIfs < amount) {
            results.add(new MessageTestResult("If statements required.", String.format("Expected at least %d if statements but found %d.", amount, numIfs), false, 0));
        }
    }

    /**
     * Requires at most a certain number of if statements. Creates a test result
     * only for the failure case.
     * 
     * @param amount the max number of if statements allowed.
     */
    public void restrictIfStatements(int amount) {
        checkFormed(); 

        int numIfs = countIfStatements();
        if (numIfs > amount) {
            results.add(new MessageTestResult("Too many if statements.", String.format("Expected at most %d if statements but found %d.", amount, numIfs), false, 0));
        }
    }

    /**
     * Counts the number of switch statements in the file.
     */
    private int countSwitchStatements() {
        checkFormed(); 

        AtomicInteger numSwitches = new AtomicInteger(0);

        testedFile.accept(new VoidVisitorAdapter<Void>() {
            public void visit(SwitchStmt switchStmt, Void arg) {
                numSwitches.set(numSwitches.get() + 1);
            }
        }, null);

        return numSwitches.get();
    }

    /**
     * Requires at least a certain number of switch statements. Creates a test result
     * only for the failure case.
     * 
     * @param amount the minimum number of switch statements allowed.
     */
    public void requireSwitchStatements(int amount) {
        checkFormed(); 

        int numSwitches = countSwitchStatements();
        if (numSwitches < amount) {
            results.add(new MessageTestResult("Switch statements required.", String.format("Expected at least %d switch statements but found %d.", amount, numSwitches), false, 0));
        }
    }

    /**
     * Requires at most a certain number of switch statements. Creates a test result
     * only for the failure case.
     * 
     * @param amount the max number of switch statements allowed.
     */
    public void restrictSwitchStatements(int amount) {
        checkFormed(); 

        int numSwitches = countSwitchStatements();
        if (numSwitches > amount) {
            results.add(new MessageTestResult("Too many switch statements.", String.format("Expected at most %d switch statements but found %d.", amount, numSwitches), false, 0));
        }
    }

    /**
     * Counts the number of switch entries in the file. (i.e. case/default)
     */
    private int countSwitchEntries() {
        checkFormed(); 

        AtomicInteger numSwitchEntries = new AtomicInteger(0);

        testedFile.accept(new VoidVisitorAdapter<Void>() {
            public void visit(SwitchEntryStmt switchEntryStmt, Void arg) {
                numSwitchEntries.set(numSwitchEntries.get() + 1);
            }
        }, null);

        return numSwitchEntries.get();
    }

    /**
     * Requires at least a certain number of switch entry statements (i.e. case/default). Creates a test result
     * only for the failure case.
     * 
     * @param amount the minimum number of switch entry statements allowed.
     */
    public void requireSwitchEntries(int amount) {
        checkFormed(); 

        int numSwitchEntries = countSwitchEntries();
        if (numSwitchEntries < amount) {
            results.add(new MessageTestResult("Switch entries required.", String.format("Expected at least %d switch entries (i.e. case/default) but found %d.", amount, numSwitchEntries), false, 0));
        }
    }

    /**
     * Requires at most a certain number of switch entry statements (i.e. case/default). Creates a test result
     * only for the failure case.
     * 
     * @param amount the max number of switch entry statements allowed.
     */
    public void restrictSwitchEntries(int amount) {
        checkFormed(); 

        int numSwitchEntries = countSwitchEntries();
        if (numSwitchEntries > amount) {
            results.add(new MessageTestResult("Too many switch entries.", String.format("Expected at most %d switch entries (i.e. case/default) but found %d.", amount, numSwitchEntries), false, 0));
        }
    }

    /**
     * Counts the number of breaks in the file.
     */
    private int countBreakStatements() {
        checkFormed(); 

        AtomicInteger numBreaks = new AtomicInteger(0);

        testedFile.accept(new VoidVisitorAdapter<Void>() {
            public void visit(BreakStmt breakStmt, Void arg) {
                numBreaks.set(numBreaks.get() + 1);
            }
        }, null);

        return numBreaks.get();
    }

    /**
     * Requires at least a certain number of breaks statements. Creates a test result
     * only for the failure case.
     * 
     * @param amount the minimum number of break statements allowed.
     */
    public void requireBreakStatements(int amount) {
        checkFormed(); 

        int numBreaks = countBreakStatements();
        if (numBreaks < amount) {
            results.add(new MessageTestResult("Break statements required.", String.format("Expected at least %d break statements but found %d.", amount, numBreaks), false, 0));
        }
    }

    /**
     * Requires at most a certain number of break statements. Creates a test result
     * only for the failure case.
     * 
     * @param amount the max number of break statements allowed.
     */
    public void restrictBreakStatements(int amount) {
        checkFormed(); 

        int numBreaks = countBreakStatements();
        if (numBreaks > amount) {
            results.add(new MessageTestResult("Too many break statements.", String.format("Expected at most %d break statements but found %d.", amount, numBreaks), false, 0));
        }
    }

    /**
     * Counts the number of ternaries in the file.
     */
    private int countTernaryExpressions() {
        checkFormed(); 

        AtomicInteger numTernaries = new AtomicInteger(0);

        testedFile.accept(new VoidVisitorAdapter<Void>() {
            public void visit(ConditionalExpr ternaryExpr, Void arg) {
                numTernaries.set(numTernaries.get() + 1);
            }
        }, null);

        return numTernaries.get();
    }

    /**
     * Requires at least a certain number of ternary expressions. Creates a test result
     * only for the failure case.
     * 
     * @param amount the minimum number of ternary expressions allowed.
     */
    public void requireTernaryExpressions(int amount) {
        checkFormed(); 

        int numTernaries = countTernaryExpressions();
        if (numTernaries < amount) {
            results.add(new MessageTestResult("Ternary expression required.", String.format("Expected at least %d ternary expressions but found %d.", amount, numTernaries), false, 0));
        }
    }

    /**
     * Requires at most a certain number of ternary expressions. Creates a test result
     * only for the failure case.
     * 
     * @param amount the max number of ternary expressions allowed.
     */
    public void restrictTernaryExpressions(int amount) {
        checkFormed(); 

        int numTernaries = countTernaryExpressions();
        if (numTernaries > amount) {
            results.add(new MessageTestResult("Too many ternary expressions.", String.format("Expected at most %d ternary expressions but found %d.", amount, numTernaries), false, 0));
        }
    }
}