package com.ruitech.bookstudy.binder;

import androidx.annotation.NonNull;

public interface IUISame<T> {
    boolean areUISame(@NonNull T oldItem, @NonNull T newItem);
    default Object getChangePayload(@NonNull T oldItem, @NonNull T newItem) {
        return null;
    }
}
