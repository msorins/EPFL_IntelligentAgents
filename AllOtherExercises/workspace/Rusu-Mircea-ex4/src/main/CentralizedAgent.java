package main;

//the list of imports

import logist.LogistSettings;
import logist.agent.Agent;
import logist.behavior.CentralizedBehavior;
import logist.config.Parsers;
import logist.plan.Plan;
import logist.simulation.Vehicle;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.task.TaskSet;
import logist.topology.Topology;
import logist.topology.Topology.City;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A very simple auction agent that assigns all tasks to its first vehicle and
 * handles them sequentially.
 *
 */
@SuppressWarnings("unused")
public class CentralizedAgent implements CentralizedBehavior {

    private Topology topology;
    private TaskDistribution distribution;
    private Agent agent;
    private long timeout_setup;
    private long timeout_plan;

    @Override
    public void setup(Topology topology, TaskDistribution distribution,
            Agent agent) {

        // this code is used to get the timeouts
        LogistSettings ls = null;
        try {
            ls = Parsers.parseSettings("config" + File.separator + "settings_default.xml");
        }
        catch (Exception exc) {
            System.out.println("There was a problem loading the configuration file.");
        }

        // the setup method cannot last more than timeout_setup milliseconds
        timeout_setup = ls.get(LogistSettings.TimeoutKey.SETUP);
        // the plan method cannot execute more than timeout_plan milliseconds
        timeout_plan = ls.get(LogistSettings.TimeoutKey.PLAN);

        this.topology = topology;
        this.distribution = distribution;
        this.agent = agent;
    }

    @Override
    public List<Plan> plan(List<Vehicle> vehicles, TaskSet tasks) {
        long time_start = System.currentTimeMillis();
        CSP csp = CSP.generate(vehicles, tasks);

        // PARAMETERS !!
        Integer p = 60;
        Integer nrNeighboursGenerated = 100;
        // END OF PARAMETERS

        Random rand = CSP.getRandom();
        while(true) {
            CSP newCSP = csp.chooseBestNeighbour(nrNeighboursGenerated);
            // Then with probability p it
            // returns A, with probability 1 − p it returns the current assignment A_old
            if(rand.nextInt(100) <= p) {
                csp = newCSP;
            }

            long time_end = System.currentTimeMillis();
            long duration = time_end - time_start;
            if(duration > timeout_plan - 200) {
                break;
            }
            System.out.println("Duration: " + duration + "; Current cost: " + csp.cost());
        }
        long time_end = System.currentTimeMillis();
        long duration = time_end - time_start;
        System.out.println("Best plan has cost of: " + csp.cost());
        System.out.println("The plan was generated in " + duration + " milliseconds.");
        return csp.toPlans();
    }

    private Plan centralizedPlan(Vehicle vehicle, TaskSet tasks) {
        City current = vehicle.getCurrentCity();
        Plan plan = new Plan(current);

        for (Task task : tasks) {
            // move: current city => pickup location
            for (City city : current.pathTo(task.pickupCity)) {
                plan.appendMove(city);
            }

            plan.appendPickup(task);

            // move: pickup location => delivery location
            for (City city : task.path()) {
                plan.appendMove(city);
            }

            plan.appendDelivery(task);

            // set current city
            current = task.deliveryCity;
        }
        return plan;
    }


}
