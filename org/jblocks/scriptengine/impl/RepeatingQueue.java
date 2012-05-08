package org.jblocks.scriptengine.impl;

import java.util.Arrays;

/**
 * This is just for speed. <br />
 * (should be little bit faster than the java.util Queues...) <br />
 * 
 * @author ZeroLuck
 */
@SuppressWarnings("unchecked")
class RepeatingQueue<E> {

    private E[] elements;
    private int index;
    private int size;

    public RepeatingQueue() {
        elements = (E[]) new Object[1000];
    }

    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            return (T[]) Arrays.copyOf(elements, size, a.getClass());
        }
        System.arraycopy(elements, 0, a, 0, size);
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    public E peek() {
        if (index >= size) {
            index = 0;
            if (index == size) {
                return null;
            }
        }
        return elements[index++];
    }

    public void add(E e) {
        if ((size + 1) >= elements.length) {
            elements = Arrays.copyOf(elements, size * 2);
        }
        elements[size++] = e;
    }

    public void remove() {
        if (size == index) {
            elements[index - 1] = null;
            size--;
        } else {
            E last = elements[size - 1];
            elements[index - 1] = last;
            size--;
        }
    }
}
