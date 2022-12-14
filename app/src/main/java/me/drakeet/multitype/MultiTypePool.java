/*
 * Copyright 2016 drakeet. https://github.com/drakeet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.drakeet.multitype;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * An List implementation of TypePool.
 *
 * @author drakeet
 */
public class MultiTypePool implements TypePool {

    @NonNull
    private final List<Class<?>> classes;
    @NonNull
    private final List<ItemViewBinder<?, ?>> binders;
    @NonNull
    private final List<Linker<?>> linkers;


    /**
     * Constructs a MultiTypePool with default lists.
     */
    public MultiTypePool() {
        this.classes = new ArrayList<>();
        this.binders = new ArrayList<>();
        this.linkers = new ArrayList<>();
    }


    /**
     * Constructs a MultiTypePool with default lists and a specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the list
     */
    public MultiTypePool(int initialCapacity) {
        this.classes = new ArrayList<>(initialCapacity);
        this.binders = new ArrayList<>(initialCapacity);
        this.linkers = new ArrayList<>(initialCapacity);
    }


    /**
     * Constructs a MultiTypePool with specified lists.
     *
     * @param classes the list for classes
     * @param binders the list for binders
     * @param linkers the list for linkers
     */
    public MultiTypePool(
        @NonNull List<Class<?>> classes,
        @NonNull List<ItemViewBinder<?, ?>> binders,
        @NonNull List<Linker<?>> linkers) {
        this.classes = classes;
        this.binders = binders;
        this.linkers = linkers;
    }


    @Override
    public <T> void register(
        @NonNull Class<? extends T> clazz,
        @NonNull ItemViewBinder<T, ?> binder,
        @NonNull Linker<T> linker) {
        classes.add(clazz);
        binders.add(binder);
        linkers.add(linker);
    }


    @Override
    public int firstIndexOf(@NonNull final Class<?> clazz) {
        int index = classes.indexOf(clazz);
        if (index != -1) {
            return index;
        }
        for (int i = 0; i < classes.size(); i++) {
            if (classes.get(i).isAssignableFrom(clazz)) {
                return i;
            }
        }
        return -1;
    }


    @NonNull
    @Override
    public List<Class<?>> getClasses() {
        return classes;
    }


    @NonNull
    @Override
    public List<ItemViewBinder<?, ?>> getItemViewBinders() {
        return binders;
    }


    @NonNull
    public List<Linker<?>> getLinkers() {
        return linkers;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("class: ");
        for (Class<?> one : classes) {
            stringBuilder.append(one.getSimpleName());
            stringBuilder.append(",");
        }

        stringBuilder.append("\nbinder: ");

        for (ItemViewBinder<?, ?> binder : binders) {
            stringBuilder.append(binder.getClass().getSimpleName());
            stringBuilder.append(",");
        }
        stringBuilder.append("\n");

        return stringBuilder.toString();
    }
}
