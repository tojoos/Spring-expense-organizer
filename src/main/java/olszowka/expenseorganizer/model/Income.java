package olszowka.expenseorganizer.model;

import java.time.LocalDate;

public class Income extends Position {

    public Income(String name, String value, String category) {
        super(name, value, category);
    }

    public Income(String name, String value, String category, LocalDate date) {
        super(name, value, category, date);
    }
}

