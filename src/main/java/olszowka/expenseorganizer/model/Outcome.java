package olszowka.expenseorganizer.model;

import java.time.LocalDate;

public class Outcome extends Position {

    public Outcome(String name, String value, String category) {
        super(name, value, category);
    }

    public Outcome(String name, String value, String category, LocalDate date) {
        super(name, value, category, date);
    }
}
