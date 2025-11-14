package dev.pawin.tour_pro.tour.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import dev.pawin.tour_pro.tour.model.Tour;

public interface TourRepository extends PagingAndSortingRepository<Tour, Integer>, CrudRepository<Tour, Integer>{
    
}
