package main;

//the list of imports

import logist.LogistSettings;
import logist.agent.Agent;
import logist.behavior.AuctionBehavior;
import logist.config.Parsers;
import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.task.TaskSet;
import logist.topology.Topology;
import logist.topology.Topology.City;

import java.io.File;
import java.util.*;

/**
 * Our implementation of the AuctionAgent
 *
 */
public class AuctionAgent implements AuctionBehavior {

	private Topology topology;
	private TaskDistribution distribution;
	private Agent agent;
	private Random random;
	private ArrayList<Task> taskSet;
	private long totalWonBids = 0;

	private static long timeout_setup, timeout_plan, timeout_bid;

	@Override
	public void setup(Topology topology, TaskDistribution distribution,
			Agent agent) {

    // this code is used to get the timeouts
    LogistSettings ls = null;
    try {
      ls = Parsers.parseSettings("config" + File.separator + "settings_auction.xml");
    }
    catch (Exception exc) {
      System.out.println("There was a problem loading the configuration file.");
    }

    // the setup method cannot last more than timeout_setup milliseconds
    timeout_setup = ls.get(LogistSettings.TimeoutKey.SETUP);
    // the plan method cannot execute more than timeout_plan milliseconds
    timeout_plan = ls.get(LogistSettings.TimeoutKey.PLAN);

    timeout_bid = ls.get(LogistSettings.TimeoutKey.BID);

    this.topology = topology;
		this.distribution = distribution;
		this.agent = agent;

		this.totalWonBids = 0;
		this.taskSet = new ArrayList<Task>();

    long seed = -9019554669489983951L * agent.id();
		this.random = new Random(seed);
	}

	public long getCost(List<Vehicle> vehicles, TaskSet tasks, long timeout) {
	  List<Plan> bestPlans = getPlanInTimeout(vehicles, tasks, timeout);

	  long cost = 0;
	  int i = 0;
	  for (Plan plan : bestPlans) {
	    cost += plan.totalDistance() * vehicles.get(i).costPerKm(); /* total distance in km */
        i += 1;
    }

	  return cost;
  }

  public List<Plan> getPlanInTimeout(List<Vehicle> vehicles, TaskSet tasks, long timeout) {
	  // TODO(cosmin): Remove system.out.println for speed
    long time_start = System.currentTimeMillis();
    CSP csp = CSP.generate(vehicles, tasks);

    // PARAMETERS !!
    Integer p = 60;
    Integer nrNeighboursGenerated = 200;
    // END OF PARAMETERS

    Random rand = CSP.getRandom();
    CSP bestCSP = null;
    while(true) {
      CSP newCSP = csp.chooseBestNeighbour(nrNeighboursGenerated);
      // Then with probability p it
      // returns A, with probability 1 − p it returns the current assignment A_old
      if(rand.nextInt(100) <= p) {
        csp = newCSP;
      }

      long time_end = System.currentTimeMillis();
      long duration = time_end - time_start;
      if(duration > timeout - 50) {
        break;
      }

      if(bestCSP == null || csp.cost() < bestCSP.cost() ) {
          bestCSP = csp;
          System.out.println("Found lower cost: " + bestCSP.cost());
      }
      if(duration % 100 == 0) {
          System.out.println("Duration --> " + duration + "; Current cost: " + csp.cost());
      }

    }
    long time_end = System.currentTimeMillis();
    long duration = time_end - time_start;
    System.out.println("Best plan has cost of: " + bestCSP.cost());
    System.out.println("The plan was generated in " + duration + " milliseconds.");
    return bestCSP.toPlans();
  }

  @Override
  public List<Plan> plan(List<Vehicle> vehicles, TaskSet tasks) {
	  return getPlanInTimeout(vehicles, tasks, timeout_plan);
  }

  @Override
	public void auctionResult(Task previous, int winner, Long[] bids) {
    System.out.println("Auction result. Winner " + winner + " got task " + previous.id);
    for (int i = 0; i < bids.length; ++ i) {
      System.out.println("bids[" + i + "] = " + bids[i]);
    }
    if (winner == agent.id()) {
      taskSet.add(new Task(taskSet.size(), previous.pickupCity, previous.deliveryCity, previous.reward, previous.weight));
      totalWonBids += bids[agent.id()];
		}
	}

	private TaskSet toTaskSet(List<Task> taskSet) {
	  Task[] arr = new Task[taskSet.size()];
	  return TaskSet.create(taskSet.toArray(arr));
  }

	@Override
	public Long askPrice(Task task) {

	  taskSet.add(new Task(taskSet.size(), task.pickupCity, task.deliveryCity, task.reward, task.weight));

	  double expectedRevenue = 1.1;

	  double cost = getCost(agent.vehicles(), toTaskSet(taskSet), timeout_bid);
	  double bid = cost * expectedRevenue - totalWonBids;

	  // Remove now this task, it was used just to simulate
    taskSet.remove(taskSet.size() - 1);

    System.out.println("SmartAuction agent " + agent.id() + " has bid " + Math.round(bid) + " for task " + task.id);

		return (long) Math.round(bid);
	}
}
