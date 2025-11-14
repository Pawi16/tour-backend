package dev.pawin.tour_pro.tour;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.pawin.tour_pro.common.enumeration.TourStatus;
import dev.pawin.tour_pro.common.exception.EntityNotFound;
import dev.pawin.tour_pro.tour.dto.CreateTourDto;
import dev.pawin.tour_pro.tour.model.Tour;
import dev.pawin.tour_pro.tour.model.TourCount;
import dev.pawin.tour_pro.tour.repository.TourCountRepository;
import dev.pawin.tour_pro.tour.repository.TourRepository;
import dev.pawin.tour_pro.tour_company.model.TourCompany;
import dev.pawin.tour_pro.tour_company.repository.TourCompanyRepository;

@Service
public class TourServiceImpl implements TourService {

    private final Logger logger = LoggerFactory.getLogger(TourServiceImpl.class);
    private final TourRepository tourRepository;
    private final TourCountRepository tourCountRepository;
    private final TourCompanyRepository tourCompanyRepository;

    public TourServiceImpl(TourRepository tourRepository, TourCountRepository tourCountRepository,
            TourCompanyRepository tourCompanyRepository) {
        this.tourRepository = tourRepository;
        this.tourCountRepository = tourCountRepository;
        this.tourCompanyRepository = tourCompanyRepository;
    }

    @Override
    @Transactional
    public Tour createTour(CreateTourDto payload) {
        var tourCompanyId = payload.tourCompanyId();
        var tourCompany = tourCompanyRepository.findById(tourCompanyId)
                .orElseThrow(() -> new EntityNotFound(String.format("Tour Company Id: %s not found", tourCompanyId)));
        AggregateReference<TourCompany, Integer> tourCompanyReference = AggregateReference.to(tourCompany.id());
        var tour = new Tour(
                null,
                tourCompanyReference,
                payload.title(),
                payload.description(),
                payload.location(),
                0,
                payload.activityDate(),
                TourStatus.PENDING.name());
        var newTour = tourRepository.save(tour);
        logger.debug("Tour has been created: {}");
        tourCountRepository.save(new TourCount(null, AggregateReference.to(newTour.id()), 0));
        return newTour;
    }

    @Override
    public Tour getTourById(int id) {
        return tourRepository.findById(id).orElseThrow(() -> new EntityNotFound(String.format("Tour Id: %s not found", id)));
    }

    @Override
    public Page<Tour> getPageTour(Pageable pageable) {
        return tourRepository.findAll(pageable);
        }

}
