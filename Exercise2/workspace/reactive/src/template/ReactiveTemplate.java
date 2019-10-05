package template;

import java.nio.file.Files;
import java.util.Random;
import java.util.stream.Collectors;

import logist.simulation.Vehicle;
import logist.agent.Agent;
import logist.behavior.ReactiveBehavior;
import logist.plan.Action;
import logist.plan.Action.Move;
import logist.plan.Action.Pickup;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.topology.Topology;
import logist.topology.Topology.City;

public class ReactiveTemplate implements ReactiveBehavior {

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

  FILE -> SETTINGS -> VERSION CONTROL -> VCSs -> GIT - > Convert Text Files -> "Convert to projects's line separators"

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

    if (availableTask == null || random.nextDouble() > pPickup) {
      City currentCity = vehicle.getCurrentCity();
      action = new Move(currentCity.randomNeighbor(random));
    } else {
      action = new Pickup(availableTask);
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

  private double R(City state, int a) {
    if (a == 0) {
      return 0;
    }
    double avg = 0;
    for (City nextState : this.topology.cities()) {
      avg += this.td.probability(state, nextState) * this.td.reward(state, nextState) / this.td.weight(state, nextState);
    }
    return avg / this.numCities();
  }

  private double T(City state, boolean action, City nextState) {
    /*
     * State: (city, has_package)
     *
     *
     * Action: (MOVE, next_city)
     * Action: (PICK_UP, delivery_city)
     *
     *
     * T(s, a, s') = probablity
     *
     *
     * MOVE
     * T((city, 0), (move, nonAdjCity), (nonAdjCity, 0)) => 0
     *
     * T((city, 0), (move, adjCity), (adjCity, 0)) => 1
     *
     * T((city, 0), (move, adjCity), (adjCity, 1)) => 0
     *
     *
     *
     * T((city, 0), (pickup, destCity), (destCity, 0)) = 1
     *
     * T((city, 1), (move, next(city)), (next(city), 0)) = 0
     *
     *
     * val absoluteVal = MAX_VALUE - (MAX_VALUE - MIN_VALUE) * distance(city, otherCity) / maxDistance;
     *
     * val norm = absoluteVal / 99999
     *
     *
     *
     *
     *
     * (99999 - 1000) * distanta / DISTANTA_MAXIM
     *
     *
     * 1000 + (99999 - 1000) * 1 => 99999
     *
     * 9999 -
     */
  }

  private void learnOffline() {

    System.out.println(topology.cities().stream().map(item -> item.id).collect(Collectors.toList()));
    System.out.println(topology.cities().stream().map(item -> item.name).collect(Collectors.toList()));

    V = new double[this.numCities()];
    bestV = new int[this.numCities()];
    Q = new double[this.numCities()][2];

    // Initialize V random in [0, 1)
    for (int i = 0; i < V.length; ++i) {
      V[i] = Math.random();
    }

    boolean modified;
    do {
      modified = false;
      for (int s = 0; s < this.numCities(); ++s) {
        for (int a = 0; a <= 1; ++a) {
          for (int aS = 0; aS < this.numCities(); ++aS) {
            double expectedNextReward = 0;
            for (int nextS = 0; nextS < this.numCities(); ++nextS) {
              expectedNextReward += this.T(s, a, nextS) * V[nextS];
            }
            Q[s][a] = this.R(s, a) + this.gamma * expectedNextReward;
            if (V[s] < Q[s][a]) {
              V[s] = Q[s][a];
              bestV[s] = a;
              modified = true;
            }
          }
        }
      }
    } while (modified);
  }
}
