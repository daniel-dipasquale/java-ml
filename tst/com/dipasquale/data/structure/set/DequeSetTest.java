package com.dipasquale.data.structure.set;

import org.junit.Assert;
import org.junit.Test;

public final class DequeSetTest {
    @Test
    public void TEST_1() {
        DequeSet<String> test = DequeSet.create();

        Assert.assertTrue(test instanceof DequeSet<String>);
    }
}
