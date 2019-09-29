# EPFL Intelligent Agents first homework

Code based mainly on the tutorial from [http://liapc3.epfl.ch/repast/main.htm](http://liapc3.epfl.ch/repast/main.htm).

## TODO

[x] Make agents reproducible

[x] Handle collisions; Rabbits will not go / spawn on other rabbits. Grass wil not spawn on other grass. If grass is spawned on Rabbit, it is instantly eaten by rabbit

[x] Handle no-where to go case ; Agent will try in a random order all positions next to him, if he can he will move, otherwise he stays on-place but still looses energy (getting hungrier)

[x] Distribute grass at each step

[x] How do we handle when some grass was distributed in the current step? Shall we make every move "atomic" in the sense that it will happen instantly? ; Look at the action order, I think we only need to make some conventions regarding what to execute in which order

[ ] Write report


## Action order
The order in which our world is going to execute possible actions has an impact on the behaviour.

Our chosen order for each step is:
  1. Move agents
  2. Agents will try to move to the chosen direction
  3. Agents eat (if they are on a grass)
  4. Agents loose the energy for moving
  5. Agents give birth (if energy >= birthThreshold)
  6. Agents die (if energy = 0) 

## Assumptions

The initial energy levels for each rabbit is a random integer between [levelMin, levelMax]. 
```
levelMin >= 1 && levelMin < birthThreshold
levelMax >= levelMin && levelMax < birthThreshold
```

This is a fair assumption since if a rabbit were to be born with an energy level >= birthThreshold, then it might reproduce right away and we will have more rabbits on the space.

Another assumption consists in the possibility for a grass cell to by spawned directly on top of a rabbit, we allowed for such a case, as everything is random, maybe some rabbits get lucky to receive an extra energy point.

In the case that a rabbit cannot move due to it beeing surrounded by other rabbits, we chose for the rabbit to stay on place but still loose energy (as a penalty for still not being able to search for foog). Maybe we can leave it's energy level as it is?