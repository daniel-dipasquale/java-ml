# java-ml

## About Project

I'm learning how some machine learning algorithms work by implementing them using java. Below is the ordered list of the algorithms I'm interested in learning and using:

- [x] [NEAT Algorithm](http://nn.cs.utexas.edu/downloads/papers/stanley.phd04.pdf)
    - [x] XOR test :+1:

         ```
         generation: 13
         species: 8
         fitness: 3.524689
         ```

         ```
         generation: 17
         species: 9
         fitness: 3.577579
         ```

    - [x] Cart-pole balancing test: :+1:

         ```
         generation: 16
         species: 8
         fitness: 60.009998
         ```

         ```
         generation: 7
         species: 8
         fitness: 60.009998
         ```

      ![Cart-pole balancing test](https://i.makeagif.com/media/9-30-2015/3TntUH.gif)

- [ ] Feedforward Neural Networks
- [ ] Soft Actor Critic

## Configuration Examples

1. NEAT Algorithm
    1. XOR test

   ```java
        NeatEvaluator neat = Neat.createEvaluator(EvaluatorSettings.builder()
                        .general(GeneralEvaluatorSupportSettings.builder()
                                .populationSize(populationSize)
                                .genesisGenomeFactory(GenesisGenomeTemplateSettings.builder()
                                        .inputs(IntegerNumberSettings.literal(2))
                                        .inputBias(FloatNumberSettings.literal(0f))
                                        .inputActivationFunction(EnumSettings.literal(ActivationFunctionType.IDENTITY))
                                        .outputs(IntegerNumberSettings.literal(1))
                                        .outputBias(FloatNumberSettings.random(RandomType.UNIFORM, -1f, 1f))
                                        .outputActivationFunction(EnumSettings.literal(OutputActivationFunctionType.SIGMOID))
                                        .biases(ImmutableList.of())
                                        .initialConnectionType(InitialConnectionType.ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS)
                                        .initialWeightType(InitialWeightType.RANDOM)
                                        .build())
                                .fitnessDeterminerFactory(FitnessDeterminerFactory.createLastValue())
                                .fitnessFunction(genome -> {
                                        float error = 0f;

                                        for (int i = 0; i < inputs.length; i++) {
                                                float[] output = genome.activate(inputs[i]);

                                                error += (float) Math.pow(expectedOutputs[i] - output[0], 2D);
                                        }

                                        return inputs.length - error;
                                })
                                .build())
                        .nodes(NodeGeneSupportSettings.builder()
                                .hiddenBias(FloatNumberSettings.random(RandomType.UNIFORM, -1f, 1f))
                                .hiddenActivationFunction(EnumSettings.literal(ActivationFunctionType.TAN_H))
                                .build())
                        .connections(ConnectionGeneSupportSettings.builder()
                                .weightFactory(FloatNumberSettings.random(RandomType.UNIFORM, -1f, 1f))
                                .weightPerturber(FloatNumberSettings.literal(2.5f))
                                .build())
                        .neuralNetwork(NeuralNetworkSupportSettings.builder()
                                .type(NeuralNetworkType.MULTI_CYCLE_RECURRENT)
                                .build())
                        .parallelism(ParallelismSupportSettings.builder()
                                .eventLoop(shouldUseParallelism ? EVENT_LOOP : null)
                                .build())
                        .random(RandomSupportSettings.builder()
                                .nextIndex(RandomType.UNIFORM)
                                .isLessThan(RandomType.UNIFORM)
                                .build())
                        .mutation(MutationSupportSettings.builder()
                                .addNodeMutationRate(FloatNumberSettings.literal(0.1f))
                                .addConnectionMutationRate(FloatNumberSettings.literal(0.2f))
                                .perturbConnectionsWeightRate(FloatNumberSettings.literal(0.75f))
                                .replaceConnectionsWeightRate(FloatNumberSettings.literal(0.5f))
                                .disableConnectionExpressedRate(FloatNumberSettings.literal(0.05f))
                                .build())
                        .crossOver(CrossOverSupportSettings.builder()
                                .mateOnlyRate(FloatNumberSettings.literal(0.2f))
                                .mutateOnlyRate(FloatNumberSettings.literal(0.25f))
                                .overrideConnectionExpressedRate(FloatNumberSettings.literal(0.5f))
                                .useRandomParentConnectionWeightRate(FloatNumberSettings.literal(0.6f))
                                .build())
                        .speciation(SpeciationSupportSettings.builder()
                                .maximumSpecies(IntegerNumberSettings.literal(20))
                                .maximumGenomes(IntegerNumberSettings.literal(20))
                                .weightDifferenceCoefficient(FloatNumberSettings.literal(0.5f))
                                .disjointCoefficient(FloatNumberSettings.literal(1f))
                                .excessCoefficient(FloatNumberSettings.literal(1f))
                                .compatibilityThreshold(FloatNumberSettings.literal(3f))
                                .compatibilityThresholdModifier(FloatNumberSettings.literal(1.2f))
                                .eugenicsThreshold(FloatNumberSettings.literal(0.2f))
                                .elitistThreshold(FloatNumberSettings.literal(0.01f))
                                .elitistThresholdMinimum(IntegerNumberSettings.literal(2))
                                .stagnationDropOffAge(IntegerNumberSettings.literal(15))
                                .interSpeciesMatingRate(FloatNumberSettings.literal(0.001f))
                                .build())
                        .build());
   ```

    1. Cart-pole balancing test:

   ```java
        NeatEvaluator neat = Neat.createEvaluator(EvaluatorSettings.builder()
                        .general(GeneralEvaluatorSupportSettings.builder()
                                .populationSize(populationSize)
                                .genesisGenomeFactory(GenesisGenomeTemplateSettings.builder()
                                        .inputs(IntegerNumberSettings.literal(4))
                                        .inputBias(FloatNumberSettings.literal(0f))
                                        .inputActivationFunction(EnumSettings.literal(ActivationFunctionType.IDENTITY))
                                        .outputs(IntegerNumberSettings.literal(1))
                                        .outputBias(FloatNumberSettings.random(RandomType.UNIFORM, -1f, 1f))
                                        .outputActivationFunction(EnumSettings.literal(OutputActivationFunctionType.TAN_H))
                                        .biases(ImmutableList.of())
                                        .initialConnectionType(InitialConnectionType.ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS)
                                        .initialWeightType(InitialWeightType.RANDOM)
                                        .build())
                                .fitnessDeterminerFactory(FitnessDeterminerFactory.createLastValue())
                                .fitnessFunction(genome -> {
                                        float minimumTimeSpent = Float.MAX_VALUE;

                                        for (int i = 0, attempts = 5; i < attempts; i++) {
                                                CartPoleEnvironment cartPole = CartPoleEnvironment.builder()
                                                        .build();

                                        while (!cartPole.isLimitHit() && Double.compare(cartPole.getTimeSpent(), timeSpentGoal) < 0) {
                                            float[] input = convertToFloat(cartPole.getState());
                                            float[] output = genome.activate(input);

                                            cartPole.stepInDiscrete(output[0]);
                                        }

                                        minimumTimeSpent = Math.min(minimumTimeSpent, (float) cartPole.getTimeSpent());
                                    }

                                    if (Float.compare(minimumTimeSpent, Float.MAX_VALUE) == 0) {
                                        return 0f;
                                    }

                                    return minimumTimeSpent;
                                })
                                .build())
                        .nodes(NodeGeneSupportSettings.builder()
                                .hiddenBias(FloatNumberSettings.random(RandomType.UNIFORM, -1f, 1f))
                                .hiddenActivationFunction(EnumSettings.literal(ActivationFunctionType.SIGMOID))
                                .build())
                        .connections(ConnectionGeneSupportSettings.builder()
                                .weightFactory(FloatNumberSettings.random(RandomType.UNIFORM, -1f, 1f))
                                .weightPerturber(FloatNumberSettings.literal(1.8f))
                                .build())
                        .neuralNetwork(NeuralNetworkSupportSettings.builder()
                                .type(NeuralNetworkType.MULTI_CYCLE_RECURRENT)
                                .build())
                        .parallelism(ParallelismSupportSettings.builder()
                                .eventLoop(shouldUseParallelism ? EVENT_LOOP : null)
                                .build())
                        .random(RandomSupportSettings.builder()
                                .nextIndex(RandomType.UNIFORM)
                                .isLessThan(RandomType.UNIFORM)
                                .build())
                        .mutation(MutationSupportSettings.builder()
                                .addNodeMutationRate(FloatNumberSettings.literal(0.1f))
                                .addConnectionMutationRate(FloatNumberSettings.literal(0.2f))
                                .perturbConnectionsWeightRate(FloatNumberSettings.literal(0.75f))
                                .replaceConnectionsWeightRate(FloatNumberSettings.literal(0.5f))
                                .disableConnectionExpressedRate(FloatNumberSettings.literal(0.05f))
                                .build())
                        .crossOver(CrossOverSupportSettings.builder()
                                .mateOnlyRate(FloatNumberSettings.literal(0.2f))
                                .mutateOnlyRate(FloatNumberSettings.literal(0.25f))
                                .overrideConnectionExpressedRate(FloatNumberSettings.literal(0.5f))
                                .useRandomParentConnectionWeightRate(FloatNumberSettings.literal(0.6f))
                                .build())
                        .speciation(SpeciationSupportSettings.builder()
                                .maximumSpecies(IntegerNumberSettings.literal(20))
                                .maximumGenomes(IntegerNumberSettings.literal(20))
                                .weightDifferenceCoefficient(FloatNumberSettings.literal(0.5f))
                                .disjointCoefficient(FloatNumberSettings.literal(1f))
                                .excessCoefficient(FloatNumberSettings.literal(1f))
                                .compatibilityThreshold(FloatNumberSettings.literal(4f))
                                .compatibilityThresholdModifier(FloatNumberSettings.literal(1.2f))
                                .eugenicsThreshold(FloatNumberSettings.literal(0.4f))
                                .elitistThreshold(FloatNumberSettings.literal(0.01f))
                                .elitistThresholdMinimum(IntegerNumberSettings.literal(2))
                                .stagnationDropOffAge(IntegerNumberSettings.literal(15))
                                .interSpeciesMatingRate(FloatNumberSettings.literal(0.001f))
                                .build())
                        .build());
   ```