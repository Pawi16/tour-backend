package dev.pawin.tour_pro.tour_company;

import dev.pawin.tour_pro.tour_company.model.TourCompany;
import dev.pawin.tour_pro.tour_company.model.TourCompanyDto;

public interface TourCompanyService {
    TourCompany registerTourCompany(TourCompanyDto payload);

    TourCompany approveTourCompany (Integer id);
}
