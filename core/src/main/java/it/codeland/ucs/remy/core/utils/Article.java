package it.codeland.ucs.remy.core.utils;

import java.util.Objects;

public class Article {
    String title;

    String description;

    String image;

    String date;

    String hashtag;

    String tags;

    String path;

    String link;

    public Article(String title, String description, String image, String date, String hashtag, String tags, String path) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.date = date;
        this.hashtag = hashtag;
        this.tags = tags;
        this.path = path;
        this.link = path+".html";
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
    
    public String getLink() {
        return this.link;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return Objects.equals(path, article.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

}