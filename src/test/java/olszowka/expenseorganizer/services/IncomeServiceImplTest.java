package olszowka.expenseorganizer.services;

import olszowka.expenseorganizer.model.Income;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IncomeServiceImplTest {

    IncomeService incomeService;

    List<Income> incomes;

    @BeforeEach
    void setUp() {
        incomeService = new IncomeServiceImpl();
        incomes = new ArrayList<>();
        incomes.add(new Income("Paycheck", "2000.00", "Salary", LocalDate.now()));
        incomes.add(new Income("Cashback - shopping", "120.00", "Cashback", LocalDate.now()));
        incomes.add(new Income("Silver investment", "500.00", "Investment", LocalDate.now()));
        incomeService.addAllPositions(incomes);
    }

    @Test
    void getAllPositions() {
        //when
        List<Income> incomesReceived = incomeService.getAllPositions();

        //then
        assertNotNull(incomesReceived);
        assertEquals(this.incomes.size(), incomesReceived.size());
    }

    @Test
    void clearAllPositions() {
        assertEquals(this.incomes.size(), incomeService.getAllPositions().size());

        //when
        incomeService.clearAllPositions();

        //then
        assertEquals(0, incomeService.getAllPositions().size());
    }

    @Test
    void deletePosition() {
        Income income = new Income("Lottery", "10000.00", "Cashback", LocalDate.now());
        incomeService.addPosition(income);
        assertEquals(this.incomes.size()+1, incomeService.getAllPositions().size());
        //when
        incomeService.deletePosition(income);

        //then
        assertEquals(this.incomes.size(), incomeService.getAllPositions().size());
    }

    @Test
    void addPosition() {
        Income income = new Income("Salary 03/22", "2000.00", "Salary", LocalDate.now());
        assertEquals(this.incomes.size(), incomeService.getAllPositions().size());

        //when
        incomeService.addPosition(income);

        //then
        assertEquals(this.incomes.size()+1, incomeService.getAllPositions().size());
    }

    @Test
    void calculateTotalAmount() {
        //given
        double actualSum = 0.0;

        for(int i=0 ; i<this.incomes.size() ; i++) {
            actualSum += Double.parseDouble(incomes.get(i).getValue());
        }

        //when
        String returnedSum = incomeService.calculateTotalAmount();

        //then
        assertEquals(actualSum, Double.parseDouble(returnedSum));
    }
}