# java-ml

## About Project

I'm learning how some machine learning algorithms work by implementing them using java. Below is the ordered list of the
algorithms I'm interested in learning and using:

- [x] [NEAT Algorithm](http://nn.cs.utexas.edu/downloads/papers/stanley.phd04.pdf) (it has issues still)
    - [x] outstanding issues:
        - persistence/restore is buggy
        - among others ...
    - [x] XOR test :+1: (random data sample)

         ```
         iteration: 1
         generation: 34
         species: 39
         hidden nodes: 1
         connections: 5
         fitness: 3.858177
         ```

        - [metrics](https://fv9-3.failiem.lv/thumb_show.php?i=8rv3jcqqh&view)

    - [x] Single Pole Balancing test: :+1: (random data sample)

         ```
         iteration: 1
         generation: 125
         species: 64
         hidden nodes: 2
         connections: 5
         fitness: 60.009998
         ```

        - [metrics](https://fv9-3.failiem.lv/thumb_show.php?i=7dbz4jffq&view)

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
                        .genesisGenomeTemplate(GenesisGenomeTemplate.builder()
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

                                return (float) inputs.length - error;
                        })
                        .fitnessDeterminerFactory(new LastValueFitnessDeterminerFactory())
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
                        .type(RandomType.UNIFORM)
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
                .metrics(MetricSupport.builder()
                        .type(EnumSet.noneOf(MetricCollectionType.class))
                        .build())
                .build());
   ```

    1. Cart-pole balancing test:

   ```java
        RandomSupport randomSupport = new ThreadLocalRandomSupport();
        double timeSpentGoal = 60D;

        NeatEvaluator neat = Neat.createEvaluator(EvaluatorSettings.builder()
                .general(GeneralEvaluatorSupportSettings.builder()
                        .populationSize(150)
                        .genesisGenomeTemplate(GenesisGenomeTemplate.builder()
                                .inputs(IntegerNumber.literal(4))
                                .outputs(IntegerNumber.literal(1))
                                .biases(ImmutableList.of())
                                .initialConnectionType(InitialConnectionType.ALL_INPUTS_AND_BIASES_TO_ALL_OUTPUTS)
                                .initialWeightType(InitialWeightType.RANDOM)
                                .build())
                        .fitnessFunction(genomeActivator -> {
                                CartPoleEnvironment cartPole = CartPoleEnvironment.createRandom(randomSupport);

                                while (!cartPole.isLimitHit() && Double.compare(cartPole.getTimeSpent(), timeSpentGoal) < 0) {
                                    float[] input = convertToFloat(cartPole.getState());
                                    float[] output = genomeActivator.activate(input);

                                    cartPole.stepInDiscrete(output[0]);
                                }

                                return (float) cartPole.getTimeSpent();
                        })
                        .fitnessDeterminerFactory(new MinimumFitnessDeterminerFactory())
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
                        .type(RandomType.UNIFORM)
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
                .metrics(MetricSupport.builder()
                        .type(EnumSet.noneOf(MetricCollectionType.class))
                        .build());
   ```