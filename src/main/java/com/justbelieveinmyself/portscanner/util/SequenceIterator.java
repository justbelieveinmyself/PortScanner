package com.justbelieveinmyself.portscanner.util;

import java.util.Iterator;

/**
 * Соединяет итераторы вместе
 */
public class SequenceIterator<E> implements Iterator<E> {
    private Iterator<E>[] iterators;
    int currentIndex = 0;

    public SequenceIterator(Iterator<E>... iterators) {
        this.iterators = iterators;

        //проверка на то, что последний итератор не пуст
        if (!iterators[iterators.length-1].hasNext())
            throw new IllegalArgumentException();
    }

    @Override
    public boolean hasNext() {
        //пока последний итератор имеет следующего
        return iterators[iterators.length-1].hasNext();
    }

    @Override
    public E next() {
        if (!iterators[currentIndex].hasNext())
            currentIndex++;

        return iterators[currentIndex].next();
    }

    @Override
    public void remove() {
        iterators[currentIndex].remove();
    }

}
