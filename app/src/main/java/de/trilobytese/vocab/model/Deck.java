package de.trilobytese.vocab.model;

public class Deck {

    public long id;
    public String name;
    public long timestampCreated;
    public long timestampModified;
    public boolean starred;
    public long categoryId;

    public Deck() {
    }

    public Deck(String name, long categoryId) {
        this.name = name;
        this.categoryId = categoryId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimestampCreated(long timestamp) {
        this.timestampCreated = timestamp;
    }

    public long getTimestampModified() {
        return timestampModified;
    }

    public void setTimestampModified(long timestamp) {
        this.timestampModified = timestamp;
    }

    public boolean isStarred() {
        return starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public boolean equals(Object o) {

        if (o == null)
            return false;

        if (o == this)
            return true;

        if (!o.getClass().equals(getClass()))
            return false;

        Deck that = (Deck)o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (int)(id^(id>>>32));
        return hash;
    }
}
