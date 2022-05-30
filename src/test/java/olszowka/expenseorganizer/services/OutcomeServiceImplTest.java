package olszowka.expenseorganizer.services;

import olszowka.expenseorganizer.model.Income;
import olszowka.expenseorganizer.model.Outcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OutcomeServiceImplTest {

    OutcomeService outcomeService;

    List<Outcome> outcomes;

    @BeforeEach
    void setUp() {
        outcomeService = new OutcomeServiceImpl();
        outcomes = new ArrayList<>();
        outcomes.add(new Outcome("Dinner", "80.00", "Food", LocalDate.now()));
        outcomes.add(new Outcome("New socks", "19.99", "Clothes", LocalDate.now()));
        outcomes.add(new Outcome("Silver investment", "500.00", "Investment", LocalDate.now()));
        outcomeService.addAllPositions(outcomes);
    }

    @Test
    void getAllPositions() {
        //when
        List<Outcome> outcomesReceived = outcomeService.getAllPositions();

        //then
        assertNotNull(outcomesReceived);
        assertEquals(this.outcomes.size(), outcomesReceived.size());
    }

    @Test
    void clearAllPositions() {
        assertEquals(this.outcomes.size(), outcomeService.getAllPositions().size());

        //when
        outcomeService.clearAllPositions();

        //then
        assertEquals(0, outcomeService.getAllPositions().size());
    }

    @Test
    void deletePosition() {
        Outcome outcome = new Outcome("Lottery Ticket", "9.50", "Cashback", LocalDate.now());
        outcomeService.addPosition(outcome);
        assertEquals(this.outcomes.size()+1, outcomeService.getAllPositions().size());
        //when
        outcomeService.deletePosition(outcome);

        //then
        assertEquals(this.outcomes.size(), outcomeService.getAllPositions().size());
    }

    @Test
    void addPosition() {
        Outcome outcome = new Outcome("Holidays - 2022", "4000.00", "Holidays", LocalDate.now());
        assertEquals(this.outcomes.size(), outcomeService.getAllPositions().size());

        //when
        outcomeService.addPosition(outcome);

        //then
        assertEquals(this.outcomes.size()+1, outcomeService.getAllPositions().size());
    }

    @Test
    void calculateTotalAmount() {
        //given
        double actualSum = 0.0;

        for(int i=0 ; i<this.outcomes.size() ; i++) {
            actualSum += Double.parseDouble(outcomes.get(i).getValue());
        }

        //when
        String returnedSum = outcomeService.calculateTotalAmount();

        //then
        assertEquals(actualSum, Double.parseDouble(returnedSum));
    }

    @Test
    void addAllPositions() {
        //given
        List<Outcome> outcomesToAdd = new ArrayList<>();
        Outcome outcome1 = new Outcome("sth", "100.00", "Other", LocalDate.now());
        Outcome outcome2 = new Outcome("sth different", "1000.00", "Food", LocalDate.now());
        outcomesToAdd.add(outcome1);
        outcomesToAdd.add(outcome2);

        //when
        outcomeService.addAllPositions(outcomesToAdd);

        //then
        assertEquals(outcomesToAdd.size() + outcomes.size(), outcomeService.getAllPositions().size());
        assertEquals(outcome2.getCategory(), outcomeService.getAllPositions().get(outcomesToAdd.size() + outcomes.size() - 1).getCategory());
    }

    @Test
    void calculateTotalAmountForPositions() {
        //given
        List<Outcome> outcomeToCalculate = new ArrayList<>();
        Outcome outcome1 = new Outcome("sth", "99.99", "Other", LocalDate.now());
        Outcome outcome2 = new Outcome("sth different", "1000.00", "Other", LocalDate.now());
        outcomeToCalculate.add(outcome1);
        outcomeToCalculate.add(outcome2);

        //when
        String calculatedAmount = outcomeService.calculateTotalAmountForPositions(outcomeToCalculate);

        //then
        assertEquals(Double.parseDouble(outcome1.getValue()) + Double.parseDouble(outcome2.getValue()), Double.parseDouble(calculatedAmount));
    }
}