package olszowka.expenseorganizer.services;

import olszowka.expenseorganizer.model.Position;

import java.util.HashSet;
import java.util.Set;

public abstract class PositionService<T extends Position> {
    protected Set<T> positions = new HashSet<>();

    Set<T> getAllPositions() {
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
}
