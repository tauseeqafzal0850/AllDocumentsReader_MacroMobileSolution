package com.example.alldocumentsreaderimagescanner.converter.interfaces

interface IConsumer<T> {
    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    fun accept(t: T)
}