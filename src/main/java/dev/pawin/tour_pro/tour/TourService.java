package dev.pawin.tour_pro.tour;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import dev.pawin.tour_pro.tour.dto.CreateTourDto;
import dev.pawin.tour_pro.tour.model.Tour;

public interface TourService {
    Tour createTour (CreateTourDto payload);

    Tour getTourById(int id);

    Page<Tour> getPageTour(Pageable pageable);
}
