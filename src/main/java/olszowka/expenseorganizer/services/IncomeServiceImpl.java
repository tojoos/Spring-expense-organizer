package olszowka.expenseorganizer.services;

import olszowka.expenseorganizer.model.Income;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncomeServiceImpl extends PositionService<Income> implements IncomeService {
    @Override
    public List<Income> getAllPositions() {
        return super.getAllPositions();
    }

    @Override
    public void clearAllPositions() {
        super.clearAllPositions();
    }

    @Override
    public void deletePosition(Income position) {
        super.deletePosition(position);
    }

    @Override
    public void addPosition(Income position) {
        super.addPosition(position);
    }

    @Override
    public String calculateTotalAmount() {
        return super.calculateTotalAmount();
    }
}
