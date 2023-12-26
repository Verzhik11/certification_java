package service;

import com.gridnine.testing.Flight;
import exception.FlightException;

import java.time.LocalDate;
import java.util.List;

/**
 * Класс для комбинирования правил с одним примером
 */

public class FilterByRules {
    private final FilterFlights filterFlights;

    public FilterByRules(FilterFlights filterFlights) {
        this.filterFlights = filterFlights;
    }

    /**
     * Метод комбинирует правила и выводит список перелетов без пересадок на определенную дату
     * @return список
     */
    public List<Flight> todayNoTransit() {
        List<Flight> noTransitFlights = filterFlights.kindOfFlight(0);
        return new FilterFlights(noTransitFlights).thisDay(LocalDate.now());
    }

    /**
     * Метод комбинирует правило для выведения списка перелетов в прошлом, а затем применяет правило
     * длительных пересадок
     * @return списко или исключение при отсутствии перелетов по данным правилам
     */
    public List<Flight> longTransferPast() {
        List<Flight> flights = filterFlights.pastFlights();
        FilterFlights filterFlights1 = new FilterFlights(flights);
        return filterFlights1.longTransfer();
    }

}

