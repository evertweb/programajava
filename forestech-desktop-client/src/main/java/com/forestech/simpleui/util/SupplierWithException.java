package com.forestech.simpleui.util;

/**
 * SupplierWithException
 * Functional interface that allows throwing exceptions.
 */
@FunctionalInterface
public interface SupplierWithException<T> {
    T get() throws Exception;
}
