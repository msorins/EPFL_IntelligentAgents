import uchicago.src.sim.analysis.*;
import uchicago.src.sim.engine.*;
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
  private static final int GRIDSIZE = 50;
  private static final int NUMINITRABBITS = 20;
  private static final int NUMINITGRASS = 1500;
  private static final int GRASSGROWTHRATE = 10;
  private static final int BIRTHTHRESHOLD = 20;
  private static final int MINRABBITINITIALENERGY = 10;
  private static final int MAXRABBITINITIALENERGY = 10;
  private static final int ENERGYLOSTBYREPRODUCING = 10;
  private static final int ENERGYFROMEATING = 3;

  private int gridSize = GRIDSIZE;
  private int numInitRabbits = NUMINITRABBITS;
  private int numInitGrass = NUMINITGRASS;
  private int grassGrowthRate = GRASSGROWTHRATE;
  private int birthThreshold = BIRTHTHRESHOLD;
  private int minRabbitInitialEnergy = MINRABBITINITIALENERGY;
  private int maxRabbitInitialEnergy = MAXRABBITINITIALENERGY;
  private int energyLostByReproducing = ENERGYLOSTBYREPRODUCING;
  private int energyFromEating = ENERGYFROMEATING;

  private Schedule schedule;
  private RabbitsGrassSimulationSpace space;
  private DisplaySurface displaySurf;
  private ArrayList agentList;
  private OpenSequenceGraph grassPerRabbits;
  private OpenHistogram energyDistribution;

  class GrassInSpace implements DataSource, Sequence {
    public Object execute() {
      return getSValue();
    }
    public double getSValue() {
      return (double)space.getTotalGrass();
    }
  }

  class RabbitsInSpace implements DataSource, Sequence {
    public Object execute() {
      return getSValue();
    }
    public double getSValue() {
      return (double)space.getTotalRabbits() * 100;
    }
  }

  class AgentEnergy implements BinDataSource {
    public double getBinValue(Object o) {
      RabbitsGrassSimulationAgent agent = (RabbitsGrassSimulationAgent) o;
      return agent.getEnergy();
    }
  }

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

  public String[] getInitParam() {
    // Parameters to be set by users via the Repast UI slider bar
    // Do "not" modify the parameters names provided in the skeleton code, you can add more if you want
    String[] params = { "GridSize", "NumInitRabbits", "NumInitGrass", "GrassGrowthRate", "BirthThreshold", "MinRabbitInitialEnergy", "MaxRabbitInitialEnergy", "EnergyToReproduce", "EnergyLostByReproducing"};
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

    // Tear down Displays
    if (displaySurf != null){
      displaySurf.dispose();
    }
    displaySurf = null;

    if (grassPerRabbits != null) {
      grassPerRabbits.dispose();
    }
    grassPerRabbits = null;

    if (energyDistribution != null) {
      energyDistribution.dispose();
    }
    energyDistribution = null;

    // Create Displays
    displaySurf = new DisplaySurface(this, "Rabbits Grass Simulation Model Window 1");
    grassPerRabbits = new OpenSequenceGraph("Grass / Rabbits",this);
    energyDistribution = new OpenHistogram("Agent Energy", 8, 0);

    // Register Displays
    registerDisplaySurface("Rabbits Grass Simulation Model Window 1", displaySurf);
    this.registerMediaProducer("Plot", grassPerRabbits);

    System.out.println("Finished setup");
  }

  public void begin() {
    // Called when initialised button is clicked
    // Should initialise the simulation
    System.out.println("Running begin()");
    buildModel();
    buildSchedule();
    buildDisplay();
    displaySurf.display();
    grassPerRabbits.display();
    energyDistribution.display();
  }


  public void buildModel(){
    System.out.println("Running BuildModel");
    // Check for init params errors
    if(this.numInitRabbits + this.numInitGrass > this.gridSize * this.gridSize) {
      throw new Error("Number of rabbits + number of grass bigger than total grid size");
    }

    // Add grass && rabbits
    space = new RabbitsGrassSimulationSpace(this.gridSize);
    space.spreadGrass(this.numInitGrass, energyFromEating);

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
        // Function executing at every step
        SimUtilities.shuffle(agentList);

        // Every agent takes a step && eat if necessary
        for(int i = 0; i < agentList.size(); i++){
          RabbitsGrassSimulationAgent agent = (RabbitsGrassSimulationAgent) agentList.get(i);
          agent.step();

          // Try to reproduce the agent
          if(agent.getEnergy() >= birthThreshold) {
            agent.setEnergy( agent.getEnergy() - energyLostByReproducing);
            addNewAgent();
          }
        }

        // Agent without energy die
        reapDeadAgents();

        // Distribute new grass
        space.spreadGrass(grassGrowthRate, energyFromEating);

        // Update the display with the changes
        displaySurf.updateDisplay();

        // Stop simulation if no more rabbits
        if(agentList.size() == 0) {
          fireSimEvent(new SimEvent(this, SimEvent.STOP_EVENT));
        }

      }
    }

    schedule.scheduleActionBeginning(0, new CarryDropStep());

    class CarryDropCountLiving extends BasicAction {
      public void execute(){
        countLivingAgents();
      }
    }

    schedule.scheduleActionAtInterval(10, new CarryDropCountLiving());

    class UpdateGrassInSpace extends BasicAction {
      public void execute(){
        grassPerRabbits.step();
      }
    }

    schedule.scheduleActionAtInterval(10, new UpdateGrassInSpace());

    class UpdateAgentEnergy extends BasicAction {
      public void execute(){
        if(agentList.size() != 0) {
          energyDistribution.step();
        }
      }
    }

    schedule.scheduleActionAtInterval(1, new UpdateAgentEnergy());
  }

  public void buildDisplay() {
    System.out.println("Running BuildDisplay");
    ColorMap grassColorMap = new ColorMap();

    // No grass is represented as black
    grassColorMap.mapColor(0, Color.black);

    // Grass is represented as green
    grassColorMap.mapColor(ENERGYFROMEATING, Color.green);

    Value2DDisplay displayGrass =
        new Value2DDisplay(space.getGrassSpace(), grassColorMap);

    Object2DDisplay displayAgents = new Object2DDisplay(space.getAgentSpace());
    displayAgents.setObjectList(agentList);

    displaySurf.addDisplayableProbeable(displayGrass, "Grass");
    displaySurf.addDisplayableProbeable(displayAgents, "Agents");

    grassPerRabbits.addSequence("Grass In Space", new GrassInSpace());
    grassPerRabbits.addSequence("Rabbits In Space", new RabbitsInSpace());
    energyDistribution.createHistogramItem("Agent Energy", agentList, new AgentEnergy());
  }

  private void addNewAgent(){
    RabbitsGrassSimulationAgent a = new RabbitsGrassSimulationAgent(Math.min(this.minRabbitInitialEnergy, this.birthThreshold - 1), Math.min(this.maxRabbitInitialEnergy, this.birthThreshold));
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

  public int getMinRabbitInitialEnergy() {
    return minRabbitInitialEnergy;
  }

  public void setMinRabbitInitialEnergy(int minRabbitInitialEnergy) {
    this.minRabbitInitialEnergy = minRabbitInitialEnergy;
  }

  public int getMaxRabbitInitialEnergy() {
    return maxRabbitInitialEnergy;
  }

  public void setMaxRabbitInitialEnergy(int maxRabbitInitialEnergy) {
    this.maxRabbitInitialEnergy = maxRabbitInitialEnergy;
  }

  public int getEnergyLostByReproducing() {
    return energyLostByReproducing;
  }

  public void setEnergyLostByReproducing(int energyLostByReproducing) {
    this.energyLostByReproducing = energyLostByReproducing;
  }

  public int getEnergyFromEating() {
    return energyFromEating;
  }

  public void setEnergyFromEating(int energyFromEating) {
    this.energyFromEating = energyFromEating;
  }
}
