package com.github.amjadnas.sqldbmanager.utills;

/**
 * data container used to hold key value pairs
 * @param <F> first value
 * @param <S> second value
 */
public class Pair<F, S> {

    public final  F first;
    public final  S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }
}
