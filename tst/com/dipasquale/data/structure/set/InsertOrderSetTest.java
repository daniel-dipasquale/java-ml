package com.dipasquale.data.structure.set;

import org.junit.Assert;
import org.junit.Test;

public final class InsertOrderSetTest {
    @Test
    public void TEST_1() {
        InsertOrderSet<String> test = InsertOrderSet.create();

        Assert.assertTrue(test instanceof InsertOrderSet<String>);
    }
}
