package olszowka.expenseorganizer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public abstract class Position {
    private String name;
    private String value;
    private String category;
    private LocalDate date;

    public Position(String name, String value, String category) {
        this.name = name;
        this.value = value;
        this.category = category;
        this.date = LocalDate.now();
    }

    public Position(String name, String value, String category, LocalDate date) {
        this.name = name;
        this.value = value;
        this.category = category;
        this.date = date;
    }
}
