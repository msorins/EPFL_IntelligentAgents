## Running steps

1. Import project in intelij & specify ./workspace/logist dependencies
2. Modify compilation output directory to .workspace/reactive/bin/ (https://www.jetbrains.com/help/idea/specifying-compilation-settings.html)
3. **Manual**:

```
cd ./workspace/reactive
JAVA_HOME=$(/usr/libexec/java_home -v 11)
java -jar ../logist/logist.jar config/reactive.xml reactive-random

```
4. **IntelIJ**: To DO


[x] 1.  Download the skeleton files for the reactive exercise
[x] 2.  Import the project in Eclipse (or another IDE, for example Netbeans).
[x] 3.  Reference the library logist and the other libraries that you downloaded from the moodle page for this exercise
[x] 4.  Link the javadoc for the logist library
[x] 5.  Read carefully the documentation for logist - there you can find instructions how to execute the platform
[x] 6.  Write your agent
[ ] 7.  The name of your agent (in agents.xml) that represents your final solution should be reactive-rla
[ ] 8.  Make sure that you fulfill the requirements for the solution given in the exercise description
[ ] 9.  Perform some simulations and analyze the performance of your reactive agent according to the instructions given in the latex template for the report
[ ] 10.	Create a folder named &quot;doc&quot; in the project
[ ] 11. Write documentation using the latex template and place it into a doc folder that you created. The pdf should be named lastname1-lastname2-ex2.pdf. The report should not be longer than 3 pages.
[ ] 12. Zip the folder lastname1-lastname2-ex2 (without the libraries) and submit it on moodle
