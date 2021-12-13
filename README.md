# java-ml

## About Project

I'm learning how some machine learning algorithms work by implementing them using java. Below is the ordered list of the
algorithms I'm interested in learning and using:

- [x] **NEAT Algorithm**

    - [Evolving Neural Networks through Augmenting Topologies](http://nn.cs.utexas.edu/downloads/papers/stanley.ec02.pdf)
    - [Efficient Evolution of Neural Networks through Complexification](http://nn.cs.utexas.edu/downloads/papers/stanley.phd04.pdf)

    - [x] XOR test :+1: (random data sample)

         ```
         iteration: 1
         generation: 37
         species: 45
         hidden nodes: 1
         connections: 6
         fitness: 3.403556
         ```

        - [metrics](https://fv9-3.failiem.lv/thumb_show.php?i=2d5ht3rcy&view)

    - [x] Single Pole Cart Balance test: :+1: (random data sample)

         ```
         iteration: 1
         generation: 17
         species: 44
         hidden nodes: 0
         connections: 4
         fitness: 60.009998
         ```

        - [metrics](https://fv9-3.failiem.lv/thumb_show.php?i=nz4b9euc5&view)

      ![Single Pole Balancing test](https://i.makeagif.com/media/9-30-2015/3TntUH.gif)

- [ ] Gradient Descent Neural Network
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
                        .fitnessDeterminerFactory(new LastValueFitnessDeterminerFactory())
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
                        .outputBias(FloatNumber.random(RandomType.UNIFORM, 0.05f))
                        .outputActivationFunction(EnumValue.literal(OutputActivationFunctionType.STEEPENED_SIGMOID))
                        .hiddenBias(FloatNumber.random(RandomType.UNIFORM, 2.5f))
                        .hiddenActivationFunction(EnumValue.literal(ActivationFunctionType.STEEPENED_SIGMOID))
                        .build())
                .connections(ConnectionGeneSupport.builder()
                        .weightFactory(FloatNumber.random(RandomType.UNIFORM, 0.1f))
                        .weightPerturber(FloatNumber.literal(2.5f))
                        .recurrentAllowanceRate(FloatNumber.literal(0.2f))
                        .recurrentStateType(RecurrentStateType.DEFAULT)
                        .multiCycleAllowanceRate(FloatNumber.literal(0f))
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
                .metrics(MetricSupport.builder()
                        .type(EnumSet.noneOf(MetricCollectionType.class))
                        .build())
                .build());
   ```

    1. Single Pole Cart Balance test:

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
                        .fitnessDeterminerFactory(new AverageFitnessDeterminerFactory())
                        .build())
                .build());
   ```