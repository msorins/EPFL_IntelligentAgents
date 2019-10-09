package main;
import logist.LogistPlatform;

public class Main {
    public static void main(String[] args) {
        String randomAgent[] = {"config/deliberative.xml", "deliberative-random"};
        String deliberativeAgent[] = {"config/deliberative.xml", "deliberative-main"};
        LogistPlatform.main(deliberativeAgent);
    }
}
