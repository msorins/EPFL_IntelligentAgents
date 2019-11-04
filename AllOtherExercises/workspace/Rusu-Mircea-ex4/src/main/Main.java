package main;
import logist.LogistPlatform;

public class Main {
    public static void main(String[] args) {
        String randomAgent[] = {"config/centralized.xml", "centralized-random"};
        String centralizedAgent[] = {"config/centralized.xml", "centralized-main"};
        String centralizedOneAgent[] = {"config/centralized-one-vehicle.xml", "centralized-main"};
        String centralizedTwoIdenticalAgent[] = {"config/centralized-two-identical-vehicle.xml", "centralized-main"};

        LogistPlatform.main(centralizedAgent);
    }
}
