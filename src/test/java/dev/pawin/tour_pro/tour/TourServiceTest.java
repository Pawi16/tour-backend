package dev.pawin.tour_pro.tour;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import dev.pawin.tour_pro.common.enumeration.TourCompanyStatus;
import dev.pawin.tour_pro.common.enumeration.TourStatus;
import dev.pawin.tour_pro.common.exception.EntityNotFound;
import dev.pawin.tour_pro.tour.dto.CreateTourDto;
import dev.pawin.tour_pro.tour.model.Tour;
import dev.pawin.tour_pro.tour.model.TourCount;
import dev.pawin.tour_pro.tour.repository.TourCountRepository;
import dev.pawin.tour_pro.tour.repository.TourRepository;
import dev.pawin.tour_pro.tour_company.model.TourCompany;
import dev.pawin.tour_pro.tour_company.repository.TourCompanyRepository;

@ExtendWith(MockitoExtension.class)
public class TourServiceTest {
    @InjectMocks
    private TourServiceImpl tourService;
    @Mock
    private TourRepository tourRepository;
    @Mock
    private TourCountRepository tourCountRepository;
    @Mock
    private TourCompanyRepository tourCompanyRepository;

    @Test
    void whenCreateTourThenSuccess() {
        // arrange
        var mockTourCompany = new TourCompany(
                1,
                "WinTour",
                TourCompanyStatus.WAITING.name());
        when(tourCompanyRepository.findById(anyInt())).thenReturn(Optional.of(mockTourCompany));
        var payload = new CreateTourDto(mockTourCompany.id(), "PhuketTour", "lazy trip", "Phuket", null,
                Instant.parse("2025-01-15T14:30:00Z"), null);
        var mockTour = new Tour(1, AggregateReference.to(mockTourCompany.id()), payload.title(), payload.description(),
                payload.location(), 0, payload.activityDate(), TourStatus.PENDING.name());
        when(tourRepository.save(any(Tour.class))).thenReturn(mockTour);
        var mockTourCount = new TourCount(1, AggregateReference.to(mockTour.id()), 0);
        when(tourCountRepository.save(any(TourCount.class))).thenReturn(mockTourCount);

        // act
        var actual = tourService.createTour(payload);

        // assert
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(mockTour.id(), actual.id());
        Assertions.assertEquals(payload.title(), actual.title());
        Assertions.assertEquals(payload.description(), actual.description());
        Assertions.assertEquals(payload.location(), actual.location());
        Assertions.assertEquals(payload.activityDate(), actual.activityDate());
        Assertions.assertEquals(TourStatus.PENDING.name(), actual.status());
        Assertions.assertEquals(mockTourCompany.id(), actual.tourCompanyId().getId());

    }

    @Test
    void whenCreateTourButTourCompanyNotFoundThenError() {
        when(tourCompanyRepository.findById(anyInt())).thenThrow(new EntityNotFound());
        var payload = new CreateTourDto(1, "PhuketTour", "lazy trip", "Phuket", null,
                Instant.parse("2025-01-15T14:30:00Z"), null);

        // act and assert
        Assertions.assertThrows(
                EntityNotFound.class, () -> {
                    tourService.createTour(payload);

                });
    }

    @Test
    void whenGetTourByIdThenSuccess() {
        
        //arrange
        var mockTourCompany = new TourCompany(
                1,
                "WinTour",
                TourCompanyStatus.WAITING.name());
        var mockTour = new Tour(1, AggregateReference.to(mockTourCompany.id()), "PhuketTour", "lazy trip", "Phuket", 0, Instant.parse("2025-01-15T14:30:00Z"), TourStatus.PENDING.name());
        when(tourRepository.findById(anyInt())).thenReturn(Optional.of(mockTour));
        // act
        var actual = tourService.getTourById(1);

        //assert
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(mockTour.id(), actual.id());
        Assertions.assertEquals(mockTour.title(), actual.title());
        Assertions.assertEquals(mockTour.description(), actual.description());
        Assertions.assertEquals(mockTour.location(), actual.location());
        Assertions.assertEquals(mockTour.activityDate(), actual.activityDate());
        Assertions.assertEquals(mockTour.status(), actual.status());
        Assertions.assertEquals(mockTour.tourCompanyId().getId(), actual.tourCompanyId().getId());
        
    }

    @Test
    void whenGetTourByIdButNotFoundThenError() {
        // arrange
        when(tourRepository.findById(anyInt())).thenReturn(Optional.empty());

        // act & assert
        Assertions.assertThrows(EntityNotFound.class, () -> {
            tourService.getTourById(999);
        });

    }

    @Test
    void whenGetPageTourThenSuccess() {
        // arrange
        Pageable pageable = PageRequest.of(0, 10);
        var mockTour = new Tour(1, AggregateReference.to(1), "PhuketTour", "lazy trip", "Phuket", 0, Instant.parse("2025-01-15T14:30:00Z"), TourStatus.PENDING.name());
        List<Tour> tours = List.of(mockTour);
        Page<Tour> pagedTours = new PageImpl<>(tours, pageable, tours.size());

        when(tourRepository.findAll(pageable)).thenReturn(pagedTours);

        // act
        Page<Tour> actual = tourService.getPageTour(pageable);

        // assert
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(1, actual.getTotalElements());
        Assertions.assertEquals(1, actual.getContent().size());
        Assertions.assertEquals(mockTour.title(), actual.getContent().get(0).title());
    }

}
