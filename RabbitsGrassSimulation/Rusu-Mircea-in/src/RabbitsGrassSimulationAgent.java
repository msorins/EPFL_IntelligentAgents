import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;

import java.awt.*;
import java.util.Random;


/**
 * Class that implements the simulation agent for the rabbits grass simulation.
 */

public class RabbitsGrassSimulationAgent implements Drawable {
  private int x;
  private int y;
  private int energy;
  private static int IDNumber = 0;
  private int ID;
  private RabbitsGrassSimulationSpace space;

  private static int[] dX = {0, 1,  0, -1};
  private static int[] dY = {1, 0, -1,  0};

  public RabbitsGrassSimulationAgent(int minEnergy, int maxEnergy){
    x = -1;
    y = -1;
    this.energy =
        (int)((Math.random() * (maxEnergy - minEnergy)) + minEnergy);
    ID = ++ IDNumber;
  }

  public void setSpace(RabbitsGrassSimulationSpace space){
    this.space = space;
  }

  public void draw(SimGraphics G) {
    if (energy > 0) {
      G.drawFastRoundRect(Color.blue);
    }
    else {
      // dead 💀
      G.drawFastRoundRect(Color.red);
    }
  }

  public void step(){
    int dir = new Random().nextInt(4); // random int in [0, 3]

    // The following formula makes the grid a torus (no bounds)
    int newx = (x + dX[dir] + space.getAgentSpace().getSizeX()) % space.getAgentSpace().getSizeX();
    int newy = (y + dY[dir] + space.getAgentSpace().getSizeY()) % space.getAgentSpace().getSizeY();

    if (tryMove(newx, newy)) {
      // Eat the energy
      energy += space.takeEnergyAt(x, y);
    }
    // Decrease the energy
    energy --;
  }

  public int getEnergy() {
    return energy;
  }

  public void setEnergy(int energy) {
    this.energy = energy;
  }

  public void setXY(int x, int y){
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public String getID(){
    return "A-" + ID;
  }

  public void report(){
    System.out.println(getID() +
        " at " +
        x + ", " + y +
        " has " +
        getEnergy() + " energy ");
  }

  private boolean tryMove(int newX, int newY) {
    return space.moveAgentAt(x, y, newX, newY);
  }
}
