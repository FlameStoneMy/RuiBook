package com.ruitech.bookstudy.book;

import com.ruitech.bookstudy.bean.Book;

public class BookQueryEvent {
    public final Book book;
    public final State state;
    public final BookQueryTask.FromType fromType;

    public enum State {
        START,
        SUCC,
        FAIL
    }

    public BookQueryEvent(Book book, State state, BookQueryTask.FromType fromType) {
        this.book = book;
        this.state = state;
        this.fromType = fromType;
    }
}
