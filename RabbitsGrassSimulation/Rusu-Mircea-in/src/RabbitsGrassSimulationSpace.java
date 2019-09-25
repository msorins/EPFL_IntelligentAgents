import uchicago.src.sim.space.Object2DGrid;

/**
 * Class that implements the simulation space of the rabbits grass simulation.
 */

public class RabbitsGrassSimulationSpace {
  private Object2DGrid rabbitSpace;
  private Object2DGrid grassSpace;

  public RabbitsGrassSimulationSpace(int size) {
    rabbitSpace = new Object2DGrid(size, size);
    grassSpace = new Object2DGrid(size, size);
    for(int i = 0; i < size; i++){
      for(int j = 0; j < size; j++){
        rabbitSpace.putObjectAt(i, j, 0);
        grassSpace.putObjectAt(i, j, 0);
      }
    }
  }

  public void spreadRabbits(int numInitRabbits) {
    if (numInitRabbits > rabbitSpace.getSizeX() * rabbitSpace.getSizeY()) {
      throw new RuntimeException("Too many rabbits");
    }

    distributeValues(rabbitSpace, numInitRabbits);
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

  public int getRabbitAt(int x, int y) {
    return getValueAt(rabbitSpace, x, y);
  }

  private void distributeValues(Object2DGrid grid, int total) {
    for (int i = 0; i < total; ++ i) {
      // Choose coordinates
      int x = (int)(Math.random() * grid.getSizeX());
      int y = (int)(Math.random() * grid.getSizeY());

      if (grid.getObjectAt(x, y) == null) {
        // Put grass if empty
        grid.putObjectAt(x, y, 1);
      } else {
        // Go back one step and try again if non-empty
        -- i;
      }
    }
  }

  private int getValueAt(Object2DGrid grid, int x, int y) {
    if (grid.getObjectAt(x, y) == null) {
      return 0;
    }
    return (Integer)grid.getObjectAt(x, y);
  }
}
