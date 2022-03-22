# java-ml

## About Project

I'm learning about machine learning algorithms by implementing them in Java. Included in the project are some tests
against simulations using the algorithms implemented.

- [x] **NEAT Algorithm**
    - [Evolving Neural Networks through Augmenting Topologies](http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf)
    - [Efficient Evolution of Neural Networks through Complexification](http://nn.cs.utexas.edu/downloads/papers/stanley.phd04.pdf)
- [ ] **AlphaZero**
    - [x] Inference implementation
    - [ ] Gradient descent learning implementation
    - [Mastering Chess and Shogi by Self-Play with a General Reinforcement Learning Algorithm](https://arxiv.org/abs/1712.01815)
    - [A Simple Alpha(Go) Zero Tutorial](https://web.stanford.edu/~surag/posts/alphazero.html)
- [ ] **Gradient Descent Neural Network**
- [ ] **Q-Learning**
- [ ] **Deep Q-Learning**

## Experiment & Random Sample Results

- [x] [XOR test](tst/com/dipasquale/ai/rl/neat/common/xor/XorTaskSetup.java) :+1:

<table>
   <tr>
      <th>experiment #1</th>
      <th>experiment #2</th>
   </tr>
   <tr>
      <td colspan="2" style="font-weight: bold;">neat algorithm:</td>
   </tr>
   <tr>
      <td colspan="2" style="padding-left: 20px;">population size:</td>
   </tr>
   <tr>
      <td colspan="2" style="padding-left: 40px; font-style: italic;">150</td>
   </tr>
   <tr>
      <td colspan="2" style="padding-left: 20px;">input topology:</td>
   </tr>
   <tr>
      <td colspan="2" style="padding-left: 40px; font-style: italic;">1 for X</td>
   </tr>
   <tr>
      <td colspan="2" style="padding-left: 40px; font-style: italic;">1 for Y</td>
   </tr>
   <tr>
      <td colspan="2" style="padding-left: 20px;">output topology:</td>
   </tr>
   <tr>
      <td colspan="1" style="padding-left: 40px; font-style: italic;">1 sigmoid</td>
      <td colspan="1" style="padding-left: 40px; font-style: italic;">2 sigmoid</td>
   </tr>
   <tr>
      <td colspan="2" style="padding-left: 20px;">bias topology:</td>
   </tr>
   <tr>
      <td colspan="2" style="padding-left: 40px; font-style: italic;">1 with bias of 1</td>
   </tr>
   <tr>
      <td colspan="2" style="padding-left: 20px;">initial hidden layer topology:</td>
   </tr>
   <tr>
      <td colspan="2" style="padding-left: 40px; font-style: italic;">0 layers</td>
   </tr>
   <tr>
      <td colspan="2" style="font-weight: bold;">sample results:</td>
   </tr>
   <tr>
      <td>
         <pre>
iteration: 1
generation: 37
species: 45
hidden nodes: 1
expressed connections: 6
total connections: 8
maximum fitness: 3.403556</pre>
      </td>
      <td>
         <pre>
iteration: 1
generation: 4
species: 1
hidden nodes: 0
expressed connections: 6
total connections: 6
maximum fitness: 3.578723</pre>
      </td>
   </tr>
</table>

- [x] [Cart Single Pole Balance test](tst/com/dipasquale/ai/rl/neat/common/cartpole/CartSinglePoleBalanceTaskSetup.java) :
  +1:

<table>
   <tr>
      <th>experiment #1</th>
      <th>experiment #2</th>
   </tr>
   <tr>
      <td colspan="2" style="font-weight: bold;">neat algorithm:</td>
   </tr>
   <tr>
      <td colspan="2" style="padding-left: 20px;">population size:</td>
   </tr>
   <tr>
      <td colspan="2" style="padding-left: 40px; font-style: italic;">150</td>
   </tr>
   <tr>
      <td colspan="2" style="padding-left: 20px;">input topology:</td>
   </tr>
   <tr>
      <td colspan="2" style="padding-left: 40px; font-style: italic;">1 for cart position</td>
   </tr>
   <tr>
      <td colspan="2" style="padding-left: 40px; font-style: italic;">1 for cart velocity</td>
   </tr>
   <tr>
      <td colspan="2" style="padding-left: 40px; font-style: italic;">1 for pole angle</td>
   </tr>
   <tr>
      <td colspan="2" style="padding-left: 40px; font-style: italic;">1 for pole velocity at tip</td>
   </tr>
   <tr>
      <td colspan="2" style="padding-left: 20px;">output topology:</td>
   </tr>
   <tr>
      <td colspan="1" style="padding-left: 40px; font-style: italic;">1 sigmoid</td>
      <td colspan="1" style="padding-left: 40px; font-style: italic;">2 sigmoid</td>
   </tr>
   <tr>
      <td colspan="2" style="padding-left: 20px;">bias topology:</td>
   </tr>
   <tr>
      <td colspan="2" style="padding-left: 40px; font-style: italic;">1 with bias of 1</td>
   </tr>
   <tr>
      <td colspan="2" style="padding-left: 20px;">initial hidden layer topology:</td>
   </tr>
   <tr>
      <td colspan="2" style="padding-left: 40px; font-style: italic;">0 layers</td>
   </tr>
   <tr>
      <td colspan="2" style="font-weight: bold;">sample results:</td>
   </tr>
   <tr>
      <td>
         <pre>
iteration: 1
generation: 11
species: 28
hidden nodes: 1
expressed connections: 6
total connections: 6
maximum fitness: 60.009998</pre>
      </td>
      <td>
         <pre>
iteration: 1
generation: 3
species: 1
hidden nodes: 0
expressed connections: 10
total connections: 10
maximum fitness: 60.009998</pre>
      </td>
   </tr>
</table>

- [x] [Tic-Tac-Toe test](tst/com/dipasquale/ai/rl/neat/common/tictactoe/TicTacToeTaskSetup.java) :+1:

<table>
   <tr>
      <th>experiment #1</th>
      <th>experiment #2</th>
      <th>experiment #3</th>
      <th>experiment #4</th>
   </tr>
   <tr>
      <td colspan="4" style="font-weight: bold;">neat algorithm:</td>
   </tr>
   <tr>
      <td colspan="4" style="padding-left: 20px;">population size:</td>
   </tr>
   <tr>
      <td colspan="4" style="padding-left: 40px; font-style: italic;">150</td>
   </tr>
   <tr>
      <td colspan="4" style="padding-left: 20px;">input topology:</td>
   </tr>
   <tr>
      <td colspan="4" style="padding-left: 40px; font-style: italic;">1 for player 1</td>
   </tr>
   <tr>
      <td colspan="4" style="padding-left: 40px; font-style: italic;">1 for player 2</td>
   </tr>
   <tr>
      <td colspan="4" style="padding-left: 20px;">output topology:</td>
   </tr>
   <tr>
      <td colspan="1" style="padding-left: 40px; font-style: italic;">1 tanh (value network)</td>
      <td colspan="3" style="padding-left: 40px; font-style: italic;">2 tanh (value network)</td>
   </tr>
   <tr>
      <td colspan="1" style="padding-left: 40px; font-style: italic;">9 sigmoid (policy network)</td>
      <td colspan="3" style="padding-left: 40px; font-style: italic;">18 sigmoid (policy network)</td>
   </tr>
   <tr>
      <td colspan="4" style="padding-left: 20px;">bias topology:</td>
   </tr>
   <tr>
      <td colspan="4" style="padding-left: 40px; font-style: italic;">0</td>
   </tr>
   <tr>
      <td colspan="4" style="padding-left: 20px;">initial hidden layer topology:</td>
   </tr>
   <tr>
      <td colspan="3" style="padding-left: 40px; font-style: italic;">0 layers</td>
      <td colspan="1" style="padding-left: 40px; font-style: italic;">2 layers of 5, 5</td>
   </tr>
   <tr>
      <td colspan="4" style="padding-left: 20px;">classic monte carlo tree search duels:</td>
   </tr>
   <tr>
      <td colspan="4" style="padding-left: 40px; font-style: italic;">training: 12 matches (6 as X player and 6 as O player)</td>
   </tr>
   <tr>
      <td colspan="4" style="padding-left: 40px; font-style: italic;">acceptance: 55% win rate vs 30 cached classic monte carlo simulations</td>
   </tr>
   <tr>
      <td colspan="4" style="font-weight: bold;">alpha zero:</td>
   </tr>
   <tr>
      <td colspan="4" style="padding-left: 20px;">maximum expansions: 15</td>
   </tr>
   <tr>
      <td colspan="4" style="padding-left: 20px;">value reversed on player 2:</td>
   </tr>
   <tr>
      <td colspan="4" style="padding-left: 40px; font-style: italic;">state heuristic as value network disabled</td>
   </tr>
   <tr>
      <td colspan="4" style="padding-left: 20px;">policy reversed on player 2</td>
   </tr>
   <tr>
      <td colspan="2" style="padding-left: 20px;">dirichlet noise on root node disabled</td>
      <td colspan="2" style="padding-left: 20px;">dirichlet noise on root node enabled</td>
   </tr>
   <tr>
      <td colspan="2" style="padding-left: 40px; font-style: italic;"></td>
      <td colspan="2" style="padding-left: 40px; font-style: italic;">shape: 0.03</td>
   </tr>
   <tr>
      <td colspan="2" style="padding-left: 40px; font-style: italic;"></td>
      <td colspan="2" style="padding-left: 40px; font-style: italic;">epsilon: 0.25</td>
   </tr>
   <tr>
      <td colspan="4" style="padding-left: 20px;">cpuct set to 1</td>
   </tr>
   <tr>
      <td colspan="4" style="padding-left: 20px;">back propagation set to BackPropagationType.REVERSED_ON_OPPONENT</td>
   </tr>
   <tr>
      <td colspan="4" style="padding-left: 20px;">temperature threshold: 3rd depth</td>
   </tr>
   <tr>
      <td colspan="4" style="font-weight: bold;">sample results:</td>
   </tr>
   <tr>
      <td>
         <pre>
iteration: 1
generation: 195
species: 69
hidden nodes: 1
expressed connections: 20
total connections: 23
maximum fitness: 2.208129</pre>
      </td>
      <td>
         <pre>
iteration: 1
generation: 138
species: 74
hidden nodes: 1
expressed connections: 41
total connections: 42
maximum fitness: 1.960799</pre>
      </td>
      <td>
         <pre>
iteration: 1
generation: 130
species: 77
hidden nodes: 3
expressed connections: 42
total connections: 49
maximum fitness: 1.958826</pre>
      </td>
      <td>
         <pre>
iteration: 1
generation: 61
species: 89
hidden nodes: 12
expressed connections: 138
total connections: 141
maximum fitness: 2.234746</pre>
      </td>
   </tr>
</table>

- [ ] [2048 test](tst/com/dipasquale/ai/rl/neat/common/game2048/Game2048TaskSetup.java) :-1: (outstanding)
