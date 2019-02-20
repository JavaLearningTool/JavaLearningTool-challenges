cd ..
passed=0
failed=0

if [ $1 = "-v" ]; then
    v=1
    echo "Verbose!"
else
    v=0
    echo "Use -v for verbose mode"
fi

for dir in `find . -maxdepth 1 -type d`
do
    # Look for Testers
    if [ -f $dir"/"$dir"Test.java" ]; then

        cd $dir
        
        testName=$(echo $dir| cut -d'/' -f 2)
        echo "Testing $testName"
        
        # Compile and run
        javac=$(javac -cp .:./test:../tester_lib/build/libs/tester_lib-all.jar:../shared *.java test/*.java 2>&1)
        json=$(java -cp .:./test:../tester_lib/build/libs/tester_lib-all.jar:../shared $testName"Test" 2>&1 test/)

        if [[ $json == *"\"passed\": \"true\""* ]] && [[ $json != *"\"passed\": \"false\""* ]]; then
            ((passed++))
        else
            ((failed++))

            if [ $v = 1 ]; then
                echo "JAVAC:"
                echo $javac
                echo
                echo "OUT:"
                echo $json
                echo
            fi

            echo $testName" failed!!!"
        fi

        cd ..

    fi
done

echo "PASSED: $passed"
echo "FAILED: $failed"