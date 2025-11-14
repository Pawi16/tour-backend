package dev.pawin.tour_pro.tour;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import dev.pawin.tour_pro.tour.dto.CreateTourDto;
import dev.pawin.tour_pro.tour.model.Tour;

import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.slf4j.Logger;

@RestController
@RequestMapping("/api/v1/tours")
public class TourController {

    private final Logger logger = LoggerFactory.getLogger(TourController.class);
    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @GetMapping
    public Page<Tour> getTours(
        @RequestParam(required = true) int page,
        @RequestParam(required = true) int size,
        @RequestParam(required = true) String sortField,
        @RequestParam(required = true) String sortDirection

    ) {
        Sort sort = Sort.by(Sort.Direction.valueOf(sortDirection.toUpperCase()), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);
        return tourService.getPageTour(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tour> getTourById(@PathVariable int id) {
        logger.info("Get tourId : {}", id);
        return ResponseEntity.ok(tourService.getTourById(id));
        
    }

    @PostMapping
    public ResponseEntity<Tour> createTour(@RequestBody @Validated CreateTourDto body) {
        var newTour = tourService.createTour(body);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newTour.id())
                .toUri();
        return ResponseEntity.created(location).body(newTour);
    }


}