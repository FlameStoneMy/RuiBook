package com.ruitech.bookstudy.utils;

/**
 * This checks whether Url sent to OkHttp is valid:
 * 1. url is not null.
 * 2. url is valid if scheme is http/https.
 * They throw NullPointerException & IllegalArgumentException respectively,
 * either of them is RuntimeException, Unchecked Exception.
 * We want callers intentionally handle invalid url case, so we convert them to Checked Exception,
 * namely this.
 */
public class UrlInvalidException extends Exception {
    public UrlInvalidException(String name) {
        super(name);
    }
}
