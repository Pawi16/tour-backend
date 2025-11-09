package dev.pawin.tour_pro.tour_company.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("tour_company")
public record TourCompany(@Id Integer id, String name, String status) {

}
