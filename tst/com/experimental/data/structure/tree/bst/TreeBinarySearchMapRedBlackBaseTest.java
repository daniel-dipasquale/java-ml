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
//public final class TreeBinarySearchMapRedBlackBaseTest {
//    private static TreeBinarySearchMapRedBlack createBsTreeMap() {
//        TreeBinarySearchMapRedBlack map = new TreeBinarySearchMapRedBlack(Integer::compareTo);
//
//        map.put(3, "3");
//        map.put(6, "6");
//        map.put(9, "9");
//        map.put(12, "12");
//        map.put(15, "15");
//        map.put(18, "18");
//        map.put(21, "21");
//        map.put(24, "24");
//        map.put(27, "27");
//        map.put(30, "30");
//        map.put(33, "33");
//        map.put(36, "36");
//        map.put(39, "39");
//        map.put(42, "42");
//        map.put(45, "45");
//
//        return map;
//    }
//
//    @Test
//    public void TEST_1() {
//        TreeBinarySearchMapRedBlack test = createBsTreeMap();
//        List<Map.Entry<Integer, String>> result = ImmutableList.copyOf(test.iterator(false));
//
//        Assertions.assertEquals(ImmutableList.<Map.Entry<Integer, String>>builder()
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
//    private static class TreeBinarySearchMapRedBlack extends TreeBinarySearchMapRedBlackBase<Integer, String, TreeNodeRedBlackDefault<Integer, String>> {
//        protected TreeBinarySearchMapRedBlack(final Comparator<Integer> comparator) {
//            super(comparator, new TreeStateDefault<>(), (k, v) -> new TreeNodeRedBlackDefault<>(new TreeNodeDefault<>(k, v)));
//        }
//    }
//}
