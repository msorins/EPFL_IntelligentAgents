import sun.jvm.hotspot.gc.shared.Space;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.engine.SimInit;

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
    // TODO Auto-generated method stub
    // Called when button with two curved arrows is clicked
    System.out.println("Running setup");
    space = null;
  }

  public void buildModel(){
    System.out.println("Running BuildModel");
    space = new RabbitsGrassSimulationSpace(this.gridSize);
    space.spreadRabbits(this.numInitRabbits);
    space.spreadGrass(this.numInitGrass);
  }

  public void buildSchedule(){
    System.out.println("Running BuildSchedule");
  }

  public void buildDisplay() {
    System.out.println("Running BuildDisplay");
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
