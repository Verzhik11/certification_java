package service;

import com.gridnine.testing.Flight;
import com.gridnine.testing.Segment;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;

public class FilterByRulesTest {
    private final FilterByRules out = new FilterByRules(new FilterFlights(sheetSegments()));


    @Test
    public void todayNoTransit() {
        Segment today = new Segment(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusHours(2).truncatedTo(ChronoUnit.MINUTES));
        Flight todayFlight = new Flight(List.of(today));
        assertThat(out.todayNoTransit()).hasSize(1).isNotEmpty().containsAnyOf(todayFlight);
        assertThat(out.todayNoTransit().get(0).getSegments()).hasSize(1).isEqualTo(List.of(today));
        assertThat(out.todayNoTransit()).isEqualTo(List.of(todayFlight));
    }

    @Test
    public void longTransferPast() {
        Segment usual5 = new Segment(LocalDateTime.now().minusDays(4).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().minusDays(4).plusHours(3).truncatedTo(ChronoUnit.MINUTES));
        Segment usual6 = new Segment(LocalDateTime.now().minusDays(4).plusHours(10).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().minusDays(4).plusHours(13).truncatedTo(ChronoUnit.MINUTES));
        Flight pastTransferLong = new Flight(List.of(usual5, usual6));
        assertThat(out.longTransferPast()).hasSize(1).isNotEmpty().containsAnyOf(pastTransferLong);
        assertThat(out.longTransferPast().get(0).getSegments()).hasSize(2);
        assertThat(out.longTransferPast()).isEqualTo(List.of(pastTransferLong));
        assertThat(out.longTransferPast().get(0).getSegments().get(1).getArrivalDate()).isEqualTo(usual6.getArrivalDate());

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
        Segment usual5 = new Segment(LocalDateTime.now().minusDays(4).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().minusDays(4).plusHours(3).truncatedTo(ChronoUnit.MINUTES));
        Segment usual6 = new Segment(LocalDateTime.now().minusDays(4).plusHours(10).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().minusDays(4).plusHours(13).truncatedTo(ChronoUnit.MINUTES));
        Segment today = new Segment(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusHours(2).truncatedTo(ChronoUnit.MINUTES));
        Flight past = new Flight(List.of(segmentPast));
        Flight timeZone = new Flight(List.of(segmentTimeZone));
        Flight transfer1 = new Flight(List.of(usual1, usual2));
        Flight transfer2Long = new Flight(List.of(usual1, usual2, usual3));
        Flight transfer3Long = new Flight(List.of(usual1, usual2, usual3, usual4));
        Flight todayFlight = new Flight(List.of(today));
        Flight pastTransferLong = new Flight(List.of(usual5, usual6));
        return List.of(past, timeZone, transfer1, transfer2Long, transfer3Long, todayFlight, pastTransferLong);
    }
}