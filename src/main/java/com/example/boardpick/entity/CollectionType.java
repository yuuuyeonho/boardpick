package com.example.boardpick.entity;

public enum CollectionType {
    PERSONAL("개인"),
    CAFE("카페");

    private final String label;

    CollectionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
