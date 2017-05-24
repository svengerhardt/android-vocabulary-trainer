package de.trilobytese.vocab.model;

public class Category {

    public static final int PRIMARY_CATEGORY_ID = 1;

    private long id;
    private String name;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
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

    @Override
    public boolean equals(Object o) {

        if (o == null)
            return false;

        if (o == this)
            return true;

        if (!o.getClass().equals(getClass()))
            return false;

        Category that = (Category)o;
        return id == that.id && (name == that.name || (name != null && name.equals(that.name)));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (int)(id^(id>>>32));
        hash = 31 * hash + (null == name ? 0 : name.hashCode());
        return hash;
    }
}
