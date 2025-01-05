package com.example.webcrawler.url;

public class UrlEnum {
    public enum Type {
        TEXT("text"),
        HTML("html");

        private final String type;

        Type(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}
