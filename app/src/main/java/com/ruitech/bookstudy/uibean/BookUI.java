package com.ruitech.bookstudy.uibean;

import com.ruitech.bookstudy.bean.Book;
import com.ruitech.bookstudy.utils.ListUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BookUI extends SelectedUI<Book> {
    public BookUI(Book Book, boolean selected) {
        super(Book, selected);
    }

//    public static final List<BookUI> from(List<Book> list, Book selectedBook) {
//        List<BookUI> ret = new ArrayList<>();
//        if (!ListUtils.isEmpty(list)) {
//            Iterator<Book> iter = list.iterator();
//            Book book = iter.next();
//            ret.add(new BookUI(book, book.equals(selectedBook)));
//            while (iter.hasNext()) {
//                book = iter.next();
//                ret.add(new BookUI(book, book.equals(selectedBook)));
//            }
//        }
//        return ret;
//    }

    public static final int from(List<Book> fromList, List toList, Book selectedBook) {
        int pos = -1;
        int len = toList.size();
        if (!ListUtils.isEmpty(fromList)) {
            for (int i = 0; i < fromList.size(); i++) {
                Book book = fromList.get(i);
                boolean selected = book.equals(selectedBook);
                if (selected) {
                    pos = i + len;
                }
                toList.add(new BookUI(book, selected));
            }
        }

        return pos;
    }
}
