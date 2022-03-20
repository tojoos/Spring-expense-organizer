package olszowka.expenseorganizer.services;

import olszowka.expenseorganizer.model.Position;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class PositionService<T extends Position> {
    protected List<T> positions = new ArrayList<>();

    public List<T> getAllPositions() {
        return positions;
    }

    void addPosition(T position) {
        positions.add(position);
    }

    void deletePosition(T position) {
        positions.remove(position);
    }

    void clearAllPositions() {
        positions.clear();
    }

    String calculateTotalAmount() {
        double sum = 0;
        for(T o : getAllPositions()) {
            sum += Double.parseDouble(o.getValue());
        }
        return (new DecimalFormat("###.##").format(sum).replace(",", "."));
    }
}
