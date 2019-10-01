import uchicago.src.sim.space.Object2DGrid;

/**
 * Class that implements the simulation space of the rabbits grass simulation.
 */

public class RabbitsGrassSimulationSpace {
  private Object2DGrid agentSpace;
  private Object2DGrid grassSpace;

  public RabbitsGrassSimulationSpace(int size) {
    agentSpace = new Object2DGrid(size, size);
    grassSpace = new Object2DGrid(size, size);
    for(int i = 0; i < size; i++){
      for(int j = 0; j < size; j++){
        grassSpace.putObjectAt(i, j, 0);
      }
    }
  }

  public void spreadGrass(int numInitGrass, int energyFromEating) {
    distributeValues(grassSpace, numInitGrass, energyFromEating);
  }

  public int getTotalGrass() {
    int totalGrass = 0;
    for (int i = 0; i < grassSpace.getSizeX(); i++){
      for (int j = 0; j < grassSpace.getSizeY(); j++){
        totalGrass += this.getGrassAt(i, j);
      }
    }
    return totalGrass;
  }

  public int getTotalRabbits() {
    int totalRabbits = 0;
    for (int i = 0; i < agentSpace.getSizeX(); i++){
      for (int j = 0; j < agentSpace.getSizeY(); j++){
        if(this.agentSpace.getObjectAt(i, j) != null) {
          totalRabbits += 1;
        }
      }
    }

    return totalRabbits;
  }

  public int getGrassAt(int x, int y) {
    return getValueAt(grassSpace, x, y);
  }

  public int getAgentAt(int x, int y) {
    return getValueAt(agentSpace, x, y);
  }

  public Object2DGrid getGrassSpace() {
    return grassSpace;
  }

  public Object2DGrid getAgentSpace() {
    return agentSpace;
  }

  public boolean isCellOccupied(int x, int y){
    return agentSpace.getObjectAt(x, y) != null;
  }

  public boolean addAgent(RabbitsGrassSimulationAgent agent){
    int count = 0;
    int countLimit = 10 * agentSpace.getSizeX() * agentSpace.getSizeY();
    while (count < countLimit) {
      int x = (int)(Math.random()*(agentSpace.getSizeX()));
      int y = (int)(Math.random()*(agentSpace.getSizeY()));
      if(isCellOccupied(x, y) == false){
        agentSpace.putObjectAt(x, y, agent);
        agent.setXY(x, y);
        agent.setSpace(this);
        return true;
      }
      count++;
    }

    return false;
  }

  public void removeAgentAt(int x, int y) {
    agentSpace.putObjectAt(x, y, null);
  }

  public int takeEnergyAt(int x, int y) {
    // Get energy from grass
    int energy = getGrassAt(x, y);

    // Reset aka delete the grass
    grassSpace.putObjectAt(x, y, 0);
    return energy;
  }

  public boolean moveAgentAt(int x, int y, int newX, int newY) {
    boolean retVal = false;
    if (!isCellOccupied(newX, newY)) {
      // Get crt rabbit agent
      RabbitsGrassSimulationAgent agent = (RabbitsGrassSimulationAgent) agentSpace.getObjectAt(x, y);

      // Remove it
      removeAgentAt(x, y);

      // Set its new position
      agent.setXY(newX, newY);
      agentSpace.putObjectAt(newX, newY, agent);
      retVal = true;
    }
    return retVal;
  }


  private void distributeValues(Object2DGrid grid, int total, int initialValue) {
    int tries = 10 * grid.getSizeX() * grid.getSizeY();

    for (int i = 0, crtTry = 0; i < total && crtTry < tries;  ++ i, ++ crtTry) {
      // Choose coordinates
      int x = (int)(Math.random() * grid.getSizeX());
      int y = (int)(Math.random() * grid.getSizeY());

      if (isEmpty(grid, x, y)) {
        // Put grass if empty
        grid.putObjectAt(x, y, initialValue);
      } else {
        // Go back one step and try again if non-empty
        -- i;
      }
    }
  }

  private boolean isEmpty(Object2DGrid grid, int x, int y) {
    return grid.getObjectAt(x, y) == null || (Integer) grid.getObjectAt(x, y) == 0;
  }

  private int getValueAt(Object2DGrid grid, int x, int y) {
    if (grid.getObjectAt(x, y) == null) {
      return 0;
    }
    return (Integer)grid.getObjectAt(x, y);
  }
}
