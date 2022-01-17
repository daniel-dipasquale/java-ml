# java-ml

## About Project

I'm learning how some machine learning algorithms work by implementing them using java. Below is the ordered list of the
algorithms I'm interested in learning and using:

- [x] **NEAT Algorithm**
    - [Evolving Neural Networks through Augmenting Topologies](http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf)
    - [Efficient Evolution of Neural Networks through Complexification](http://nn.cs.utexas.edu/downloads/papers/stanley.phd04.pdf)
- [ ] **AlphaZero**
    - [Mastering Chess and Shogi by Self-Play with a General Reinforcement Learning Algorithm](https://arxiv.org/abs/1712.01815)
    - [A Simple Alpha(Go) Zero Tutorial](https://web.stanford.edu/~surag/posts/alphazero.html)
- [ ] **Gradient Descent Neural Network**
- [ ] **Q-Learning**
- [ ] **Deep Q-Learning**
- [ ] **Soft Actor Critic**

## Test Configuration Examples

1. **NEAT Algorithm**
    - [x] [XOR test](blob/main/tst/com/dipasquale/ai/rl/neat/core/XorTaskSetup.java) :+1: (random data sample)
        - [metrics](https://fv9-3.failiem.lv/thumb_show.php?i=2d5ht3rcy&view)

      ```
      iteration: 1
      generation: 37
      species: 45
      hidden nodes: 1
      expressed connections: 6
      total connections: 8
      maximum fitness: 3.403556
      ```

      ```java
         float[][] inputs = {
                 {1f, 1f}, // => 0f
                 {1f, 0f}, // => 1f
                 {0f, 1f}, // => 1f
                 {0f, 0f}  // => 0f
         };

         float[] expectedOutputs = {0f, 1f, 1f, 0f};

         NeatEvaluator neat = Neat.createEvaluator(EvaluatorSettings.builder()
                 .general(GeneralEvaluatorSupportSettings.builder()
                         .populationSize(IntegerNumber.literal(150))
                         .genesisGenomeTemplate(GenesisGenomeTemplate.builder()
                                 .inputs(IntegerNumber.literal(2))
                                 .outputs(IntegerNumber.literal(1))
                                 .biases(List.of(FloatNumber.literal(1f)))
                                 .initialConnectionType(InitialConnectionType.ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS)
                                 .initialWeightType(InitialWeightType.ALL_RANDOM)
                                 .build())
                         .fitnessFunction((IsolatedNeatEnvironment) genomeActivator -> {
                                 float error = 0f;
                                 NeuronMemory neuronMemory = genomeActivator.createMemory();

                                 for (int i = 0; i < inputs.length; i++) {
                                         float[] output = genomeActivator.activate(inputs[i], neuronMemory);

                                         error += (float) Math.pow(expectedOutputs[i] - output[0], 2D);
                                 }

                                 return (float) inputs.length - error;
                         })
                         .fitnessDeterminerFactory(LastValueFitnessDeterminerFactory.getInstance())
                         .build())
                 .parallelism(ParallelismSupport.builder()
                         .eventLoop(eventLoop)
                         .build())
                 .random(RandomSupport.builder()
                         .type(RandomType.UNIFORM)
                         .build())
                 .nodes(NodeGeneSupport.builder()
                         .inputBias(FloatNumber.literal(0f))
                         .inputActivationFunction(EnumValue.literal(ActivationFunctionType.IDENTITY))
                         .outputBias(FloatNumber.random(RandomType.QUADRUPLE_SIGMOID, 15f))
                         .outputActivationFunction(EnumValue.literal(OutputActivationFunctionType.SIGMOID))
                         .hiddenBias(FloatNumber.random(RandomType.QUADRUPLE_STEEPENED_SIGMOID, 30f))
                         .hiddenActivationFunction(EnumValue.literal(ActivationFunctionType.TAN_H))
                         .build())
                 .connections(ConnectionGeneSupport.builder()
                         .weightFactory(FloatNumber.random(RandomType.BELL_CURVE, 2f))
                         .weightPerturber(FloatNumber.literal(2.5f))
                         .recurrentStateType(RecurrentStateType.DEFAULT)
                         .recurrentAllowanceRate(FloatNumber.literal(0.2f))
                         .unrestrictedDirectionAllowanceRate(FloatNumber.literal(0.5f))
                         .multiCycleAllowanceRate(FloatNumber.literal(0f))
                         .build())
                 .activation(ActivationSupport.builder()
                         .outputLayerNormalizer(IdentityNeuronLayerNormalizer.getInstance())
                         .build())
                 .mutation(MutationSupport.builder()
                         .addNodeRate(FloatNumber.literal(0.03f))
                         .addConnectionRate(FloatNumber.literal(0.06f))
                         .perturbWeightRate(FloatNumber.literal(0.75f))
                         .replaceWeightRate(FloatNumber.literal(0.5f))
                         .disableExpressedConnectionRate(FloatNumber.literal(0.015f))
                         .build())
                 .crossOver(CrossOverSupport.builder()
                         .overrideExpressedConnectionRate(FloatNumber.literal(0.5f))
                         .useWeightFromRandomParentRate(FloatNumber.literal(0.6f))
                         .build())
                 .speciation(SpeciationSupport.builder()
                         .maximumSpecies(IntegerNumber.literal(150))
                         .weightDifferenceCoefficient(FloatNumber.literal(0.4f))
                         .disjointCoefficient(FloatNumber.literal(1f))
                         .excessCoefficient(FloatNumber.literal(1f))
                         .compatibilityThreshold(FloatNumber.literal(3f))
                         .compatibilityThresholdModifier(FloatNumber.literal(1f))
                         .eugenicsThreshold(FloatNumber.literal(0.2f))
                         .elitistThreshold(FloatNumber.literal(0.01f))
                         .elitistThresholdMinimum(IntegerNumber.literal(2))
                         .stagnationDropOffAge(IntegerNumber.literal(15))
                         .interSpeciesMatingRate(FloatNumber.literal(0.001f))
                         .mateOnlyRate(FloatNumber.literal(0.2f))
                         .mutateOnlyRate(FloatNumber.literal(0.25f))
                         .build())
                 .metrics(MetricsSupport.builder()
                         .type(EnumSet.noneOf(MetricCollectionType.class))
                         .build())
                 .build());
      ```

    - [x] [Cart Single Pole Balance test](blob/main/tst/com/dipasquale/ai/rl/neat/core/OpenAIGymCartPoleTaskSetup.java)
      test: :+1: (random data sample)
        - [metrics](https://fv9-3.failiem.lv/thumb_show.php?i=nz4b9euc5&view)

          ![Cart Single Pole Balance](https://i.makeagif.com/media/9-30-2015/3TntUH.gif)

      ```
      iteration: 1
      generation: 17
      species: 44
      hidden nodes: 0
      expressed connections: 4
      total connections: 4
      fitness: 60.009998
      ```

      ```java
           GymClient gymClient = new GymClient();

           NeatEvaluator neat = Neat.createEvaluator(EvaluatorSettings.builder()
                   .general(GeneralEvaluatorSupportSettings.builder()
                           .populationSize(IntegerNumber.literal(150))
                           .genesisGenomeTemplate(GenesisGenomeTemplate.builder()
                                   .inputs(IntegerNumber.literal(4))
                                   .outputs(IntegerNumber.literal(1))
                                   .biases(List.of())
                                   .initialConnectionType(InitialConnectionType.ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS)
                                   .initialWeightType(InitialWeightType.ALL_RANDOM)
                                   .build())
                           .fitnessFunction((IsolatedNeatEnvironment) genomeActivator -> {
                                   double fitness = 0D;
                                   float[] input = convertToFloat(gymClient.start("CartPole-v0", genomeActivator.getGenome().getId()));
                                   NeuronMemory neuronMemory = neuralNetwork.createMemory();

                                   for (boolean done = false; !done; ) {
                                       float[] output = neuralNetwork.activate(input, neuronMemory);
                                       double action = Math.round(output[0]);
                                       StepResult stepResult = gymClient.step(instanceId, action);

                                       done = stepResult.isDone();
                                       input = convertToFloat(stepResult.getObservation());
                                       fitness += stepResult.getReward();
                                   }

                                   return (float) fitness;
                           })
                           .fitnessDeterminerFactory(AverageFitnessDeterminerFactory.getInstance())
                           .build())
                   .nodes(NodeGeneSupport.builder()
                           .hiddenActivationFunction(EnumValue.literal(ActivationFunctionType.RE_LU))
                           .build())
                   .connections(ConnectionGeneSupport.builder()
                           .recurrentAllowanceRate(FloatNumber.literal(0f))
                           .build())
                   .build());
      ```

    - [ ] Tic-Tac-Toe test: :-1: (outstanding)

    - [ ] Chess test: :-1: (outstanding)