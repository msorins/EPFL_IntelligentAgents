import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;


/**
 * Class that implements the simulation agent for the rabbits grass simulation.
 */

public class RabbitsGrassSimulationAgent implements Drawable {
  private int x;
  private int y;
  private int energy;

  public RabbitsGrassSimulationAgent(int energy){
    x = -1;
    y = -1;
    this.energy = energy;
  }


  public void draw(SimGraphics arg0) {
		// TODO Auto-generated method stub
  }

  public int getEnergy() {
    return energy;
  }

  public void setEnergy(int energy) {
    this.energy = energy;
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }
}
