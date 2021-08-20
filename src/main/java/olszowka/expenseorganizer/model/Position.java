package olszowka.expenseorganizer.model;

public abstract class Position {
    private String name;
    private double value;
    private String category;

    public Position() {

    }

    public Position(String name, double value, String category) {
        this.name = name;
        this.value = value;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
