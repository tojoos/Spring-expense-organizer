package olszowka.expenseorganizer.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class OutcomeTest {

    Outcome outcome;

    String category = "Food";
    LocalDate someDate = LocalDate.of(2012, 12, 12);

    @BeforeEach
    void setUp() {
        outcome = new Outcome("name", "50.00", category, someDate);
    }

    @Test
    void testGettersAndSetters() {
        String newName = "some name";
        String newValue = "some value";
        LocalDate newDate = LocalDate.now();

        assertEquals(category, outcome.getCategory());
        assertEquals(someDate, outcome.getDate());
        outcome.setValue(newValue);
        outcome.setName(newName);
        outcome.setDate(newDate);
        assertEquals(newDate, outcome.getDate());
        assertEquals(newName, outcome.getName());
        assertEquals(newValue, outcome.getValue());
    }
}