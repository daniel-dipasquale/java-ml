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
final class EmptyValuesMetricDatum implements MetricDatum, Serializable {
    @Serial
    private static final long serialVersionUID = 1380744331140358748L;
    @Getter
    private final List<Float> values = new EmptyList(this);
    private int count = 0;
    @Getter
    private float sum = 0f;
    @Getter
    private Float average = null;
    @Getter
    private Float minimum = null;
    @Getter
    private Float maximum = null;

    private static float ensureNotNull(final Float value) {
        if (value == null) {
            return 0f;
        }

        return value;
    }

    @Override
    public void add(final float value) {
        int size = count;

        count++;
        sum += value;
        average = sum / (float) count;

        if (size == 0) {
            minimum = value;
            maximum = value;
        } else {
            minimum = Math.min(ensureNotNull(minimum), value);
            maximum = Math.max(ensureNotNull(maximum), value);
        }
    }

    @Override
    public void merge(final MetricDatum other) {
        count += other.getValues().size();
        sum += other.getSum();
        average = sum / (float) count;

        if (!other.getValues().isEmpty()) {
            minimum = Math.min(ensureNotNull(minimum), other.getMinimum());
            maximum = Math.max(ensureNotNull(maximum), other.getMaximum());
        }
    }

    @Override
    public MetricDatum createCopy() {
        EmptyValuesMetricDatum copy = new EmptyValuesMetricDatum();

        copy.count = count;
        copy.sum = sum;
        copy.average = average;
        copy.minimum = minimum;
        copy.maximum = maximum;

        return copy;
    }

    @Override
    public void clear() {
        count = 0;
        sum = 0f;
        average = null;
        minimum = null;
        maximum = null;
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
