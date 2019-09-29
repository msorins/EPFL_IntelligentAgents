# EPFL Intelligent Agents first homework

Code based mainly on the tutorial from [http://liapc3.epfl.ch/repast/main.htm](http://liapc3.epfl.ch/repast/main.htm).

## TODO

[ ] Make agents reproducible

[ ] Handle collisions

[ ] Handle no-where to go case

[x] Distribute grass at each step

[ ] How do we handle when some grass was distributed in the current step? Shall we make every move "atomic" in the sense that it will happen instantly? I assume so

[ ] Write report

[ ] Rabbit / Grass distribution when there are no 

## Assumptions

The initial energy levels for each rabbit is a random integer between [levelMin, levelMax]. 
```
levelMin >= 1 && levelMin < birthThreshold
levelMax >= levelMin && levelMax < birthThreshold
```

This is a fair assumption since if a rabbit were to be born with an energy level >= birthThreshold, then it might reproduce right away and we will have more rabbits on the space.


Another assumption consists in the possibility for a grass cell to by spawned directly on top of a rabbit, we allowed for such a case, as everything is random, maybe some rabbits get lucky to receive an extra energy point.