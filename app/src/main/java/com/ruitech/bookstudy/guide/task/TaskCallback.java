package com.ruitech.bookstudy.guide.task;

import com.ruitech.bookstudy.utils.NetworkResponse;

public interface TaskCallback extends TaskLoadingCallback {
    void onLoaded(NetworkResponse networkResponse);
}
