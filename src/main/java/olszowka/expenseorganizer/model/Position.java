package olszowka.expenseorganizer.model;

import java.time.LocalDate;

public abstract class Position {
    private String name;
    private String value;
    private String category;
    private LocalDate date;

    public Position() {

    }

    public Position(String name, String value, String category) {
        this.name = name;
        this.value = value;
        this.category = category;
        this.date = LocalDate.now();
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate localDate) {
        this.date = localDate;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
