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

    }
}
