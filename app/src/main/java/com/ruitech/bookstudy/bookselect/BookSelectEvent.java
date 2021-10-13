package com.ruitech.bookstudy.bookselect;

import com.ruitech.bookstudy.bean.Book;
import com.ruitech.bookstudy.bean.Grade;
import com.ruitech.bookstudy.bean.Subject;

public class BookSelectEvent {
    public final Subject subject;
    public final Grade grade;
    public final Book book;

    public BookSelectEvent(Subject subject, Grade grade, Book book) {
        this.subject = subject;
        this.grade = grade;
        this.book = book;
    }
}
