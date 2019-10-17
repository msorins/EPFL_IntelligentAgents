## State
Must design state in a way that we can uniquely identify all the possible scenarios in which the world and the agent can be. 

We have a state HashMap so we are not visiting a state the second time as we considered that that path has already been computed.

### State: currentCity
Making only the currentCity as a state is an invalid approach as in this scenario we cannot visit a city twice (which we may need if for example in a city there are more tasks that weight more than the capacity of the agent)

##3 State: currentCity, deliveringTask
deliveringTask = which tasks the agent is delivering at the current time

Again, there are some distinct scenarios that would be mapped to the same state. (an agent after it delivered a task cannot go to a neighbour city that was already visited before the agent got that task)


### State: currentCity, deliveringTask, deliveredTask
deliveringTask = which tasks the agent is delivering at the current time
deliveringTask = which tasks the agent has delivered until the current time

using the pair of these three info we can map uniquely all the possible states of the world.


## Info about DFS
dfs is returning a plan object, optimizing by the totalDistance

always making a deep copy of the parameters (once added an Action in the Plan we cannot remove it, so wee need to deep copy stuff)

Order of the actions:
1. If there are tasks that can be delivered in the current city, to that first
2. If there are availabke tasks in the current city, take one by one and start another BFS path (there are cases in which there are more tasks than the agent can take at given time, so which action we choose could influence the results -> another reason for the current state representation)
3. Move to one of the neighbors of the current city

## To do
1. "Cost is computed by multiplying the total distance traveled by a vehicle with the cost per kilometer of the vehicle" ... 
at the moment when we are finding the most optimal route for a vehicle we are only taking into consideration  the total distance traveled (basically multypling that with a constant will not render a more efficient path; as from what I understoond each agent, itself is designing a plan at the beginning, if smth unexpected happens in the world it will rebuild that plan ) []
2. A* []
3. implement planCancelled in BFS []

I think this is done, we are just taking into consideration the tasks an agent is already doing (in the init step) ~ read below

4. implement planCancelled in A* []

planCancelled method info:
```
void planCancelled(TaskSet carriedTasks)
In a multi agent system the plan of an agent might get stuck when the agent
tries to pick up a task that has already been picked up by another agent. In
this case this method is called followed by an another plan computation (a
call to the method above). This time the vehicle might be carrying some tasks
initially (the carriedTasks argument) and these tasks have to be considered
when the next plan is computed. You can also use the getCurrentTasks()
method of the vehicle to obtain the set of tasks that the vehicle is holding
```