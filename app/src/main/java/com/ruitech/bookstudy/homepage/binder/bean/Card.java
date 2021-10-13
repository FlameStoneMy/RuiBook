package com.ruitech.bookstudy.homepage.binder.bean;

import com.ruitech.bookstudy.homepage.CardType;

public abstract class Card {
    private CardType type;

    public Card(CardType cardType) {
        type = cardType;
    }

    public CardType getType() {
        return type;
    }
}
