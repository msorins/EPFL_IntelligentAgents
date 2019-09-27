import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;


/**
 * Class that implements the simulation agent for the rabbits grass simulation.
 */

public class RabbitsGrassSimulationAgent implements Drawable {
  private int x;
  private int y;
  private int energy;
  private static int IDNumber = 0;
  private int ID;

  public RabbitsGrassSimulationAgent(int minEnergy, int maxEnergy){
    x = -1;
    y = -1;
    this.energy =
        (int)((Math.random() * (maxEnergy - minEnergy)) + minEnergy);
    ID = ++ IDNumber;
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
}
