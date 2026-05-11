package Models;

public class SelectOption {

    private int id;
    private String name;

    public SelectOption() {
    }

    public SelectOption(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static SelectOption empty(String text) {
        return new SelectOption(0, text);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

@Override
    public String toString() {
        return name;
    }
}