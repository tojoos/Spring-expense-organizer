package olszowka.expenseorganizer.model;

public enum Timeframe {
    DAY(0), WEEK(1), MONTH(2), ALL(3);

    private final int idx;

    private Timeframe(int idx) {
        this.idx = idx;
    }

    public int getIdx() {
        return idx;
    }
}
