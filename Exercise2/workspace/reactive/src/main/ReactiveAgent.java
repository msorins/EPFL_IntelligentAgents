package main;

import logist.agent.Agent;
import logist.behavior.ReactiveBehavior;
import logist.plan.Action;
import logist.plan.Action.Move;
import logist.plan.Action.Pickup;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.topology.Topology;
import logist.topology.Topology.City;

import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

class State {
  private City current, packageDest;
  public State(City current, City packageDest) {
    this.current = current;
    this.packageDest = packageDest;
  }

  public City getCurrent() {
    return current;
  }

  public City getPackageDest() {
    return packageDest;
  }

  public int encode(int totalCities) {
    if (packageDest == null) {
      return current.id * (totalCities + 1);
    }
    return current.id * (totalCities + 1) + packageDest.id + 1;
  }
}

class PickupAction extends AgentAction {
  @Override
  int encode(int totalCities) {
    return totalCities;
  }
}

abstract class AgentAction {
  abstract int encode(int totalCities);
}

class MoveAction extends AgentAction {
  City neighbour;
  MoveAction(City neighbour) {
    this.neighbour = neighbour;
  }
  @Override
  int encode(int _totalCities) {
    return this.neighbour.id;
  }
}

public class ReactiveAgent implements ReactiveBehavior {

  private Random random;
  private double pPickup;
  private int numActions;
  private Agent myAgent;
  private Topology topology;
  private TaskDistribution td;
  private double gamma = 0.9;

  private double[] V;
  private int[] bestV;
  private double[][] Q;

  private final AgentAction decode(int x, int totalCitites, Topology tp) {
    if (x == totalCitites) {
      return new PickupAction();
    } else {
      Optional<City> city = tp.cities().stream().filter(c -> c.id == x).findFirst();
      if (city.isEmpty()) {
        throw new RuntimeException("city not found");
      }
      return new MoveAction(city.get());
    }
  }

  @Override
  public void setup(Topology topology, TaskDistribution td, Agent agent) {

    // Reads the discount factor from the agents.xml file.
    // If the property is not present it defaults to 0.95
    Double discount = agent.readProperty("discount-factor", Double.class,
        0.95);

    this.random = new Random();
    this.pPickup = discount;
    this.numActions = 0;
    this.topology = topology;
    this.td = td;
    this.myAgent = agent;

    learnOffline();
  }

  @Override
  public Action act(Vehicle vehicle, Task availableTask) {
    Action action;

    State s = new State(vehicle.getCurrentCity(), null);
    if (availableTask != null && !(random.nextDouble() > pPickup)) {
      if (vehicle.getCurrentCity() == availableTask.pickupCity) {
        s = new State(vehicle.getCurrentCity(), availableTask.deliveryCity);
      } else {
        s = new State(vehicle.getCurrentCity(), null);
      }
    }

    AgentAction agentAction = decode(bestV[s.encode(this.numCities())], this.numCities(), this.topology);

    if (agentAction instanceof MoveAction) {
      MoveAction move = (MoveAction) agentAction;
      action = new Move(move.neighbour);
    } else if (agentAction instanceof PickupAction) {
      action = new Pickup(availableTask);
    } else {
      throw new RuntimeException("Unrecognized action in step");
    }

    if (numActions >= 1) {
      System.out.println("The total profit after " + numActions + " actions is " + myAgent.getTotalProfit() + " (average profit: " + (myAgent.getTotalProfit() / (double) numActions) + ")");
    }
    numActions++;

    return action;
  }

  private int numCities() {
    return this.topology.cities().size();
  }

  private double R(State s, AgentAction action) {
    if (action instanceof PickupAction) {
      // reward of the package - distance to the next city
      return this.td.reward(s.getCurrent(), s.getPackageDest()) - s.getCurrent().distanceTo(s.getPackageDest());
    } else if (action instanceof MoveAction) {
      // negative reward if we don't pick anything
      MoveAction move = (MoveAction) action;
      return -s.getCurrent().distanceUnitsTo(move.neighbour);
    } else {
      throw new RuntimeException("Unknown action for reward");
    }
  }

  private double T(State s, AgentAction a, State sp) {
    if (a instanceof PickupAction) {
      // If we pick something up, we must make sure the states "match"
      if (s.getPackageDest() == sp.getCurrent()) {
        // In the case that they 'match' (ie the action can lead to sp)
        // the probability is the probability that the next state will have those values
        return this.td.probability(sp.getCurrent(), sp.getPackageDest());
      } else {
        // Impossible, if we decide to pickup something, we must deliver to that exact city
        throw new RuntimeException("Not match");
      }
    } else if (a instanceof MoveAction) {
      MoveAction move = (MoveAction) a;
      if (move.neighbour == sp.getCurrent()) {
        return this.td.probability(sp.getCurrent(), sp.getPackageDest());
      } else {
        throw new RuntimeException("Not match");
      }
    } else {
      throw new RuntimeException("Unknown action for T");
    }
  }

  private void learnOffline() {

    System.out.println(topology.cities().stream().map(item -> item.id).collect(Collectors.toList()));
    System.out.println(topology.cities().stream().map(item -> item.name).collect(Collectors.toList()));

    V = new double[(this.numCities() + 1) * this.numCities()];
    bestV = new int[(this.numCities() + 1) * this.numCities()];
    Q = new double[(this.numCities() + 1) * this.numCities()][this.numCities() + 1];

    for (int i = 0; i < V.length; ++i) {
      V[i] = Integer.MIN_VALUE;
    }

    // Number of cities
    int N = this.topology.cities().size();

    boolean modified;
    int epoch = 0;
    do {
      modified = false;
      System.out.println(++ epoch);
      for (City city: this.topology.cities()) {
        State s;
        for (City packageCity : this.topology.cities()) {
          s = new State(city, packageCity);

          // now we can decide which actions to take
          // 1. a potential one is to do a delivery

          AgentAction a = new PickupAction();

          double expectedReward = 0;
          for (City nextPackageCity : this.topology.cities()) {
            State sp = new State(packageCity, nextPackageCity);
            expectedReward += T(s, a, sp) * V[sp.encode(N)];
          }

          Q[s.encode(N)][a.encode(N)] = R(s, a) + gamma * expectedReward;

          if (V[s.encode(N)] < Q[s.encode(N)][a.encode(N)]) {
            V[s.encode(N)] = Q[s.encode(N)][a.encode(N)];
            bestV[s.encode(N)] = a.encode(N);
            modified = true;
          }

          // or
          // 2. we can decide to go to a neighbour city

          for (City neighbour : city.neighbors()) {
            a = new MoveAction(neighbour);
            Q[s.encode(N)][a.encode(N)] = R(s, a);
            expectedReward = 0;
            for (City neighbourPackage : this.topology.cities()) {
              State sp = new State(neighbour, neighbourPackage);
              expectedReward += T(s, a, sp) * V[sp.encode(N)];
            }
            Q[s.encode(N)][a.encode(N)] = R(s, a) + gamma * expectedReward;
            if (V[s.encode(N)] < Q[s.encode(N)][a.encode(N)]) {
              V[s.encode(N)] = Q[s.encode(N)][a.encode(N)];
              bestV[s.encode(N)] = a.encode(N);
              modified = true;
            }
          }
        }
        // or
        // 3. we don't have any available packages here
        s = new State(city, null);
        for (City neighbour : city.neighbors()) {
          AgentAction a = new MoveAction(neighbour);
          Q[s.encode(N)][a.encode(N)] = R(s, a);
          double expectedReward = 0;
          for (City neighbourPackage : this.topology.cities()) {
            State sp = new State(neighbour, neighbourPackage);
            expectedReward += T(s, a, sp) * V[sp.encode(N)];
          }
          Q[s.encode(N)][a.encode(N)] = R(s, a) + gamma * expectedReward;
          if (V[s.encode(N)] < Q[s.encode(N)][a.encode(N)]) {
            V[s.encode(N)] = Q[s.encode(N)][a.encode(N)];
            bestV[s.encode(N)] = a.encode(N);
            modified = true;
          }
        }
      }
    } while (modified);

    for (int i = 0; i < V.length; ++i) {
      System.out.println(V[i]);
    }
  }
}
