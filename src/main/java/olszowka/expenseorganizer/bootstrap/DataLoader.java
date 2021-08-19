package olszowka.expenseorganizer.bootstrap;

import olszowka.expenseorganizer.model.Income;
import olszowka.expenseorganizer.model.Outcome;
import olszowka.expenseorganizer.services.IncomeService;
import olszowka.expenseorganizer.services.OutcomeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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
        Income income1 = new Income("stypendium", 600.00);
        incomeService.addPosition(income1);

        Income income2 = new Income("praca", 120.00);
        incomeService.addPosition(income2);

        Outcome outcome1 = new Outcome("Pizza", 23.00);
        outcomeService.addPosition(outcome1);

        Outcome outcome2 = new Outcome("Kebab", 12.50);
        outcomeService.addPosition(outcome2);
    }
}
