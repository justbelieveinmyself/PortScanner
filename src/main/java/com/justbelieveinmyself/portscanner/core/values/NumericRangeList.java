package com.justbelieveinmyself.portscanner.core.values;

import java.util.Collection;

/**
 * Создает человекочитаемое представление элементов коллекции
 */
public class NumericRangeList implements Comparable<NumericRangeList> {
    private boolean displayAsRanges;
    private int[] numbers;

    public NumericRangeList(Collection<Integer> numbers, boolean displayAsRanges) {
        this.numbers = new int[numbers.size()];
        int c = 0;

        for (Number n : numbers) {
            this.numbers[c++] = n.intValue();
        }
        this.displayAsRanges = displayAsRanges;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        int prevNumber = Integer.MAX_VALUE;
        int rangeStartNumber = 0;
        boolean isRange = false;
        int i = 0;

        if (numbers.length > 0) {
            prevNumber = numbers[0];
            sb.append(prevNumber);
        }

        while (++i < numbers.length) {
            int curNumber = numbers[i];

            if (displayAsRanges && prevNumber + 1 == curNumber) {
                if (!isRange) {
                    isRange = true;
                    rangeStartNumber = prevNumber;
                }
            } else {
                if (isRange) {
                    sb.append(rangeStartNumber + 1 == prevNumber ? ',' : '-').append(prevNumber);
                }
                sb.append(',').append(curNumber);
            }
            prevNumber = curNumber;
        }
        if (isRange) {
            sb.append(rangeStartNumber + 1 == prevNumber ? ',' : '-').append(prevNumber);
        }

        return sb.toString();

    }

    @Override
    public int compareTo(NumericRangeList o) {
        int result = this.numbers.length - o.numbers.length;

        if (result == 0) {
            for (int i = 0; i < numbers.length && result == 0; i++) {
                result = numbers[i] - o.numbers[i];
            }
        }

        return result;
    }

}
