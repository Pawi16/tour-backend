package dev.pawin.tour_pro.tour_company;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import dev.pawin.tour_pro.common.exception.EntityNotFound;
import dev.pawin.tour_pro.tour_company.model.TourCompany;
import dev.pawin.tour_pro.tour_company.model.TourCompanyDto;
import dev.pawin.tour_pro.tour_company.model.TourCompanyStatus;

@Service
public class TourCompanyServiceImpl implements TourCompanyService {

    private Logger logger = LoggerFactory.getLogger(TourCompanyServiceImpl.class);
    private final TourCompanyRepository tourCompanyRepository;

    public TourCompanyServiceImpl(TourCompanyRepository tourCompanyRepository) {
        this.tourCompanyRepository = tourCompanyRepository;
    }

    @Override
    public TourCompany registerTourCompany(TourCompanyDto payload) {
        logger.debug("[registerTour] new tour company is registering...");
        var tourCompanyName = payload.name();
        var tourCompanyStatus = TourCompanyStatus.WAITING.name();
        var tourCompany = new TourCompany(
                null,
                tourCompanyName,
                tourCompanyStatus);
        var newTourCompany = tourCompanyRepository.save(tourCompany);
        logger.debug("[registerTour] register new tour company success: {}", newTourCompany);
        return newTourCompany;
    }

    @Override
    public TourCompany approveTourCompany(Integer id) {
        TourCompany tourCompany = tourCompanyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound(String.format("Tour Company Id: %s not found", id)));
        TourCompany approvedTourCompany = new TourCompany(id, tourCompany.name(), TourCompanyStatus.APPROVED.name());
        return tourCompanyRepository.save(approvedTourCompany);
    }

}
