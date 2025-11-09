package dev.pawin.tour_pro.tour_company;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.pawin.tour_pro.tour_company.model.TourCompany;
import dev.pawin.tour_pro.tour_company.model.TourCompanyDto;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/tour-companies")
public class TourCompanyController {

    private final Logger logger = LoggerFactory.getLogger(TourCompanyController.class);
    private final TourCompanyService tourCompanyService;

    public TourCompanyController(TourCompanyService tourCompanyService) {
        this.tourCompanyService = tourCompanyService;
    }

    @PostMapping
    public ResponseEntity<TourCompany> registerTourCompany (@RequestBody @Validated TourCompanyDto body) {
        var result = tourCompanyService.registerTourCompany(body);

        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<TourCompany> approveTourCompany (@PathVariable Integer id ) {
        
        var approvedTourCompany = tourCompanyService.approveTourCompany(id);
        logger.info("[approveTourCompany] company id : {} is approved", id);
        return ResponseEntity.ok(approvedTourCompany);
    }
    
    


}
