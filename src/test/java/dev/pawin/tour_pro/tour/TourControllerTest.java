package dev.pawin.tour_pro.tour;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.exception.InternalServerErrorException;

import dev.pawin.tour_pro.common.enumeration.TourCompanyStatus;
import dev.pawin.tour_pro.common.enumeration.TourStatus;
import dev.pawin.tour_pro.common.exception.EntityNotFound;
import dev.pawin.tour_pro.tour.dto.CreateTourDto;
import dev.pawin.tour_pro.tour.model.Tour;
import dev.pawin.tour_pro.tour_company.dto.RegisterTourCompanyDto;
import dev.pawin.tour_pro.tour_company.model.TourCompany;

@WebMvcTest(TourController.class)
public class TourControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TourService tourService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void whenGetTourByIdThenSuccess() throws Exception {
        // arrange
        var mockTourCompany = new TourCompany(
                1,
                "WinTour",
                TourCompanyStatus.WAITING.name());
        var mockTour = new Tour(1, AggregateReference.to(mockTourCompany.id()), "PhuketTour", "lazy trip", "Phuket", 0,
                Instant.parse("2025-01-15T14:30:00Z"), TourStatus.PENDING.name());
        when(tourService.getTourById(anyInt())).thenReturn(mockTour);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tours/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockTour.id()))
                .andExpect(jsonPath("$.title").value(mockTour.title()))
                .andExpect(jsonPath("$.description").value(mockTour.description()))
                .andExpect(jsonPath("$.location").value(mockTour.location()))
                .andExpect(jsonPath("$.status").value(mockTour.status()));
    }

    @Test
    void whenGetTourByIdButNotFoundThenError() throws Exception {
        // arrange
        int nonExistentId = 999;
        when(tourService.getTourById(nonExistentId))
                .thenThrow(new EntityNotFound(String.format("Tour Id: %s not found", nonExistentId)));

        // act & assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tours/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetTourByIdButServerErrorThenReturn500() throws Exception {
        // arrange
        when(tourService.getTourById(anyInt()))
                .thenThrow(new InternalServerErrorException(""));

        // act & assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tours/{id}", 1))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void whenCreateTourThenSuccess() throws Exception {
        // arrange
        var mockTourCompany = new TourCompany(
                1,
                "WinTour",
                TourCompanyStatus.WAITING.name());
        var mockTour = new Tour(1, AggregateReference.to(mockTourCompany.id()), "PhuketTour", "lazy trip", "Phuket", 0,
                Instant.parse("2025-01-15T14:30:00Z"), TourStatus.PENDING.name());
        when(tourService.createTour(any(CreateTourDto.class))).thenReturn(mockTour);
        var payload = new CreateTourDto(1, "PhuketTour", "lazy trip", "Phuket", null,
                Instant.parse("2025-01-15T14:30:00Z"), null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tours")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(mockTour.id()))
                .andExpect(jsonPath("$.title").value(mockTour.title()))
                .andExpect(jsonPath("$.description").value(mockTour.description()))
                .andExpect(jsonPath("$.location").value(mockTour.location()))
                .andExpect(jsonPath("$.status").value(mockTour.status()));
    }

    @Test
    void whenGetPageToursThenSuccess() throws Exception {
        // arrange
        var mockTourCompany = new TourCompany(
                1,
                "WinTour",
                TourCompanyStatus.WAITING.name());
        Pageable pageable = PageRequest.of(0, 10);
        var mockTour = new Tour(1, AggregateReference.to(1), "PhuketTour", "lazy trip", "Phuket", 0,
                Instant.parse("2025-01-15T14:30:00Z"), TourStatus.PENDING.name());
        List<Tour> tours = List.of(mockTour);
        Page<Tour> pagedTours = new PageImpl<>(tours, pageable, tours.size());
        when(tourService.getPageTour(any(Pageable.class))).thenReturn(pagedTours);

        //arrange & assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tours?page=1&size=2&sortField=id&sortDirection=desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(mockTour.id()))
                .andExpect(jsonPath("$.content[0].title").value(mockTour.title()));

    }

}
