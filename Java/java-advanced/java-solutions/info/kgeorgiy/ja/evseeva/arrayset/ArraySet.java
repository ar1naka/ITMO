package info.kgeorgiy.ja.evseeva.arrayset;

import java.util.*;

public class ArraySet<E> extends AbstractSet<E> implements SortedSet<E> {
    private final List<E> list;
    private final Comparator<E> comparator;

    public ArraySet(final Collection<E> collection, Comparator<E> comparator) {
        Set<E> treeSet = new TreeSet<>(comparator);
        treeSet.addAll(collection);
        list = new ArrayList<>(treeSet);
        this.comparator = comparator;
    }

    public ArraySet(final Collection<E> collection) {
        this(collection, null);
    }

    public ArraySet() {
        this(new ArrayList<>(), null);
    }

    public ArraySet(Comparator<E> comparator) {
        this(new ArrayList<>(), comparator);
    }
    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    @SuppressWarnings("unchecked")
    public SortedSet<E> subSet(E fromElement, E toElement) {
        Comparator<E> c = comparator;
        if (comparator == null) {
            c = (Comparator<E>) Comparator.naturalOrder();
        }
        if (c.compare(fromElement, toElement) > 0) {
            throw new IllegalArgumentException("FromElement must be less or equals then toElement!");
        }
        return new ArraySet<>(list.subList(find(fromElement),
                find(toElement)), comparator);
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        return new ArraySet<>(list.subList(0, find(toElement)), comparator);
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return new ArraySet<>(list.subList(find(fromElement), size()), comparator);
    }

    @Override
    public E first() {
        if (size() == 0) {
            throw new NoSuchElementException("Error with the first element because the set is empty!");
        }
        return list.get(0);
    }

    @Override
    public E last() {
        if (size() == 0) {
            throw new NoSuchElementException("Error with the last element because the set is empty!!");
        }
        return list.get(size() - 1);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object elem) {
        return Collections.binarySearch(list, (E) elem, comparator) >= 0;
    }

    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }

    private int find(E x){
        int tmp = Collections.binarySearch(list, x, comparator);
        if (tmp < 0) {
            return Math.abs(tmp) - 1;
        }
        return tmp;
    }
}
