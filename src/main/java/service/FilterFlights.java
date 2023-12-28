package service;

import com.gridnine.testing.Flight;
import com.gridnine.testing.Segment;
import exception.FlightException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс для создания правил
 */
public class FilterFlights {
    private final List<Flight> flights;

    public FilterFlights(List<Flight> flights) {
        this.flights = flights;
    }

    /**
     * метод получает список уже совершенных рейсов
     * @return список перелетов
     */
    public List<Flight> pastFlights() {
        LocalDate now = LocalDateTime.now().toLocalDate();
        List<Flight> result = flights.stream()
                .filter(flight -> flight.getSegments().stream()
                        .anyMatch(segment -> segment.getDepartureDate().toLocalDate().isBefore(now)))
                .collect(Collectors.toList());
        if (result.isEmpty()) {
            throw new FlightException("Список перелетов за прошлый период отсутствует");
        }
        return result;

    }

    /**
     * метод получает список рейсов, у которых время прилеты раньше времени вылета (разницы в часовых поясах)
     * @return списко перелетов
     */
    public List<Flight> timeZone() {
        List<Flight> result = flights.stream()
                .filter(flight -> flight.getSegments().stream()
                        .anyMatch(segment -> segment.getArrivalDate().isBefore(segment.getDepartureDate())))
                .collect(Collectors.toList());
        if (result.isEmpty()) {
            throw new FlightException("Список перелетов за прошлый период отсутствует");
        }
        return result;
    }

    /**
     * метод получает список перелетов с длительной пересадокой (более 2-х часов)
     * @return списко перелетов
     */

    public List<Flight> longTransfer() {
        List<Flight> newList = new ArrayList<>();
        for (Flight flight : flights) {
            if (flight.getSegments().size() > 1) {
                List<Segment> segments = flight.getSegments();
                for (int i = 0; i < segments.size() - 1; i++) {
                    int a = flight.getSegments().get(i).getArrivalDate().getHour();
                    int b = flight.getSegments().get(i + 1).getDepartureDate().getHour();
                    if (b - a > 2) {
                        newList.add(flight);
                        break;
                    }
                }
            }
        }
        if (newList.isEmpty()) {
            throw new FlightException("Перелеты с длительной пересадкой отсутствуют");
        }
        return newList;
    }

    /**
     * метод получает список перелетов на определенный день
     * @param date календарный день
     * @return список перелетов
     */
    public List<Flight> thisDay(LocalDate date) {
        List<Flight> result = flights.stream().filter(flight -> flight.getSegments().stream()
                        .findFirst()
                        .map(segment -> segment.getDepartureDate().toLocalDate().isEqual(date))
                        .orElse(false))
                .collect(Collectors.toList());
        if (result.isEmpty()) {
            throw new FlightException("Список рейсов на этот день отсутствует");
        }
        return result;
    }

    /**
     *
     * @param quantity задает кол-во пересадок, где 0 - без пересадок, 1 - одна пересадка, 2 - две пересадки,
     * 3 - три пересадки и более;
     * @return список перелетов по запросу
     */
    public List<Flight> kindOfFlight (int quantity) {
        List<Flight> result;

        switch (quantity) {
            case 0:
                result = filterFlightsBySegmentSize(flights, 1);
                break;
            case 1:
                result = filterFlightsBySegmentSize(flights, 2);
                break;
            case 2:
                result = filterFlightsBySegmentSize(flights, 3);
                break;
            case 3:
                result = filterFlightsBySegmentSize(flights, 4);
                break;
            default:
                result = flights.stream()
                        .filter(flight -> flight.getSegments().size() > 4)
                        .collect(Collectors.toList());
        }

        if (result.isEmpty()) {
            throw new FlightException("Перелет не найден для количества сегментов: " + quantity);
        }

        return result;
    }

    private List<Flight> filterFlightsBySegmentSize(List<Flight> flights, int segmentSize) {
        return flights.stream()
                .filter(flight -> flight.getSegments().size() == segmentSize)
                .collect(Collectors.toList());
    }

}
