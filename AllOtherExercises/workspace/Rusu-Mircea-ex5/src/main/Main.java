package main;
import logist.LogistPlatform;

public class Main {
    public static void main(String[] args) {
      String decentralizedAgent[] = {"config/auction.xml", "auction-main-02"};
      String decentralizedAgentVsRandom[] = {"config/auction.xml", "auction-main-02", "auction-random"};
      String createTournamentVsRandom[] = {"-new", "smart-vs-random", "config"};
      String runTournamentVsRandom[] = {"-run", "smart-vs-random", "config/auction.xml"};
      String scoreTournamentVsRandom[] = {"-score", "smart-vs-random", "results.out"};

      LogistPlatform.main(createTournamentVsRandom);
      LogistPlatform.main(runTournamentVsRandom);
      LogistPlatform.main(scoreTournamentVsRandom);
    }
}
