# java-ml

## About Project

I'm learning how some machine learning algorithms work by implementing them using java. Below is the ordered list of the
algorithms I'm interested in learning and using:

- [x] [NEAT Algorithm](http://nn.cs.utexas.edu/downloads/papers/stanley.phd04.pdf) (it has issues still)
    - [x] outstanding issues:
        - restarting is buggy, the genome ids aren't reused properly
        - single pole balancing is still outstanding
        - fix adjusted fitness for organisms after evaluation
        - consider handling a full species extinction by restarting the whole process again with a genesis genome
        - among others ...
    - [x] XOR test :+1: (random data sample)

         ```
         generation: 19
         species: 16
         complexity: 8
         fitness: 3.507003
         ```

    - [ ] Single Pole Balancing test: :-1: (random data sample)

         ```
         generation: (outstanding)
         species: (outstanding)
         complexity: (outstanding)
         fitness: (outstanding)
         ```

      ![Single Pole Balancing test](https://i.makeagif.com/media/9-30-2015/3TntUH.gif)

- [ ] Feedforward Neural Networks
- [ ] Soft Actor Critic

## Configuration Examples

1. NEAT Algorithm
    1. XOR test

   ```java
        float[][] inputs = new float[][]{
                new float[]{1f, 1f}, // 0f
                new float[]{1f, 0f}, // 1f
                new float[]{0f, 1f}, // 1f
                new float[]{0f, 0f}  // 0f
        };

        float[] expectedOutputs = new float[]{0f, 1f, 1f, 0f};
   
        NeatEvaluator neat = Neat.createEvaluator(EvaluatorSettings.builder()
                .general(GeneralEvaluatorSupportSettings.builder()
                        .populationSize(150)
                        .genesisGenomeFactory(GenesisGenomeTemplate.builder()
                                .inputs(IntegerNumber.literal(2))
                                .outputs(IntegerNumber.literal(1))
                                .biases(ImmutableList.of())
                                .initialConnectionType(InitialConnectionType.ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS)
                                .initialWeightType(InitialWeightType.RANDOM)
                                .build())
                        .fitnessFunction(genomeActivator -> {
                                float error = 0f;

                                for (int i = 0; i < inputs.length; i++) {
                                        float[] output = genomeActivator.activate(inputs[i]);

                                        error += (float) Math.pow(expectedOutputs[i] - output[0], 2D);
                                }

                                return inputs.length - error;
                        })
                        .fitnessDeterminerFactory(FitnessDeterminerFactory.createLastValue())
                        .build())
                .nodes(NodeGeneSupport.builder()
                        .inputBias(FloatNumber.literal(0f))
                        .inputActivationFunction(EnumValue.literal(ActivationFunctionType.IDENTITY))
                        .outputBias(FloatNumber.random(RandomType.UNIFORM, -1f, 1f))
                        .outputActivationFunction(EnumValue.literal(OutputActivationFunctionType.SIGMOID))
                        .hiddenBias(FloatNumber.random(RandomType.UNIFORM, -1f, 1f))
                        .hiddenActivationFunction(EnumValue.literal(ActivationFunctionType.TAN_H))
                        .build())
                .connections(ConnectionGeneSupport.builder()
                        .weightFactory(FloatNumber.random(RandomType.UNIFORM, -1f, 1f))
                        .weightPerturber(FloatNumber.literal(2.5f))
                        .build())
                .activation(ActivationSupport.builder()
                        .neuralNetworkType(NeuralNetworkType.FEED_FORWARD)
                        .build())
                .parallelism(ParallelismSupport.builder()
                        .eventLoop(eventLoop)
                        .build())
                .random(RandomSupport.builder()
                        .integerGenerator(RandomType.UNIFORM)
                        .floatGenerator(RandomType.UNIFORM)
                        .build())
                .mutation(MutationSupport.builder()
                        .addNodeMutationRate(FloatNumber.literal(0.03f))
                        .addConnectionMutationRate(FloatNumber.literal(0.06f))
                        .perturbWeightRate(FloatNumber.literal(0.75f))
                        .replaceWeightRate(FloatNumber.literal(0.5f))
                        .disableExpressedRate(FloatNumber.literal(0.015f))
                        .build())
                .crossOver(CrossOverSupport.builder()
                        .overrideExpressedRate(FloatNumber.literal(0.5f))
                        .useRandomParentWeightRate(FloatNumber.literal(0.6f))
                        .build())
                .speciation(SpeciationSupport.builder()
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
                .build());
   ```

    1. Cart-pole balancing test:

   ```java
        double timeSpentGoal = 60D;
        RandomSupport randomSupport = new ThreadLocalRandomSupport();

        NeatEvaluator neat = Neat.createEvaluator(EvaluatorSettings.builder()
                .general(GeneralEvaluatorSupportSettings.builder()
                        .populationSize(150)
                        .genesisGenomeFactory(GenesisGenomeTemplate.builder()
                                .inputs(IntegerNumber.literal(4))
                                .outputs(IntegerNumber.literal(1))
                                .biases(ImmutableList.of())
                                .initialConnectionType(InitialConnectionType.ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS)
                                .initialWeightType(InitialWeightType.RANDOM)
                                .build())
                        .fitnessFunction(genomeActivator -> {
                                float minimumTimeSpent = Float.MAX_VALUE;

                                for (int i = 0; i < 5; i++) {
                                    CartPoleEnvironment cartPole = CartPoleEnvironment.createRandom(randomSupport);

                                    while (!cartPole.isLimitHit() && Double.compare(cartPole.getTimeSpent(), timeSpentGoal) < 0) {
                                        float[] input = convertToFloat(cartPole.getState());
                                        float[] output = genomeActivator.activate(input);

                                        cartPole.stepInDiscrete(output[0]);
                                    }

                                    minimumTimeSpent = Math.min(minimumTimeSpent, (float) cartPole.getTimeSpent());
                                }

                                if (Float.compare(minimumTimeSpent, Float.MAX_VALUE) == 0) {
                                    return 0f;
                                }

                                return minimumTimeSpent;
                        })
                        .fitnessDeterminerFactory(FitnessDeterminerFactory.createLastValue())
                        .build())
                .nodes(NodeGeneSupport.builder()
                        .inputBias(FloatNumber.literal(0f))
                        .inputActivationFunction(EnumValue.literal(ActivationFunctionType.IDENTITY))
                        .outputBias(FloatNumber.random(RandomType.UNIFORM, -1f, 1f))
                        .outputActivationFunction(EnumValue.literal(OutputActivationFunctionType.TAN_H))
                        .hiddenBias(FloatNumber.random(RandomType.UNIFORM, -1f, 1f))
                        .hiddenActivationFunction(EnumValue.literal(ActivationFunctionType.SIGMOID))
                        .build())
                .connections(ConnectionGeneSupport.builder()
                        .weightFactory(FloatNumber.random(RandomType.UNIFORM, -1f, 1f))
                        .weightPerturber(FloatNumber.literal(2.5f))
                        .build())
                .activation(ActivationSupport.builder()
                        .neuralNetworkType(NeuralNetworkType.RECURRENT)
                        .build())
                .parallelism(ParallelismSupport.builder()
                        .eventLoop(eventLoop)
                        .build())
                .random(RandomSupport.builder()
                        .integerGenerator(RandomType.UNIFORM)
                        .floatGenerator(RandomType.UNIFORM)
                        .build())
                .mutation(MutationSupport.builder()
                        .addNodeMutationRate(FloatNumber.literal(0.06f))
                        .addConnectionMutationRate(FloatNumber.literal(0.12f))
                        .perturbWeightRate(FloatNumber.literal(0.75f))
                        .replaceWeightRate(FloatNumber.literal(0.5f))
                        .disableExpressedRate(FloatNumber.literal(0.03f))
                        .build())
                .crossOver(CrossOverSupport.builder()
                        .overrideExpressedRate(FloatNumber.literal(0.5f))
                        .useRandomParentWeightRate(FloatNumber.literal(0.6f))
                        .build())
                .speciation(SpeciationSupport.builder()
                        .weightDifferenceCoefficient(FloatNumber.literal(0.5f))
                        .disjointCoefficient(FloatNumber.literal(1f))
                        .excessCoefficient(FloatNumber.literal(1f))
                        .compatibilityThreshold(FloatNumber.literal(3f))
                        .compatibilityThresholdModifier(FloatNumber.literal(1.05f))
                        .eugenicsThreshold(FloatNumber.literal(0.2f))
                        .elitistThreshold(FloatNumber.literal(0.01f))
                        .elitistThresholdMinimum(IntegerNumber.literal(2))
                        .stagnationDropOffAge(IntegerNumber.literal(15))
                        .interSpeciesMatingRate(FloatNumber.literal(0.001f))
                        .mateOnlyRate(FloatNumber.literal(0.4f))
                        .mutateOnlyRate(FloatNumber.literal(0.5f))
                        .build())
                .build());
   ```