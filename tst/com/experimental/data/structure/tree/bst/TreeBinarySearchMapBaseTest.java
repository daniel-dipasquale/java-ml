//package com.experimental.data.structure.tree.bst;
//
//import com.google.common.collect.ImmutableList;
//import org.junit.Assert;
//import org.junit.Test;
//
//import java.util.AbstractMap;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Map;
//
//public final class TreeBinarySearchMapBaseTest {
//    private static TestTreeBinarySearchMap createBsTreeMap() {
//        TestTreeBinarySearchMap map = new TestTreeBinarySearchMap(Integer::compareTo);
//
//        map.put(24, "24");
//        map.put(12, "12");
//        map.put(6, "6");
//        map.put(3, "3");
//        map.put(9, "9");
//        map.put(18, "18");
//        map.put(15, "15");
//        map.put(21, "21");
//        map.put(36, "36");
//        map.put(30, "30");
//        map.put(27, "27");
//        map.put(33, "33");
//        map.put(42, "42");
//        map.put(39, "39");
//        map.put(45, "45");
//
//        return map;
//    }
//
//    @Test
//    public void TEST_1() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//        List<Map.Entry<Integer, String>> result = ImmutableList.copyOf(test.iterator(true));
//
//        Assert.assertEquals(ImmutableList.<Map.Entry<Integer, String>>builder()
//                .add(new AbstractMap.SimpleImmutableEntry<>(3, "3"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(6, "6"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(9, "9"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(12, "12"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(15, "15"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(18, "18"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(21, "21"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(24, "24"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(27, "27"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(30, "30"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(33, "33"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(36, "36"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(39, "39"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(42, "42"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(45, "45"))
//                .build(), result);
//    }
//
//    @Test
//    public void TEST_2() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//        List<Map.Entry<Integer, String>> result = ImmutableList.copyOf(test.iterator(false));
//
//        Assert.assertEquals(ImmutableList.<Map.Entry<Integer, String>>builder()
//                .add(new AbstractMap.SimpleImmutableEntry<>(45, "45"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(42, "42"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(39, "39"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(36, "36"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(33, "33"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(30, "30"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(27, "27"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(24, "24"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(21, "21"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(18, "18"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(15, "15"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(12, "12"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(9, "9"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(6, "6"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(3, "3"))
//                .build(), result);
//    }
//
//    @Test
//    public void TEST_3() {
//        TestTreeBinarySearchMap test = new TestTreeBinarySearchMap(Integer::compareTo) {{
//            put(3, "3");
//        }};
//
//        List<Map.Entry<Integer, String>> result = ImmutableList.copyOf(test.iterator(true));
//
//        Assert.assertEquals(ImmutableList.<Map.Entry<Integer, String>>builder()
//                .add(new AbstractMap.SimpleImmutableEntry<>(3, "3"))
//                .build(), result);
//    }
//
//    @Test
//    public void TEST_4() {
//        TestTreeBinarySearchMap test = new TestTreeBinarySearchMap(Integer::compareTo) {{
//            put(3, "3");
//        }};
//
//        List<Map.Entry<Integer, String>> result = ImmutableList.copyOf(test.iterator(false));
//
//        Assert.assertEquals(ImmutableList.<Map.Entry<Integer, String>>builder()
//                .add(new AbstractMap.SimpleImmutableEntry<>(3, "3"))
//                .build(), result);
//    }
//
//    @Test
//    public void TEST_5() {
//        TestTreeBinarySearchMap test = new TestTreeBinarySearchMap(Integer::compareTo);
//        List<Map.Entry<Integer, String>> result = ImmutableList.copyOf(test.iterator(true));
//
//        Assert.assertEquals(ImmutableList.of(), result);
//    }
//
//    @Test
//    public void TEST_6() {
//        TestTreeBinarySearchMap test = new TestTreeBinarySearchMap(Integer::compareTo);
//        List<Map.Entry<Integer, String>> result = ImmutableList.copyOf(test.iterator(false));
//
//        Assert.assertEquals(ImmutableList.of(), result);
//    }
//
//    @Test
//    public void TEST_7() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//
//        Assert.assertFalse(test.containsKey(-1));
//        Assert.assertTrue(test.containsKey(3));
//        Assert.assertFalse(test.containsKey(7));
//        Assert.assertTrue(test.containsKey(24));
//        Assert.assertFalse(test.containsKey(41));
//        Assert.assertTrue(test.containsKey(45));
//    }
//
//    @Test
//    public void TEST_8() {
//        Comparator<Integer> comparator = Integer::compareTo;
//        TestTreeBinarySearchMap test = new TestTreeBinarySearchMap(comparator);
//
//        Assert.assertEquals(comparator, test.comparator());
//    }
//
//    @Test
//    public void TEST_9() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//
//        Assert.assertEquals(15, test.size());
//    }
//
//    @Test
//    public void TEST_10() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//
//        Assert.assertNull(test.get(-1));
//        Assert.assertEquals("3", test.get(3));
//        Assert.assertNull(test.get(7));
//        Assert.assertEquals("24", test.get(24));
//        Assert.assertNull(test.get(41));
//        Assert.assertEquals("45", test.get(45));
//    }
//
//    @Test
//    public void TEST_11() {
//        TestTreeBinarySearchMap test = new TestTreeBinarySearchMap(Integer::compareTo) {{
//            put(3, "3");
//            put(3, "3-changed");
//        }};
//
//        Assert.assertEquals("3-changed", test.get(3));
//        Assert.assertEquals(1, test.size());
//    }
//
//    @Test
//    public void TEST_12() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//        List<Map.Entry<Integer, String>> result = ImmutableList.copyOf(test.iteratorFrom(18, true));
//
//        Assert.assertEquals(ImmutableList.<Map.Entry<Integer, String>>builder()
//                .add(new AbstractMap.SimpleImmutableEntry<>(18, "18"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(21, "21"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(24, "24"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(27, "27"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(30, "30"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(33, "33"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(36, "36"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(39, "39"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(42, "42"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(45, "45"))
//                .build(), result);
//    }
//
//    @Test
//    public void TEST_13() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//        List<Map.Entry<Integer, String>> result = ImmutableList.copyOf(test.iteratorFrom(24, true));
//
//        Assert.assertEquals(ImmutableList.<Map.Entry<Integer, String>>builder()
//                .add(new AbstractMap.SimpleImmutableEntry<>(24, "24"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(27, "27"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(30, "30"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(33, "33"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(36, "36"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(39, "39"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(42, "42"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(45, "45"))
//                .build(), result);
//    }
//
//    @Test
//    public void TEST_14() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//        List<Map.Entry<Integer, String>> result = ImmutableList.copyOf(test.iteratorFrom(33, true));
//
//        Assert.assertEquals(ImmutableList.<Map.Entry<Integer, String>>builder()
//                .add(new AbstractMap.SimpleImmutableEntry<>(33, "33"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(36, "36"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(39, "39"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(42, "42"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(45, "45"))
//                .build(), result);
//    }
//
//    @Test
//    public void TEST_15() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//        List<Map.Entry<Integer, String>> result = ImmutableList.copyOf(test.iteratorFrom(0, true));
//
//        Assert.assertEquals(ImmutableList.<Map.Entry<Integer, String>>builder()
//                .add(new AbstractMap.SimpleImmutableEntry<>(3, "3"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(6, "6"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(9, "9"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(12, "12"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(15, "15"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(18, "18"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(21, "21"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(24, "24"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(27, "27"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(30, "30"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(33, "33"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(36, "36"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(39, "39"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(42, "42"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(45, "45"))
//                .build(), result);
//    }
//
//    @Test
//    public void TEST_16() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//        List<Map.Entry<Integer, String>> result = ImmutableList.copyOf(test.iteratorFrom(7, true));
//
//        Assert.assertEquals(ImmutableList.<Map.Entry<Integer, String>>builder()
//                .add(new AbstractMap.SimpleImmutableEntry<>(9, "9"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(12, "12"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(15, "15"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(18, "18"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(21, "21"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(24, "24"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(27, "27"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(30, "30"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(33, "33"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(36, "36"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(39, "39"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(42, "42"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(45, "45"))
//                .build(), result);
//    }
//
//    @Test
//    public void TEST_17() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//        List<Map.Entry<Integer, String>> result = ImmutableList.copyOf(test.iteratorFrom(17, true));
//
//        Assert.assertEquals(ImmutableList.<Map.Entry<Integer, String>>builder()
//                .add(new AbstractMap.SimpleImmutableEntry<>(18, "18"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(21, "21"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(24, "24"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(27, "27"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(30, "30"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(33, "33"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(36, "36"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(39, "39"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(42, "42"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(45, "45"))
//                .build(), result);
//    }
//
//    @Test
//    public void TEST_18() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//        List<Map.Entry<Integer, String>> result = ImmutableList.copyOf(test.iteratorFrom(35, true));
//
//        Assert.assertEquals(ImmutableList.<Map.Entry<Integer, String>>builder()
//                .add(new AbstractMap.SimpleImmutableEntry<>(36, "36"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(39, "39"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(42, "42"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(45, "45"))
//                .build(), result);
//    }
//
//    @Test
//    public void TEST_19() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//        List<Map.Entry<Integer, String>> result = ImmutableList.copyOf(test.iteratorFrom(44, true));
//
//        Assert.assertEquals(ImmutableList.<Map.Entry<Integer, String>>builder()
//                .add(new AbstractMap.SimpleImmutableEntry<>(45, "45"))
//                .build(), result);
//    }
//
//    @Test
//    public void TEST_20() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//        List<Map.Entry<Integer, String>> result = ImmutableList.copyOf(test.iteratorFrom(46, true));
//
//        Assert.assertEquals(ImmutableList.of(), result);
//    }
//
//    @Test
//    public void TEST_21() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//
//        test.remove(3);
//
//        List<Map.Entry<Integer, String>> result = ImmutableList.copyOf(test.iterator(true));
//
//        Assert.assertEquals(ImmutableList.<Map.Entry<Integer, String>>builder()
//                .add(new AbstractMap.SimpleImmutableEntry<>(6, "6"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(9, "9"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(12, "12"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(15, "15"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(18, "18"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(21, "21"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(24, "24"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(27, "27"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(30, "30"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(33, "33"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(36, "36"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(39, "39"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(42, "42"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(45, "45"))
//                .build(), result);
//
//        Assert.assertEquals(14, test.size());
//    }
//
//    @Test
//    public void TEST_22() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//
//        test.remove(12);
//
//        List<Map.Entry<Integer, String>> result = ImmutableList.copyOf(test.iterator(true));
//
//        Assert.assertEquals(ImmutableList.<Map.Entry<Integer, String>>builder()
//                .add(new AbstractMap.SimpleImmutableEntry<>(3, "3"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(6, "6"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(9, "9"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(15, "15"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(18, "18"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(21, "21"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(24, "24"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(27, "27"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(30, "30"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(33, "33"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(36, "36"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(39, "39"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(42, "42"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(45, "45"))
//                .build(), result);
//
//        Assert.assertEquals(14, test.size());
//    }
//
//    @Test
//    public void TEST_23() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//
//        test.remove(18);
//
//        List<Map.Entry<Integer, String>> result = ImmutableList.copyOf(test.iterator(true));
//
//        Assert.assertEquals(ImmutableList.<Map.Entry<Integer, String>>builder()
//                .add(new AbstractMap.SimpleImmutableEntry<>(3, "3"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(6, "6"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(9, "9"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(12, "12"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(15, "15"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(21, "21"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(24, "24"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(27, "27"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(30, "30"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(33, "33"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(36, "36"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(39, "39"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(42, "42"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(45, "45"))
//                .build(), result);
//
//        Assert.assertEquals(14, test.size());
//    }
//
//    @Test
//    public void TEST_24() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//
//        test.remove(24);
//
//        List<Map.Entry<Integer, String>> result = ImmutableList.copyOf(test.iterator(true));
//
//        Assert.assertEquals(ImmutableList.<Map.Entry<Integer, String>>builder()
//                .add(new AbstractMap.SimpleImmutableEntry<>(3, "3"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(6, "6"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(9, "9"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(12, "12"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(15, "15"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(18, "18"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(21, "21"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(27, "27"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(30, "30"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(33, "33"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(36, "36"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(39, "39"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(42, "42"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(45, "45"))
//                .build(), result);
//
//        Assert.assertEquals(14, test.size());
//    }
//
//    @Test
//    public void TEST_25() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//
//        test.remove(36);
//
//        List<Map.Entry<Integer, String>> result = ImmutableList.copyOf(test.iterator(true));
//
//        Assert.assertEquals(ImmutableList.<Map.Entry<Integer, String>>builder()
//                .add(new AbstractMap.SimpleImmutableEntry<>(3, "3"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(6, "6"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(9, "9"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(12, "12"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(15, "15"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(18, "18"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(21, "21"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(24, "24"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(27, "27"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(30, "30"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(33, "33"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(39, "39"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(42, "42"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(45, "45"))
//                .build(), result);
//
//        Assert.assertEquals(14, test.size());
//    }
//
//    @Test
//    public void TEST_26() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//
//        test.remove(0);
//
//        List<Map.Entry<Integer, String>> result = ImmutableList.copyOf(test.iterator(true));
//
//        Assert.assertEquals(ImmutableList.<Map.Entry<Integer, String>>builder()
//                .add(new AbstractMap.SimpleImmutableEntry<>(3, "3"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(6, "6"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(9, "9"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(12, "12"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(15, "15"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(18, "18"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(21, "21"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(24, "24"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(27, "27"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(30, "30"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(33, "33"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(36, "36"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(39, "39"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(42, "42"))
//                .add(new AbstractMap.SimpleImmutableEntry<>(45, "45"))
//                .build(), result);
//
//        Assert.assertEquals(15, test.size());
//    }
//
//    @Test
//    public void TEST_27() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//
//        test.clear();
//
//        List<Map.Entry<Integer, String>> result = ImmutableList.copyOf(test.iterator(true));
//
//        Assert.assertEquals(ImmutableList.of(), result);
//    }
//
//    @Test
//    public void TEST_28() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//        Map.Entry<Integer, String> result = test.minimumEntry(42);
//
//        Assert.assertEquals(new AbstractMap.SimpleImmutableEntry<>(39, "39"), result);
//    }
//
//    @Test
//    public void TEST_29() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//        Map.Entry<Integer, String> result = test.minimumEntry(24);
//
//        Assert.assertEquals(new AbstractMap.SimpleImmutableEntry<>(3, "3"), result);
//    }
//
//    @Test
//    public void TEST_30() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//        Map.Entry<Integer, String> result = test.maximumEntry(6);
//
//        Assert.assertEquals(new AbstractMap.SimpleImmutableEntry<>(9, "9"), result);
//    }
//
//    @Test
//    public void TEST_31() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//        Map.Entry<Integer, String> result = test.maximumEntry(24);
//
//        Assert.assertEquals(new AbstractMap.SimpleImmutableEntry<>(45, "45"), result);
//    }
//
//    @Test
//    public void TEST_32() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//        Map.Entry<Integer, String> result = test.predecessorEntry(24);
//
//        Assert.assertEquals(new AbstractMap.SimpleImmutableEntry<>(21, "21"), result);
//    }
//
//    @Test
//    public void TEST_33() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//        Map.Entry<Integer, String> result = test.predecessorEntry(36);
//
//        Assert.assertEquals(new AbstractMap.SimpleImmutableEntry<>(33, "33"), result);
//    }
//
//    @Test
//    public void TEST_34() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//        Map.Entry<Integer, String> result = test.successorEntry(24);
//
//        Assert.assertEquals(new AbstractMap.SimpleImmutableEntry<>(27, "27"), result);
//    }
//
//    @Test
//    public void TEST_35() {
//        TestTreeBinarySearchMap test = createBsTreeMap();
//        Map.Entry<Integer, String> result = test.successorEntry(12);
//
//        Assert.assertEquals(new AbstractMap.SimpleImmutableEntry<>(15, "15"), result);
//    }
//
//    private static class TestTreeBinarySearchMap extends TreeBinarySearchMapBase<Integer, String, TreeNodeDefault<Integer, String>> {
//        protected TestTreeBinarySearchMap(final Comparator<Integer> comparator) {
//            super(comparator, TreeNodeDefault::new);
//        }
//    }
//}
