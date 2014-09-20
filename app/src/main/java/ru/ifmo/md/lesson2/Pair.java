package ru.ifmo.md.lesson2;

/**
 * Created by Женя on 21.09.2014.
 */
public class Pair implements Comparable{
    int first, second;
    public Pair(int first, int second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public int compareTo(Object o) {
        assert o instanceof Pair;
        Pair p = (Pair)o;
        if (equals(o))
            return 0;
        if (p.first > first || (p.first == first && p.second > second))
            return 1;
        else
            return -1;
    }
    @Override
    public boolean equals(Object t) {
        assert t instanceof Pair;
        Pair p = (Pair)t;
        return p.first == first && p.second == second;
    }
}
