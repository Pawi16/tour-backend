package dev.pawin.tour_pro.tour.repository;

import org.springframework.data.repository.CrudRepository;

import dev.pawin.tour_pro.tour.model.TourCount;

public interface TourCountRepository extends CrudRepository<TourCount, Integer>{
    
}
