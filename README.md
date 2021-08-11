# java-ml

## About Project

I'm learning how some machine learning algorithms work by implementing them using java. Below is the ordered list of the
algorithms I'm interested in learning and using:

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
                                        .inputBias(FloatNumber.literal(0f))
                                        .inputActivationFunction(EnumValue.literal(ActivationFunctionType.IDENTITY))
                                        .outputs(IntegerNumber.literal(1))
                                        .outputBias(FloatNumber.random(RandomType.UNIFORM, -1f, 1f))
                                        .outputActivationFunction(EnumValue.literal(OutputActivationFunctionType.SIGMOID))
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
                        .nodes(NodeGeneSupport.builder()
                                .hiddenBias(FloatNumber.random(RandomType.UNIFORM, -1f, 1f))
                                .hiddenActivationFunction(EnumValue.literal(ActivationFunctionType.TAN_H))
                                .build())
                        .connections(ConnectionGeneSupport.builder()
                                .weightFactory(FloatNumber.random(RandomType.UNIFORM, -1f, 1f))
                                .weightPerturber(FloatNumber.literal(2.5f))
                                .build())
                        .neuralNetwork(NeuralNetworkSupport.builder()
                                .type(NeuralNetworkType.MULTI_CYCLE_RECURRENT)
                                .build())
                        .parallelism(ParallelismSupport.builder()
                                .eventLoop(shouldUseParallelism ? EVENT_LOOP : null)
                                .build())
                        .random(RandomSupport.builder()
                                .nextIndex(RandomType.UNIFORM)
                                .isLessThan(RandomType.UNIFORM)
                                .build())
                        .mutation(MutationSupport.builder()
                                .addNodeMutationRate(FloatNumber.literal(0.1f))
                                .addConnectionMutationRate(FloatNumber.literal(0.2f))
                                .perturbConnectionsWeightRate(FloatNumber.literal(0.75f))
                                .replaceConnectionsWeightRate(FloatNumber.literal(0.5f))
                                .disableConnectionExpressedRate(FloatNumber.literal(0.05f))
                                .build())
                        .crossOver(CrossOverSupport.builder()
                                .mateOnlyRate(FloatNumber.literal(0.2f))
                                .mutateOnlyRate(FloatNumber.literal(0.25f))
                                .overrideConnectionExpressedRate(FloatNumber.literal(0.5f))
                                .useRandomParentConnectionWeightRate(FloatNumber.literal(0.6f))
                                .build())
                        .speciation(SpeciationSupport.builder()
                                .maximumSpecies(IntegerNumber.literal(20))
                                .maximumGenomes(IntegerNumber.literal(20))
                                .weightDifferenceCoefficient(FloatNumber.literal(0.5f))
                                .disjointCoefficient(FloatNumber.literal(1f))
                                .excessCoefficient(FloatNumber.literal(1f))
                                .compatibilityThreshold(FloatNumber.literal(3f))
                                .compatibilityThresholdModifier(FloatNumber.literal(1.2f))
                                .eugenicsThreshold(FloatNumber.literal(0.2f))
                                .elitistThreshold(FloatNumber.literal(0.01f))
                                .elitistThresholdMinimum(IntegerNumber.literal(2))
                                .stagnationDropOffAge(IntegerNumber.literal(15))
                                .interSpeciesMatingRate(FloatNumber.literal(0.001f))
                                .build())
                        .build());
   ```

    1. Cart-pole balancing test:

   ```java
        NeatEvaluator neat = Neat.createEvaluator(EvaluatorSettings.builder()
                        .general(GeneralEvaluatorSupportSettings.builder()
                                .populationSize(150)
                                .genesisGenomeFactory(GenesisGenomeTemplate.builder()
                                        .inputs(IntegerNumber.literal(4))
                                        .inputBias(FloatNumber.literal(0f))
                                        .inputActivationFunction(EnumValue.literal(ActivationFunctionType.IDENTITY))
                                        .outputs(IntegerNumber.literal(1))
                                        .outputBias(FloatNumber.random(RandomType.UNIFORM, -1f, 1f))
                                        .outputActivationFunction(EnumValue.literal(OutputActivationFunctionType.TAN_H))
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
                        .nodes(NodeGeneSupport.builder()
                                .hiddenBias(FloatNumber.random(RandomType.UNIFORM, -1f, 1f))
                                .hiddenActivationFunction(EnumValue.literal(ActivationFunctionType.SIGMOID))
                                .build())
                        .connections(ConnectionGeneSupport.builder()
                                .weightFactory(FloatNumber.random(RandomType.UNIFORM, -1f, 1f))
                                .weightPerturber(FloatNumber.literal(1.8f))
                                .build())
                        .neuralNetwork(NeuralNetworkSupport.builder()
                                .type(NeuralNetworkType.MULTI_CYCLE_RECURRENT)
                                .build())
                        .parallelism(ParallelismSupport.builder()
                                .eventLoop(shouldUseParallelism ? EVENT_LOOP : null)
                                .build())
                        .random(RandomSupport.builder()
                                .nextIndex(RandomType.UNIFORM)
                                .isLessThan(RandomType.UNIFORM)
                                .build())
                        .mutation(MutationSupport.builder()
                                .addNodeMutationRate(FloatNumber.literal(0.1f))
                                .addConnectionMutationRate(FloatNumber.literal(0.2f))
                                .perturbConnectionsWeightRate(FloatNumber.literal(0.75f))
                                .replaceConnectionsWeightRate(FloatNumber.literal(0.5f))
                                .disableConnectionExpressedRate(FloatNumber.literal(0.05f))
                                .build())
                        .crossOver(CrossOverSupport.builder()
                                .mateOnlyRate(FloatNumber.literal(0.2f))
                                .mutateOnlyRate(FloatNumber.literal(0.25f))
                                .overrideConnectionExpressedRate(FloatNumber.literal(0.5f))
                                .useRandomParentConnectionWeightRate(FloatNumber.literal(0.6f))
                                .build())
                        .speciation(SpeciationSupport.builder()
                                .maximumSpecies(IntegerNumber.literal(20))
                                .maximumGenomes(IntegerNumber.literal(20))
                                .weightDifferenceCoefficient(FloatNumber.literal(0.5f))
                                .disjointCoefficient(FloatNumber.literal(1f))
                                .excessCoefficient(FloatNumber.literal(1f))
                                .compatibilityThreshold(FloatNumber.literal(4f))
                                .compatibilityThresholdModifier(FloatNumber.literal(1.2f))
                                .eugenicsThreshold(FloatNumber.literal(0.4f))
                                .elitistThreshold(FloatNumber.literal(0.01f))
                                .elitistThresholdMinimum(IntegerNumber.literal(2))
                                .stagnationDropOffAge(IntegerNumber.literal(15))
                                .interSpeciesMatingRate(FloatNumber.literal(0.001f))
                                .build())
                        .build());
   ```