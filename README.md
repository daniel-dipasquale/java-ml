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
    - experiment #1:
        - neat algorithm
            - input topology:
                - 1 for x
                - 1 for y
            - bias topology:
                - 1 (weight 1f)
            - output topology:
                - 1 sigmoid

      ```
      iteration: 1
      generation: 37
      species: 45
      hidden nodes: 1
      expressed connections: 6
      total connections: 8
      maximum fitness: 3.403556
      ```

    - experiment #2:
        - neat algorithm
            - input topology:
                - 1 for x
                - 1 for y
            - bias topology:
                - 1 (weight 1f)
            - output topology:
                - 2 sigmoid

      ```
      iteration: 1
      generation: 4
      species: 1
      hidden nodes: 0
      expressed connections: 6
      total connections: 6
      maximum fitness: 3.578723
      ```

- [x] [Cart Single Pole Balance test](tst/com/dipasquale/ai/rl/neat/common/cartpole/CartSinglePoleBalanceTaskSetup.java) :
  +1:
    - experiment #1:
        - neat algorithm
            - input topology:
                - 1 for cart position
                - 1 for cart velocity
                - 1 for pole angle
                - 1 for pole velocity at tip
            - bias topology:
                - 1 (weight 1f)
            - hidden topology:
                - 1 (breaking recommendedgit  but resulting consistent results)
            - output topology:
                - 1 sigmoid

      ```
      iteration: 1
      generation: 11
      species: 28
      hidden nodes: 1
      expressed connections: 6
      total connections: 6
      maximum fitness: 60.009998
      ```

    - experiment #2:
        - neat algorithm
            - input topology:
                - 1 for cart position
                - 1 for cart velocity
                - 1 for pole angle
                - 1 for pole velocity at tip
            - bias topology:
                - 1 (weight 1f)
            - output topology:
                - 2 sigmoid

      ```
      iteration: 1
      generation: 3
      species: 1
      hidden nodes: 0
      expressed connections: 10
      total connections: 10
      maximum fitness: 60.009998
      ```

- [x] [Tic-Tac-Toe test](tst/com/dipasquale/ai/rl/neat/common/tictactoe/TicTacToeTaskSetup.java) :+1:
    - experiment #2
        - neat algorithm:
            - input topology:
                - 1 for player 1
                - 1 for player 2
            - output topology:
                - 1 tanh (value network)
                - 9 sigmoid (policy network)
            - classic monte carlo tree search duels (55% win rate vs 50 cached simulations)
        - alpha zero:
            - maximum expansions: 9
            - value reversed on player 2:
                - disabled state heuristic as value network
            - policy reversed on player 2
            - dirichlet noise on root node disabled
            - cpuct set to rosin
            - back propagation set to BackPropagationType.REVERSED_ON_BACKTRACK
            - temperature threshold: 3rd depth

            ```
            iteration: 1
            generation: 244
            species: 67
            hidden nodes: 4
            expressed connections: 24
            total connections: 28
            maximum fitness: 2.025779
            ```

    - experiment #1
        - neat algorithm:
            - input topology:
                - 1 for player 1
                - 1 for player 2
            - output topology:
                - 2 tanh (value network)
                - 18 sigmoid (policy network)
            - classic monte carlo tree search duels (55% win rate vs 50 cached simulations)
        - alpha zero:
            - maximum expansions: 9
            - value reversed on player 2:
                - disabled state heuristic as value network
            - policy reversed on player 2
            - dirichlet noise on root node disabled
            - cpuct set to rosin
            - back propagation set to BackPropagationType.REVERSED_ON_BACKTRACK
            - temperature threshold: 3rd depth

            ```
            iteration: 1
            generation: 44
            species: 72
            hidden nodes: 0
            expressed connections: 40
            total connections: 40
            maximum fitness: 1.584441
            ```

- [ ] [2048 test](tst/com/dipasquale/ai/rl/neat/common/game2048/Game2048TaskSetup.java) :-1: (outstanding)
