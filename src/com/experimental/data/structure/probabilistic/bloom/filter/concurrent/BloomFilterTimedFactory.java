//package com.experimental.data.structure.probabilistic.bloom.filter.concurrent;
//
//import com.pasqud.common.ExpirySupport;
//import com.pasqud.data.structure.probabilistic.bloom.filter.BloomFilter;
//import com.pasqud.data.structure.probabilistic.bloom.filter.BloomFilterFactory;
//import com.pasqud.data.structure.probabilistic.bloom.filter.BloomFilterPartitionFactory;
//import com.pasqud.data.structure.probabilistic.bloom.filter.concurrent.BloomFilterMultiFactory;
//import com.pasqud.data.structure.probabilistic.MultiFunctionHashing;
//import lombok.RequiredArgsConstructor;
//
//@RequiredArgsConstructor
//public final class BloomFilterFactoryTimed implements BloomFilterFactory {
//    private final MultiFunctionHashing multiFunctionHashing;
//    private final ExpirySupport expirySupport;
//
//    @Override
//    public int getMaximumHashFunctions() {
//        return multiFunctionHashing.getMaximumHashFunctions();
//    }
//
//    @Override
//    public <T> BloomFilter<T> create(final int estimatedSize, final int hashFunctions, final double falsePositiveRatio, final long size) {
//        if (size <= (long) Integer.MAX_VALUE) {
//            PartitionFactory partitionFactory = new PartitionFactory(estimatedSize, hashFunctions, falsePositiveRatio, (int) size);
//
//            return partitionFactory.create(0);
//        }
//
//        double sizeDouble = (double) size;
//        double partitions = Math.ceil(sizeDouble / (double) Integer.MAX_VALUE);
//        int estimatedSizeByPartition = (int) Math.ceil((double) estimatedSize / partitions);
//        int sizeByPartition = (int) Math.ceil(sizeDouble / partitions);
//        BloomFilterPartitionFactory bloomFilterPartitionFactory = BloomFilterPartitionFactory.createProxy(new BloomFilterFactoryTimed())
//        PartitionFactory partitionFactory = new PartitionFactory(estimatedSizeByPartition, hashFunctions, falsePositiveRatio, sizeByPartition);
//        BloomFilterMultiFactory bloomFilterFactory = new BloomFilterMultiFactory(multiFunctionHashing, partitionFactory, (int) partitions);
//
//        return bloomFilterFactory.create(estimatedSizeByPartition, hashFunctions, falsePositiveRatio, sizeByPartition);
//    }
//
//    @RequiredArgsConstructor
//    private final class PartitionFactory implements BloomFilterMultiFactory.PartitionFactory {
//        private final int estimatedSizeByPartition;
//        private final int hashFunctions;
//        private final double falsePositiveRatio;
//        private final int sizeByPartition;
//
//        @Override
//        public <T> BloomFilter<T> create(final int index) {
//            return BloomFilterTimed.create(multiFunctionHashing, sizeByPartition, hashFunctions, expirySupport);
//        }
//    }
//}
