package main;

import logist.LogistPlatform;

public class Main {
  public static void main(String[] args) {
    String argsToSend[] = {"config/reactive.xml", "reactive-random"};
    LogistPlatform.main(argsToSend);
  }
}