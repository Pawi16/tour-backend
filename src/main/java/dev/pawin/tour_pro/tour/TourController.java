package dev.pawin.tour_pro.tour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.slf4j.Logger;

@RestController
@RequestMapping("/tours")
public class TourController {

    private final Logger logger = LoggerFactory.getLogger(TourController.class);
    private final Map<Integer, Tour> tourInMemDb;
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(1);

    public TourController() {
        tourInMemDb = new HashMap<>();
    }

    // CRUD - Tour
    @GetMapping
    public List<Tour> getTours() {
        logger.info("Get all tours");
        return new ArrayList<>(tourInMemDb.values());
    }

    @GetMapping("/{id}")
    public Tour getTourById(@PathVariable int id) {
        logger.info("Get tourId : {}", id);
        return Optional.ofNullable(tourInMemDb.get(id)).orElseThrow(() -> {
            logger.error("tourId: {} not found", id);
            return new RuntimeException("not found");
        });
    }

    @PostMapping
    public Tour createTour(@RequestBody Tour tour) {
        Tour newTour = new Tour(ATOMIC_INTEGER.getAndIncrement(), tour.title(), tour.maxPeople());
        var id = newTour.id();
        tourInMemDb.put(id, newTour);
        logger.info("create new tour: {}", tourInMemDb.get(id));
        return tourInMemDb.get(id);
    }

    @PutMapping("/{id}")
    public Tour updateTourById(@PathVariable int id, @RequestBody Tour tour) {
        Tour updatedTour = new Tour(id, tour.title(), tour.maxPeople());
        tourInMemDb.put(id, updatedTour);
        logger.info("update tour: {}", tourInMemDb.get(id));
        return tourInMemDb.get(id);
    }

    @DeleteMapping("/{id}")
    public String deleteTour(@PathVariable int id) {
        if (!tourInMemDb.containsKey(id)) {
            logger.error("tour not found");
            return "Failed";
        }
        tourInMemDb.remove(id);
        logger.info("delete tourId: {}", id);
        return "Success to delete" + id;
    }

}