# NeuralSandbox
Create your own racetrack and watch racecars with AI trying to complete your challenge!  
<hr>

## Tutos
1. Extract downloaded .rar file.
2. Make sure to have Java Runtime installed.
3. Run NeuralSandbox.jar in the folder
<hr>

## Controls
### Moving around playground:
ALT + Mouse drag --> Move around playground

### Change view
In View -> View mod, you are able to choose between two modes  
  1. Free look (LALT + Mouse drag)  
  2. Follow -> Follows the best individual in generation  
  
### Adding/Deleting/Changing barriers
You can create or modify level with 3 included tools  
  1. Add barrier (Click and drag, then rotate barrier with vertical movement)  
  2. Change position of barrier (Click on any barrier/start/finish and drag them to desired location)  
  3. Delete barrier (Simply click on a barrier you want to delete)  

It's possible to save or load you level in File menu.  
All levels are saved as *.xml* files.  

### Evolving neural network
New evolution can be started by clicking on *New population* button, which is represented by icon with 3 cars and a star.  
Simulation can be paused and resumed with buttons in toolbar and has no effect on learning or population.  

Many variables can be modified in *Settings* menu, where you can play with Neural Network itself or with the current population.  

### Modifing settings
**NeuralNetwork:**  
It is possible to change structure of the newtork with sliders in Neural Network settings.  
0 -> Layer will not be generated at all.  
1 -> Layer will have 1 neuron.  
2 -> Layer will have 2 neurons.  
Up to 8 neurons in each layer.  
More layers consist of more neurons, which means harder learing.  
Sadly this type of learning algorithm cannot learn bigger networks and even with 2 hidden layers it is extremely difficult to learn.  
That's why it is recommended to have only 1 hidden layer with maximum of 4 neurons  
**Note that these changes cannot effect current generation and NEW population needs to be regenerated.**

**Population:**  
Values in population are essential for effective learning and can be changed however you like.  
Population size is main source of variation in the generation and can heavilly effect learning.  
Mutation is another really important factor and it is required for further progression.  
Kill switch time *kills* whole generation when the certain amount of frames were drawn. --> Prevents from being stuck.    
**These values will take effect on the current generation and are there for you to play with :)**  
<hr>  

## 4K Compatible
Application is fully scalable to 4K and other high resolutions!  
*Except for java.awt stuff* 
<hr>
  
## Testing
Recommended testing:  
1.  In File -> Load level, select level in ./level/snake.xml  
2.  In File -> Load population, select population in ./population/snakeLearn180.xml  
3.  Enjoy!
