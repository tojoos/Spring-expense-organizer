package olszowka.expenseorganizer.services;

import olszowka.expenseorganizer.model.Position;
import olszowka.expenseorganizer.model.Timeframe;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalculationService {

    public <T extends Position> List<T> getPeriodicPositions(List<T> positionList, Timeframe timeframe) {
        List<T> positionsPeriodically = new ArrayList<>();

        LocalDate tillDate = getDateForPeriod(timeframe, false);

        for(T p : positionList) {
            if(p.getDate().isAfter(tillDate) || p.getDate().isEqual(tillDate)) {
                positionsPeriodically.add(p);
            }
        }

        return positionsPeriodically;
    }

    public <T extends Position> List<T> getPeriodicPositionsRange(List<T> positionList, Timeframe timeframe) {
        List<T> positionsPeriodically = new ArrayList<>();

        LocalDate tillDate = getDateForPeriod(timeframe, false);
        LocalDate tillDatePrevious = getDateForPeriod(timeframe, true);

        for(T p : positionList) {
            if((p.getDate().isAfter(tillDatePrevious) || p.getDate().isEqual(tillDatePrevious)) && p.getDate().isBefore(tillDate)) {
                positionsPeriodically.add(p);
            }
        }

        return positionsPeriodically;
    }

    private LocalDate getDateForPeriod(Timeframe timeframe, boolean getPrevious) {
        int previousPeriodMultiplier = getPrevious ? 2 : 1;

        if(timeframe == Timeframe.DAY) {
            return LocalDate.now().minusDays(previousPeriodMultiplier);
        } else if (timeframe == Timeframe.WEEK) {
            return LocalDate.now().minusWeeks(previousPeriodMultiplier);
        } else if (timeframe == Timeframe.MONTH) {
            return LocalDate.now().minusMonths(previousPeriodMultiplier);
        } else {
            return LocalDate.EPOCH;
        }
    }

    public String returnTimeframeString(Timeframe timeframe) {
        String timeframeString;
        LocalDate dateToFormat = getDateForPeriod(timeframe, false);
        timeframeString = dateToFormat.equals(LocalDate.EPOCH) ? "01-01-2022" : dateToFormat.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        timeframeString = timeframeString.concat(" - " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        return timeframeString;
    }
}
