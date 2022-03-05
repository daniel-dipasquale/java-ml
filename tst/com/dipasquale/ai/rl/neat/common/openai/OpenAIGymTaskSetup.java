package com.dipasquale.ai.rl.neat.common.openai;

import com.dipasquale.ai.rl.neat.NeatActivator;
import com.dipasquale.ai.rl.neat.common.TaskSetup;

public interface OpenAIGymTaskSetup extends TaskSetup {
    void visualize(NeatActivator activator);
}
