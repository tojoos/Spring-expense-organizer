package olszowka.expenseorganizer.services;

import olszowka.expenseorganizer.model.Outcome;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutcomeServiceImpl extends PositionService<Outcome> implements OutcomeService {
    @Override
    public List<Outcome> getAllPositions() {
        return super.getAllPositions();
    }

    @Override
    public void addPosition(Outcome position) {
        super.addPosition(position);
    }

    @Override
    public void deletePosition(Outcome position) {
        super.deletePosition(position);
    }

    @Override
    public void clearAllPositions() {
        super.clearAllPositions();
    }

    @Override
    public String calculateTotalAmount() {
        return super.calculateTotalAmount();
    }
}
