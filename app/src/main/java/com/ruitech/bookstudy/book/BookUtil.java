package com.ruitech.bookstudy.book;

import android.net.Uri;

import com.ruitech.bookstudy.App;

import java.io.File;

public class BookUtil {

    // files/books/
    public static File getBooksDir() {
        return new File(App.applicationContext().getExternalFilesDir(".nomedia"), "books");
    }

    // files/books/31/
    public static File getBookIdDir(String bookId) {
        return new File(getBooksDir(), bookId);
    }

    // files/books/31/RJYY1-1B.zip
    public static File getBookZipFile(String bookId, String bookGenuineId) {
        return new File(getBooksDir(), bookId + "/" + bookGenuineId + ".zip");
    }

    // files/books/31/RJYY1-1B/
    public static File getBookDir(String bookId) {
        return new File(getBooksDir(), bookId);
    }

    private static final String FILE_PICTURE = "pic.jpg";
    private static final String FILE_TRANSLATION = "Char.txt";
    private static final String FILE_COORDINATE = "XY.txt";
    private static final String FILE_DIR_LIST = "DirList.txt";
    // files/books/RJYY1-1B/3/Char.txt
    public static File getTranslationFile(String bookId, String bookGenuineId, int pageNum) {
        return new File(getBooksDir(), bookId + "/" + bookGenuineId + "/" + pageNum + "/" + FILE_TRANSLATION);
    }

    // files/books/RJYY1-1B/3/XY.txt
    public static File getCoordinateFile(String bookId, String bookGenuineId, int pageNum) {
        return new File(getBooksDir(), bookId + "/" + bookGenuineId + "/" + pageNum + "/" + FILE_COORDINATE);
    }

    // files/books/RJYY1-1B/3/pic.jpg
    private static File getPictureFile(String bookId, String bookGenuineId, int pageNum) {
        return new File(getBooksDir(), bookId + "/" + bookGenuineId + "/" + pageNum + "/" + FILE_PICTURE);
    }

    public static String getPictureUriStr(String bookId, String bookGenuineId, int pageNum) {
        return Uri.fromFile(getPictureFile(bookId, bookGenuineId, pageNum)).toString();
    }

    public static File getDirListFile(String bookId, String bookGenuineId) {
        return new File(getBooksDir(), bookId + "/" + bookGenuineId + "/" + FILE_DIR_LIST);
    }

    // files/books/RJYY1-1B/3/3.mp3
    private static File getMusicFile(String bookId, String bookGenuineId, int pageNum, int musicNum) {
        return new File(getBooksDir(), bookId + "/" + bookGenuineId + "/" + pageNum + "/" + (musicNum + 1) + ".mp3");
    }

    public static Uri getMusicUri(String bookId, String bookGenuineId, int pageNum, int musicNum) {
        return Uri.fromFile(getMusicFile(bookId, bookGenuineId, pageNum, musicNum));
    }
}
