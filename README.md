# EPFL Intelligent Agents first homework

## TODO

[ ] Make agents reproducible
[ ] Next-up: [http://liapc3.epfl.ch/repast/HowTo30.htm](http://liapc3.epfl.ch/repast/HowTo30.htm)
[ ] Write report
[ ] Handle colisions
[ ] Handle no-where to go case
[ ] Distribute grass at each step
[ ] How do we handle when some grass was distributed in the current step? Shall we make every move "atomic" in the sense that it will happen instantly? I assume so

## Assumptions

The initial energy levels for each rabbit is a random integer between [1, birthThreshold - 1].

This is a fair assumption since if a rabbit were to be born with an energy level >= birthThreshold, then it might reproduce right away and we will have more rabbits on the space.
