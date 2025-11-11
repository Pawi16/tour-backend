package dev.pawin.tour_pro.tour_company;

import dev.pawin.tour_pro.tour_company.dto.RegisterTourCompanyDto;
import dev.pawin.tour_pro.tour_company.model.TourCompany;

public interface TourCompanyService {
    TourCompany registerTourCompany(RegisterTourCompanyDto payload);

    TourCompany approveTourCompany (Integer id);
}
