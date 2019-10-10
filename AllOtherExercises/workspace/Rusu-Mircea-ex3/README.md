## State
Must design state in a way that we can uniquely identify all the possible scenarios in which the world and the agent can be. 

We have a state HashMap so we are not visiting a state the second time as we considered that that path has already been computed.

## State: currentCity
Making only the currentCity as a state is an invalid approach as in this scenario we cannot visit a city twice (which we may need if for example in a city there are more tasks that weight more than the capacity of the agent)

## State: currentCity, deliveringTask
deliveringTask = which tasks the agent is delivering at the current time

Again, there are some distinct scenarios that would be mapped to the same state. (an agent after it delivered a task cannot go to a neighbour city that was already visited before the agent got that task)


## State: currentCity, deliveringTask, deliveredTask
deliveringTask = which tasks the agent is delivering at the current time
deliveringTask = which tasks the agent has delivered until the current time

using the pair of these three info we can map uniquely all the possible states of the world.


## To do
1. "Cost is computed by multiplying the total distance traveled by a vehicle with the cost per kilometer of the vehicle" ... 
at the moment when we are finding the most optimal route for a vehicle we are only taking into consideration  the total distance traveled (basically multypling that with a constant will not render a more efficient path; as from what I understoond each agent, itself is designing a plan at the beginning, if smth unexpected happens in the world it will rebuild that plan )