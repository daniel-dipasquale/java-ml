package com.dipasquale.metric;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
final class EmptyValuesMetricDatum extends AbstractMetricDatum {
    @Serial
    private static final long serialVersionUID = 1380744331140358748L;
    private final List<Float> values = new EmptyList(this);
    @Getter(AccessLevel.NONE)
    private int count = 0;
    private Float average = null;

    @Override
    public void add(final float value) {
        int size = count;

        count++;
        appendStatistics(size, value);
        average = sum / (float) count;
    }

    @Override
    public void merge(final MetricDatum other) {
        if (other.getValues().isEmpty()) {
            return;
        }

        int size = count;

        count += other.getValues().size();
        mergeStatistics(size, other);
        average = sum / (float) count;
    }

    @Override
    public MetricDatum createCopy() {
        EmptyValuesMetricDatum metricDatum = new EmptyValuesMetricDatum();

        metricDatum.count = count;
        metricDatum.sum = sum;
        metricDatum.average = average;
        metricDatum.minimum = minimum;
        metricDatum.maximum = maximum;

        return metricDatum;
    }

    @Override
    public MetricDatum createReduced() {
        EmptyValuesMetricDatum metricDatum = new EmptyValuesMetricDatum();

        if (count > 0) {
            metricDatum.count = 1;
            metricDatum.sum = sum;
            metricDatum.average = sum;
            metricDatum.minimum = sum;
            metricDatum.maximum = sum;
        }

        return metricDatum;
    }

    @Override
    public void clear() {
        count = 0;
        super.clear();
        average = null;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class EmptyList implements List<Float>, Serializable {
        @Serial
        private static final long serialVersionUID = -7190256747334040852L;
        private final EmptyValuesMetricDatum metricDatum;

        @Override
        public int size() {
            return metricDatum.count;
        }

        @Override
        public boolean isEmpty() {
            return size() == 0;
        }

        @Override
        public int indexOf(final Object value) {
            int size = size();

            if (size > 0) {
                float valueFixed = (float) value;

                if (Float.compare(metricDatum.minimum, valueFixed) == 0) {
                    return 0;
                }

                if (Float.compare(metricDatum.average, valueFixed) == 0) {
                    return 1;
                }

                if (Float.compare(metricDatum.maximum, valueFixed) == 0) {
                    return size - 1;
                }
            }

            return -1;
        }

        @Override
        public int lastIndexOf(final Object value) {
            int size = size();

            if (size > 0) {
                float valueFixed = (float) value;

                if (Float.compare(metricDatum.maximum, valueFixed) == 0) {
                    return size - 1;
                }

                if (Float.compare(metricDatum.average, valueFixed) == 0) {
                    return 1;
                }

                if (Float.compare(metricDatum.minimum, valueFixed) == 0) {
                    return 0;
                }
            }

            return -1;
        }

        @Override
        public boolean contains(final Object value) {
            return indexOf(value) >= 0;
        }

        @Override
        public Float get(final int index) {
            int size = size();

            if (index < 0 || index >= size) {
                String message = String.format("%d is out of bounds (lower: 0, upper: %d)", index, size);

                throw new IndexOutOfBoundsException(message);
            }

            if (index == 0) {
                return metricDatum.minimum;
            }

            if (index < size - 1) {
                return metricDatum.average;
            }

            return metricDatum.maximum;
        }

        @Override
        public Float set(int index, Float element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(final int index, final Float value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(final Float value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Float remove(final int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(final Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(final Collection<?> values) {
            return false;
        }

        @Override
        public boolean addAll(final Collection<? extends Float> values) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(final int index, final Collection<? extends Float> values) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(final Collection<?> values) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(final Collection<?> values) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        private Stream<Float> createStream(final int offset, final int count) {
            return IntStream.range(offset, count)
                    .mapToObj(this::get);
        }

        @Override
        public Iterator<Float> iterator() {
            return createStream(0, size()).iterator();
        }

        @Override
        public ListIterator<Float> listIterator(final int index) {
            return createStream(0, size())
                    .collect(Collectors.toList())
                    .listIterator(index);
        }

        @Override
        public ListIterator<Float> listIterator() {
            return listIterator(0);
        }

        @Override
        public Object[] toArray() {
            return createStream(0, size()).toArray();
        }

        @Override
        public <TArray> TArray[] toArray(final TArray[] array) {
            return createStream(0, size())
                    .collect(Collectors.toList())
                    .toArray(array);
        }

        @Override
        public List<Float> subList(final int fromIndex, final int toIndex) {
            return createStream(fromIndex, toIndex - fromIndex)
                    .collect(Collectors.toList());
        }
    }
}
