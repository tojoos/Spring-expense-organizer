package olszowka.expenseorganizer.bootstrap;

import olszowka.expenseorganizer.model.Income;
import olszowka.expenseorganizer.model.Outcome;
import olszowka.expenseorganizer.services.IncomeService;
import olszowka.expenseorganizer.services.OutcomeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {
    private final IncomeService incomeService;
    private final OutcomeService outcomeService;

    public DataLoader(IncomeService incomeService, OutcomeService outcomeService) {
        this.incomeService = incomeService;
        this.outcomeService = outcomeService;
    }

    @Override
    public void run(String... args) throws Exception {
        loadData();
    }

    private void loadData() {
        Income income1 = new Income("Scholarship 08/22", "600.00", "Scholarship");
        incomeService.addPosition(income1);

        Income income2 = new Income("Salary 08/21", "120.00", "Primary Job", LocalDate.of(2021,8,4));
        incomeService.addPosition(income2);

        Income income3 = new Income("Salary 07/21", "120.00", "Primary Job", LocalDate.of(2021,7,1));
        incomeService.addPosition(income3);

        Outcome outcome1 = new Outcome("Pizza", "23.00", "Food");
        outcomeService.addPosition(outcome1);

        Outcome outcome2 = new Outcome("Kebab", "12.50", "Food", LocalDate.of(2021,8,4));
        outcomeService.addPosition(outcome2);

        Outcome outcome3 = new Outcome("Mieso", "22.50", "Food", LocalDate.of(2021,7,10));
        outcomeService.addPosition(outcome3);
    }
}
