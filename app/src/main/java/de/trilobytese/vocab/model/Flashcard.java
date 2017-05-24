package de.trilobytese.vocab.model;

public class Flashcard {

    private long id;
    private String source;
    private String target;
    private long deckId;

    public Flashcard() {
    }

    public Flashcard(String source, String target, long deckId) {
        this.source = source;
        this.target = target;
        this.deckId = deckId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public long getDeckId() {
        return deckId;
    }

    public void setDeckId(long deckId) {
        this.deckId = deckId;
    }

    @Override
    public boolean equals(Object o) {

        if (o == null)
            return false;

        if (o == this)
            return true;

        if (!o.getClass().equals(getClass()))
            return false;

        Flashcard that = (Flashcard)o;
        return id == that.id && (source == that.source || (source != null && source.equals(that.source)));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (int)(id^(id>>>32));
        hash = 31 * hash + (null == source ? 0 : source.hashCode());
        return hash;
    }
}
