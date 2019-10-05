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
