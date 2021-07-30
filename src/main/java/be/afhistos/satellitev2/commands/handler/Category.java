package be.afhistos.satellitev2.commands.handler;

public class Category {
    String name;

    public Category(String name) {
        this.name = name;
    }

    public Category() {
        this("Autres");
    }

    public String getName() {
        return name;
    }
}
