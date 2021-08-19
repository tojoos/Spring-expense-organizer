package olszowka.expenseorganizer.services;

import olszowka.expenseorganizer.model.Position;

import java.util.Set;

public interface CrudService<T extends Position> {
    Set<T> getAllPositions();

    void addPosition(T position);

    void deletePosition(T position);

    void clearAllPositions();

    double calculateTotalAmount();
}
