import uchicago.src.sim.space.Object2DGrid;

/**
 * Class that implements the simulation space of the rabbits grass simulation.
 * @author 
 */

public class RabbitsGrassSimulationSpace {
    private Object2DGrid space;

    public RabbitsGrassSimulationSpace(int size){
        space = new Object2DGrid(size, size);
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                space.putObjectAt(i,j, 0);
            }
        }
    }
}
