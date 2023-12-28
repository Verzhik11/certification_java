package org.example;

import com.gridnine.testing.Flight;
import com.gridnine.testing.FlightBuilder;
import com.gridnine.testing.Segment;
import service.FilterByRules;
import service.FilterFlights;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        List<Flight> flights = FlightBuilder.createFlights();
        List<Flight> flights1 = FlightBuilder.createPastAndTodayFlight();
        //System.out.println(flights);
        //System.out.println(FlightBuilder.createPastAndTodayFlight());
        FilterFlights filterFlights = new FilterFlights(flights);
        FilterFlights filterFlights1 = new FilterFlights(flights1);
        //System.out.println(filterFlights.pastFlights());
        //System.out.println(filterFlights.timeZone());
        //System.out.println(filterFlights.kindOfFlight(4));
        //System.out.println(filterFlights1.thisDay(LocalDate.now()));
        FilterByRules filterByRules = new FilterByRules(filterFlights1);
        System.out.println(filterByRules.todayNoTransit());
        //System.out.println(filterByRules.longTransferPast());
        System.out.println(sheetSegments());
    }
    private static List<Flight> sheetSegments() {
        Segment segmentPast = new Segment(LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(3).plusHours(3));
        Segment segmentTimeZone = new Segment(LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(3).minusHours(3));
        Segment usual1 = new Segment(LocalDateTime.now().plusDays(4), LocalDateTime.now().plusDays(4).plusHours(2));
        Segment usual2 = new Segment(LocalDateTime.now().plusDays(4).plusHours(3), LocalDateTime.now().plusDays(4).plusHours(6));
        Segment usual3 = new Segment(LocalDateTime.now().plusDays(4).plusHours(10), LocalDateTime.now().plusDays(4).plusHours(12));
        Segment usual4 = new Segment(LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(5).plusHours(3));
        Segment today = new Segment(LocalDateTime.now(), LocalDateTime.now().plusHours(2));
        Flight past = new Flight(List.of(segmentPast));
        Flight timeZone = new Flight(List.of(segmentTimeZone));
        Flight transfer1 = new Flight(List.of(usual1, usual2));
        Flight transfer2 = new Flight(List.of(usual1, usual2, usual3));
        Flight transfer3Long = new Flight(List.of(usual1, usual2, usual3, usual4));
        Flight todayFlight = new Flight(List.of(today));
        return Arrays.asList(past, timeZone, transfer1, transfer2, transfer3Long, todayFlight);
    }
}