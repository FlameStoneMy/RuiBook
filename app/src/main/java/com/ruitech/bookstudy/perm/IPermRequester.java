package com.ruitech.bookstudy.perm;

public interface IPermRequester {
    void onPermRequest(final String permission, Runnable runnable);
}
