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

  public void spreadGrass(int numInitGrass) {
    if (numInitGrass > grassSpace.getSizeX() * grassSpace.getSizeY()) {
      throw new RuntimeException("Too much grass");
    }

    distributeValues(grassSpace, numInitGrass);
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
    boolean retVal = false;
    int count = 0;
    int countLimit = 10 * agentSpace.getSizeX() * agentSpace.getSizeY();

    while((!retVal) && (count < countLimit)){
      int x = (int)(Math.random()*(agentSpace.getSizeX()));
      int y = (int)(Math.random()*(agentSpace.getSizeY()));
      if(isCellOccupied(x, y) == false){
        agentSpace.putObjectAt(x, y, agent);
        agent.setXY(x, y);
        retVal = true;
      }
      count++;
    }

    return retVal;
  }

  private void distributeValues(Object2DGrid grid, int total) {
    for (int i = 0; i < total; ++ i) {
      // Choose coordinates
      int x = (int)(Math.random() * grid.getSizeX());
      int y = (int)(Math.random() * grid.getSizeY());

      if (isEmpty(grid, x, y)) {
        // Put grass if empty
        grid.putObjectAt(x, y, 1);
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
