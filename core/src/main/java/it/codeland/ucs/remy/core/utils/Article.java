package it.codeland.ucs.remy.core.utils;

public class Article {
    String title;

    String description;

    String image;

    String date;

    String hashtag;

    String tags;

    String path;

    public Article(String title, String description, String image, String date, String hashtag, String tags, String path) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.date = date;
        this.hashtag = hashtag;
        this.tags = tags;
        this.path = path;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getImage() {
        return this.image;
    }

    public String getDate() {
        return this.date;
    }

    public String getHashtag() {
        return this.hashtag;
    }

    public String getTags() {
        return this.tags;
    }

    public String getPath() {
        return this.path;
    }
}
