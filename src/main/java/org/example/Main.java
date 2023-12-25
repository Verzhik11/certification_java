package org.example;

import com.gridnine.testing.Flight;
import com.gridnine.testing.FlightBuilder;
import service.FilterByRules;
import service.FilterFlights;

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
    }
}