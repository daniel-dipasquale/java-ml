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
   <thead>
      <tr>
         <th>experiment #1</th>
         <th>experiment #2</th>
      </tr>
   </thead>
   <tbody>
      <tr>
         <td colspan="2">
            <strong>neat algorithm:</strong>
         </td>
      </tr>
      <tr>
         <td colspan="2">
            <em>population size:</em>
         </td>
      </tr>
      <tr>
         <td colspan="2">
            <kbd>150</kbd>
         </td>
      </tr>
      <tr>
         <td colspan="2">
            <em>input topology:</em>
         </td>
      </tr>
      <tr>
         <td colspan="2">
            <kbd>1 for X</kbd>
         </td>
      </tr>
      <tr>
         <td colspan="2">
            <kbd>1 for Y</kbd>
         </td>
      </tr>
      <tr>
         <td colspan="2">
            <em>output topology:</em>
         </td>
      </tr>
      <tr>
         <td colspan="1">
            <kbd>1 sigmoid</kbd>
         </td>
         <td colspan="1">
            <kbd>2 sigmoid</kbd>
         </td>
      </tr>
      <tr>
         <td colspan="2">
            <em>bias topology:</em>
         </td>
      </tr>
      <tr>
         <td colspan="2">
            <kbd>1 with bias of 1</kbd>
         </td>
      </tr>
      <tr>
         <td colspan="2">
            <em>initial hidden layer topology:</em>
         </td>
      </tr>
      <tr>
         <td colspan="2">
            <kbd>0 layers</kbd>
         </td>
      </tr>
      <tr>
         <td colspan="2">
            <strong>sample results:</strong>
         </td>
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
   </tbody>
</table>

- [x] [Cart Single Pole Balance test](tst/com/dipasquale/ai/rl/neat/common/cartpole/CartSinglePoleBalanceTaskSetup.java) :
  +1:

<table>
   <thead>
      <tr>
         <th>experiment #1</th>
         <th>experiment #2</th>
      </tr>
   </thead>
   <tbody>
      <tr>
         <td colspan="2">
            <strong>neat algorithm:</strong>
         </td>
      </tr>
      <tr>
         <td colspan="2">
            <em>population size:</em>
         </td>
      </tr>
      <tr>
         <td colspan="2">
            <kbd>150</kbd>
         </td>
      </tr>
      <tr>
         <td colspan="2">
            <em>input topology:</em>
         </td>
      </tr>
      <tr>
         <td colspan="2">
            <kbd>1 for cart position</kbd>
         </td>
      </tr>
      <tr>
         <td colspan="2">
            <kbd>1 for cart velocity</kbd>
         </td>
      </tr>
      <tr>
         <td colspan="2">
            <kbd>1 for pole angle</kbd>
         </td>
      </tr>
      <tr>
         <td colspan="2">
            <kbd>1 for pole velocity at tip</kbd>
         </td>
      </tr>
      <tr>
         <td colspan="2">
            <em>output topology:</em>
         </td>
      </tr>
      <tr>
         <td colspan="1">
            <kbd>1 sigmoid</kbd>
         </td>
         <td colspan="1">
            <kbd>2 sigmoid</kbd>
         </td>
      </tr>
      <tr>
         <td colspan="2">
            <em>bias topology:</em>
         </td>
      </tr>
      <tr>
         <td colspan="2">
            <kbd>1 with bias of 1</kbd>
         </td>
      </tr>
      <tr>
         <td colspan="2">
            <em>initial hidden layer topology:</em>
         </td>
      </tr>
      <tr>
         <td colspan="2">
            <kbd>0 layers</kbd>
         </td>
      </tr>
      <tr>
         <td colspan="2">
            <strong>sample results:</strong>
         </td>
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
   </tbody>
</table>

- [x] [Tic-Tac-Toe test](tst/com/dipasquale/ai/rl/neat/common/tictactoe/TicTacToeTaskSetup.java) :+1:

<table>
   <thead>
      <tr>
         <th>experiment #1</th>
         <th>experiment #2</th>
         <th>experiment #3</th>
         <th>experiment #4</th>
      </tr>
   </thead>
   <tbody>
      <tr>
         <td colspan="4">
            <strong>neat algorithm:</strong>
         </td>
      </tr>
      <tr>
         <td colspan="4">
            <em>population size:</em>
         </td>
      </tr>
      <tr>
         <td colspan="4">
            <kbd>150</kbd>
         </td>
      </tr>
      <tr>
         <td colspan="4">
            <em>input topology:</em>
         </td>
      </tr>
      <tr>
         <td colspan="4">
            <kbd>1 for player 1</kbd>
         </td>
      </tr>
      <tr>
         <td colspan="4">
            <kbd>1 for player 2</kbd>
         </td>
      </tr>
      <tr>
         <td colspan="4">
            <em>output topology:</em>
         </td>
      </tr>
      <tr>
         <td colspan="1">
            <kbd>1 tanh (value network)</kbd>
         </td>
         <td colspan="3">
            <kbd>2 tanh (value network)</kbd>
         </td>
      </tr>
      <tr>
         <td colspan="1">
            <kbd>9 sigmoid (policy network)</kbd>
         </td>
         <td colspan="3">
            <kbd>18 sigmoid (policy network)</kbd>
         </td>
      </tr>
      <tr>
         <td colspan="4">
            <em>bias topology:</em>
         </td>
      </tr>
      <tr>
         <td colspan="4">
            <kbd>0</kbd>
         </td>
      </tr>
      <tr>
         <td colspan="4">
            <em>initial hidden layer topology:</em>
         </td>
      </tr>
      <tr>
         <td colspan="3">
            <kbd>0 layers</kbd>
         </td>
         <td colspan="1">
            <kbd>2 layers of 5, 5</kbd>
         </td>
      </tr>
      <tr>
         <td colspan="4">
            <em>classic monte carlo tree search duels:</em>
         </td>
      </tr>
      <tr>
         <td colspan="4">
            <kbd>training: 12 matches (6 as X player and 6 as O player)</kbd>
         </td>
      </tr>
      <tr>
         <td colspan="4">
            <kbd>acceptance: 55% win rate vs 30 cached classic monte carlo simulations</kbd>
         </td>
      </tr>
      <tr>
         <td colspan="4">
            <strong>alpha zero:</strong>
         </td>
      </tr>
      <tr>
         <td colspan="4">
            <em>maximum expansions: 15</em>
         </td>
      </tr>
      <tr>
         <td colspan="4">
            <em>value reversed on player 2:</em>
         </td>
      </tr>
      <tr>
         <td colspan="4">
            <kbd>state heuristic as value network disabled</kbd>
         </td>
      </tr>
      <tr>
         <td colspan="4">
            <em>policy reversed on player 2</em>
         </td>
      </tr>
      <tr>
         <td colspan="2">
            <em>dirichlet noise on root node disabled</em>
         </td>
         <td colspan="2">
            <em>dirichlet noise on root node enabled</em>
         </td>
      </tr>
      <tr>
         <td colspan="2"></td>
         <td colspan="2">
            <kbd>shape: 0.03, epsilon: 0.25</kbd>
         </td>
      </tr>
      <tr>
         <td colspan="4">
            <em>cpuct set to 1</em>
         </td>
      </tr>
      <tr>
         <td colspan="4">
            <em>back propagation set to BackPropagationType.REVERSED_ON_OPPONENT</em>
         </td>
      </tr>
      <tr>
         <td colspan="4">
            <em>temperature threshold: 3rd depth</em>
         </td>
      </tr>
      <tr>
         <td colspan="4">
            <strong>sample results:</strong>
         </td>
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
   </tbody>
</table>

- [ ] [2048 test](tst/com/dipasquale/ai/rl/neat/common/game2048/Game2048TaskSetup.java) :-1: (outstanding)
