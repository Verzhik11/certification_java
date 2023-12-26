package service;

import com.gridnine.testing.Flight;
import com.gridnine.testing.Segment;
import exception.FlightException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class FilterFlightsTest {
    private final FilterFlights out = new FilterFlights(sheetSegments());


    @Test
    void pastFlightsTest() {
        Segment segmentPast = new Segment(LocalDateTime.now().minusDays(3).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().minusDays(3).plusHours(3).truncatedTo(ChronoUnit.MINUTES));
        Flight past = new Flight(List.of(segmentPast));
        assertThat(out.pastFlights()).hasSize(1).isNotNull();
        assertThat(out.pastFlights().get(0).getSegments().get(0).getDepartureDate())
                .isEqualTo(segmentPast.getDepartureDate());
        assertThat(out.pastFlights()).isEqualTo(List.of(past));

    }

    @Test
    void whenPastFlightIsAbsend() {
        Segment today = new Segment(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusHours(2).truncatedTo(ChronoUnit.MINUTES));
        Flight todayFlight = new Flight(List.of(today));
        FilterFlights filterFlights = new FilterFlights(List.of(todayFlight));
        assertThatExceptionOfType(FlightException.class)
                .isThrownBy(() -> filterFlights.pastFlights());
    }

    @Test
    void timeZoneTest() {
        Segment segmentTimeZone = new Segment(LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusDays(3).minusHours(3).truncatedTo(ChronoUnit.MINUTES));
        Flight timeZone = new Flight(List.of(segmentTimeZone));
        assertThat(out.timeZone().get(0).getSegments().get(0).getArrivalDate()).isEqualTo(segmentTimeZone.getArrivalDate());
        assertThat(out.timeZone()).hasSize(1).isNotNull().isEqualTo(List.of(timeZone));
    }

    @Test
    void whenTimeZoneFlightIsAbsend() {
        Segment today = new Segment(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusHours(2).truncatedTo(ChronoUnit.MINUTES));
        Flight todayFlight = new Flight(List.of(today));
        FilterFlights filterFlights = new FilterFlights(List.of(todayFlight));
        assertThatExceptionOfType(FlightException.class)
                .isThrownBy(() -> filterFlights.timeZone());
    }

    @Test
    void longTransferTest() {
        Segment usual1 = new Segment(LocalDateTime.now().plusDays(4).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusDays(4).plusHours(2).truncatedTo(ChronoUnit.MINUTES));
        Segment usual2 = new Segment(LocalDateTime.now().plusDays(4).plusHours(3).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusDays(4).plusHours(6).truncatedTo(ChronoUnit.MINUTES));
        Segment usual3 = new Segment(LocalDateTime.now().plusDays(4).plusHours(10).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusDays(4).plusHours(12).truncatedTo(ChronoUnit.MINUTES));
        Segment usual4 = new Segment(LocalDateTime.now().plusDays(5).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusDays(5).plusHours(3).truncatedTo(ChronoUnit.MINUTES));
        Flight transfer3Long = new Flight(List.of(usual1, usual2, usual3, usual4));
        Flight transfer2Long = new Flight(List.of(usual1, usual2, usual3));
        assertThat(out.longTransfer()).hasSize(2).isNotNull().isEqualTo(List.of(transfer2Long, transfer3Long));
        assertThat(out.longTransfer().get(0)).isEqualTo(transfer2Long);
        assertThat(out.longTransfer().get(1).getSegments()).hasSize(4).containsAnyOf(usual1);

    }

    @Test
    void whenLongTransferFlightIsAbsend() {
        Segment today = new Segment(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusHours(2).truncatedTo(ChronoUnit.MINUTES));
        Flight todayFlight = new Flight(List.of(today));
        FilterFlights filterFlights = new FilterFlights(List.of(todayFlight));
        assertThatExceptionOfType(FlightException.class)
                .isThrownBy(() -> filterFlights.longTransfer());
    }

    @Test
    void thisDayTest() {
        Segment today = new Segment(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusHours(2).truncatedTo(ChronoUnit.MINUTES));
        Flight todayFlight = new Flight(List.of(today));
        assertThat(out.pastFlights()).hasSize(1).isNotNull();
        assertThat(out.thisDay(LocalDate.now()).get(0).getSegments().get(0).getDepartureDate())
                .isEqualTo(today.getDepartureDate());
        assertThat(out.thisDay(LocalDate.now())).isEqualTo(List.of(todayFlight));
    }

    @Test
    void whenThisDayFlightIsAbsend() {
        assertThatExceptionOfType(FlightException.class)
                .isThrownBy(() -> out.thisDay(LocalDate.now().minusDays(30)));
    }

    @ParameterizedTest
    @MethodSource("provideParamsForKindOfFlight")
    void kindOfFlightTest(int quantity, List<Flight> expected) {
        assertEquals(expected, out.kindOfFlight(quantity));
    }

    @Test
    void whenKindOfFlightIsAbsend() {
        assertThatExceptionOfType(FlightException.class)
                .isThrownBy(() -> out.kindOfFlight(5));
    }

    public static Stream<Arguments> provideParamsForKindOfFlight() {
        Segment segmentPast = new Segment(LocalDateTime.now().minusDays(3).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().minusDays(3).plusHours(3).truncatedTo(ChronoUnit.MINUTES));
        Segment segmentTimeZone = new Segment(LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusDays(3).minusHours(3).truncatedTo(ChronoUnit.MINUTES));
        Segment usual1 = new Segment(LocalDateTime.now().plusDays(4).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusDays(4).plusHours(2).truncatedTo(ChronoUnit.MINUTES));
        Segment usual2 = new Segment(LocalDateTime.now().plusDays(4).plusHours(3).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusDays(4).plusHours(6).truncatedTo(ChronoUnit.MINUTES));
        Segment usual3 = new Segment(LocalDateTime.now().plusDays(4).plusHours(10).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusDays(4).plusHours(12).truncatedTo(ChronoUnit.MINUTES));
        Segment usual4 = new Segment(LocalDateTime.now().plusDays(5).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusDays(5).plusHours(3).truncatedTo(ChronoUnit.MINUTES));
        Segment today = new Segment(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusHours(2).truncatedTo(ChronoUnit.MINUTES));
        Flight past = new Flight(List.of(segmentPast));
        Flight timeZone = new Flight(List.of(segmentTimeZone));
        Flight transfer1 = new Flight(List.of(usual1, usual2));
        Flight transfer2Long = new Flight(List.of(usual1, usual2, usual3));
        Flight transfer3Long = new Flight(List.of(usual1, usual2, usual3, usual4));
        Flight todayFlight = new Flight(List.of(today));
        return Stream.of(
                Arguments.of(0, List.of(past, timeZone, todayFlight)),
                Arguments.of(1, Collections.singletonList(transfer1)),
                Arguments.of(2, Collections.singletonList(transfer2Long)),
                Arguments.of(3, Collections.singletonList(transfer3Long))
        );
    }


    private List<Flight> sheetSegments() {
        Segment segmentPast = new Segment(LocalDateTime.now().minusDays(3).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().minusDays(3).plusHours(3).truncatedTo(ChronoUnit.MINUTES));
        Segment segmentTimeZone = new Segment(LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusDays(3).minusHours(3).truncatedTo(ChronoUnit.MINUTES));
        Segment usual1 = new Segment(LocalDateTime.now().plusDays(4).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusDays(4).plusHours(2).truncatedTo(ChronoUnit.MINUTES));
        Segment usual2 = new Segment(LocalDateTime.now().plusDays(4).plusHours(3).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusDays(4).plusHours(6).truncatedTo(ChronoUnit.MINUTES));
        Segment usual3 = new Segment(LocalDateTime.now().plusDays(4).plusHours(10).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusDays(4).plusHours(12).truncatedTo(ChronoUnit.MINUTES));
        Segment usual4 = new Segment(LocalDateTime.now().plusDays(5).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusDays(5).plusHours(3).truncatedTo(ChronoUnit.MINUTES));
        Segment today = new Segment(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusHours(2).truncatedTo(ChronoUnit.MINUTES));
        Flight past = new Flight(List.of(segmentPast));
        Flight timeZone = new Flight(List.of(segmentTimeZone));
        Flight transfer1 = new Flight(List.of(usual1, usual2));
        Flight transfer2Long = new Flight(List.of(usual1, usual2, usual3));
        Flight transfer3Long = new Flight(List.of(usual1, usual2, usual3, usual4));
        Flight todayFlight = new Flight(List.of(today));
        return List.of(past, timeZone, transfer1, transfer2Long, transfer3Long, todayFlight);
    }

}