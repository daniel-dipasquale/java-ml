# java-ml

## About Project

I'm learning about machine learning algorithms by implementing them in Java. Included in the project are some tests
against simulations using the algorithms implemented.

- [x] **NEAT Algorithm**
    - [Evolving Neural Networks through Augmenting Topologies](http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf)
    - [Efficient Evolution of Neural Networks through Complexification](http://nn.cs.utexas.edu/downloads/papers/stanley.phd04.pdf)
- [ ] **AlphaZero**
    - [ ] Inference implementation
    - [ ] Gradient descent learning implementation
    - [Mastering Chess and Shogi by Self-Play with a General Reinforcement Learning Algorithm](https://arxiv.org/abs/1712.01815)
    - [A Simple Alpha(Go) Zero Tutorial](https://web.stanford.edu/~surag/posts/alphazero.html)
- [ ] **Gradient Descent Neural Network**
- [ ] **Q-Learning**
- [ ] **Deep Q-Learning**
- [ ] **Soft Actor Critic**

## Experiment & Random Sample Results

- [x] [XOR test](tst/com/dipasquale/ai/rl/neat/common/xor/XorTaskSetup.java) :+1:
    - experiment #1:
        - neat algorithm
            - vanilla output topology solution

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
            - double output topology solution

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
            - vanilla output topology solution

      ```
      iteration: 1
      generation: 17
      species: 44
      hidden nodes: 0
      expressed connections: 4
      total connections: 4
      maximum fitness: 60.009998
      ```

    - experiment #2:
        - neat algorithm
            - double output topology solution

      ```
      iteration: 1
      generation: 5
      species: 2
      hidden nodes: 0
      expressed connections: 8
      total connections: 8
      maximum fitness: 60.009998
      ```

- [ ] [Tic-Tac-Toe test](tst/com/dipasquale/ai/rl/neat/common/tictactoe/AlphaZeroTicTacToeDuelTaskSetup.java) :-1: (
  outstanding)
    - experiment #1
        - neat algorithm
            - double output topology solution
            - classic monte carlo tree search duels
        - alpha zero
            - disabled dirichlet noise on root node

- [ ] [2048 test](tst/com/dipasquale/ai/rl/neat/common/game2048/AlphaZeroGame2048TaskSetup.java) :-1: (outstanding)

- [ ] Chess test :-1: (outstanding)