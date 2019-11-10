package main;
import logist.LogistPlatform;

public class Main {
    public static void main(String[] args) {
      String decentralizedAgent[] = {"config/auction.xml", "auction-main-02"};
      String decentralizedAgentVsRandom[] = {"config/auction.xml", "auction-main-02", "auction-random"};

      LogistPlatform.main(decentralizedAgentVsRandom);
    }
}
