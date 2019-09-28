import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.gui.ColorMap;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Value2DDisplay;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.util.SimUtilities;

import java.awt.*;
import java.util.ArrayList;

/**
 * Class that implements the simulation model for the rabbits grass
 * simulation.  This is the first class which needs to be setup in
 * order to run Repast simulation. It manages the entire RePast
 * environment and the simulation.
 */


public class RabbitsGrassSimulationModel extends SimModelImpl {
  // Default values
  private static final int GRIDSIZE = 20;
  private static final int NUMINITRABBITS = 4;
  private static final int NUMINITGRASS = 50;
  private static final int GRASSGROWTHRATE = 5;
  private static final int BIRTHTHRESHOLD = 4;

  private Schedule schedule;
  private RabbitsGrassSimulationSpace space;
  private DisplaySurface displaySurf;
  private ArrayList agentList;

  private int gridSize = GRIDSIZE;
  private int numInitRabbits = NUMINITRABBITS;
  private int numInitGrass = NUMINITGRASS;
  private int grassGrowthRate = GRASSGROWTHRATE;
  private int birthThreshold = BIRTHTHRESHOLD;

  public static void main(String[] args) {

    System.out.println("Rabbit skeleton");

    SimInit init = new SimInit();
    RabbitsGrassSimulationModel model = new RabbitsGrassSimulationModel();
    // Do "not" modify the following lines of parsing arguments
    if (args.length == 0) {
      // by default, you don't use parameter file nor batch mode
      init.loadModel(model, "", false);
    }
    else {
      init.loadModel(model, args[0], Boolean.parseBoolean(args[1]));
    }

  }

  public void begin() {
    // TODO Auto-generated method stub
    // Called when initialised button is clicked
    // Should initialise the simulation
    buildModel();
    buildSchedule();
    buildDisplay();
    displaySurf.display();
  }

  public String[] getInitParam() {
    // TODO Auto-generated method stub
    // Parameters to be set by users via the Repast UI slider bar
    // Do "not" modify the parameters names provided in the skeleton code, you can add more if you want
    String[] params = { "GridSize", "NumInitRabbits", "NumInitGrass", "GrassGrowthRate", "BirthThreshold"};
    return params;
  }

  public String getName() {
    return "Rabbits Grass Simulation";
  }

  public Schedule getSchedule() {
    return this.schedule;
  }

  public void setup() {
    // Called when button with two curved arrows is clicked
    System.out.println("Running setup");
    space = null;
    agentList = new ArrayList();
    schedule = new Schedule(1);

    if (displaySurf != null){
      displaySurf.dispose();
    }

    displaySurf = null;

    displaySurf = new DisplaySurface(this, "Rabbits Grass Simulation Model Window 1");

    registerDisplaySurface("Rabbits Grass Simulation Model Window 1", displaySurf);
  }

  public void buildModel(){
    System.out.println("Running BuildModel");
    space = new RabbitsGrassSimulationSpace(this.gridSize);
    space.spreadGrass(this.numInitGrass);

    for(int i = 0; i < this.numInitRabbits; i++){
      addNewAgent();
    }
    for(int i = 0; i < agentList.size(); i++){
      RabbitsGrassSimulationAgent agent = (RabbitsGrassSimulationAgent)agentList.get(i);
      agent.report();
    }
  }

  public void buildSchedule(){
    System.out.println("Running BuildSchedule");
    class CarryDropStep extends BasicAction {
      public void execute() {
        SimUtilities.shuffle(agentList);

        for(int i =0; i < agentList.size(); i++){
          RabbitsGrassSimulationAgent agent = (RabbitsGrassSimulationAgent) agentList.get(i);
          agent.step();
        }

        reapDeadAgents();
        displaySurf.updateDisplay();
      }
    }

    schedule.scheduleActionBeginning(0, new CarryDropStep());

    class CarryDropCountLiving extends BasicAction {
      public void execute(){
        countLivingAgents();
      }
    }

    schedule.scheduleActionAtInterval(10, new CarryDropCountLiving());
  }

  public void buildDisplay() {
    System.out.println("Running BuildDisplay");
    ColorMap grassColorMap = new ColorMap();

    // No grass is represented as black
    grassColorMap.mapColor(0, Color.black);

    // Grass is represented as green
    grassColorMap.mapColor(1, Color.green);

    Value2DDisplay displayGrass =
        new Value2DDisplay(space.getGrassSpace(), grassColorMap);

    Object2DDisplay displayAgents = new Object2DDisplay(space.getAgentSpace());
    displayAgents.setObjectList(agentList);

    displaySurf.addDisplayableProbeable(displayGrass, "Grass");
    displaySurf.addDisplayableProbeable(displayAgents, "Agents");
  }

  private void addNewAgent(){
    RabbitsGrassSimulationAgent a = new RabbitsGrassSimulationAgent(1, this.birthThreshold);
    agentList.add(a);
    space.addAgent(a);
  }

  private int countLivingAgents(){
    int livingAgents = 0;
    for(int i = 0; i < agentList.size(); i++) {
      RabbitsGrassSimulationAgent agent = (RabbitsGrassSimulationAgent) agentList.get(i);
      if(agent.getEnergy() > 0) {
        livingAgents++;
      }
    }
    System.out.println("Number of living agents is: " + livingAgents);

    return livingAgents;
  }

  private int reapDeadAgents(){
    int count = 0;
    for(int i = (agentList.size() - 1); i >= 0 ; i--){
      RabbitsGrassSimulationAgent agent = (RabbitsGrassSimulationAgent) agentList.get(i);
      if(agent.getEnergy() < 1) {
        space.removeAgentAt(agent.getX(), agent.getY());
        agentList.remove(i);
        count ++;
      }
    }
    return count;
  }

  public void setSchedule(Schedule schedule) {
    this.schedule = schedule;
  }

  public int getGridSize() {
    return gridSize;
  }

  public void setGridSize(int gridSize) {
    this.gridSize = gridSize;
  }

  public int getNumInitRabbits() {
    return numInitRabbits;
  }

  public void setNumInitRabbits(int numInitRabbits) {
    this.numInitRabbits = numInitRabbits;
  }

  public int getNumInitGrass() {
    return numInitGrass;
  }

  public void setNumInitGrass(int numInitGrass) {
    this.numInitGrass = numInitGrass;
  }

  public int getGrassGrowthRate() {
    return grassGrowthRate;
  }

  public void setGrassGrowthRate(int grassGrowthRate) {
    this.grassGrowthRate = grassGrowthRate;
  }

  public int getBirthThreshold() {
    return birthThreshold;
  }

  public void setBirthThreshold(int birthThreshold) {
    this.birthThreshold = birthThreshold;
  }
}
