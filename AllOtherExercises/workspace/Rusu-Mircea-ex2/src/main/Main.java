package main;

import logist.LogistPlatform;

public class Main {
  public static void main(String[] args) {
    String randomAgent[] = {"config/reactive.xml", "reactive-random"};
    String reactiveAgent[] = {"config/reactive.xml", "reactive-rla"};
    LogistPlatform.main(reactiveAgent);
  }
}