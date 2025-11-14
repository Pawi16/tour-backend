package dev.pawin.tour_pro.tour.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;


public record TourCount(
    @Id Integer id, 
    AggregateReference<Tour,Integer> tourId, 
    Integer amount
    ) {

}
