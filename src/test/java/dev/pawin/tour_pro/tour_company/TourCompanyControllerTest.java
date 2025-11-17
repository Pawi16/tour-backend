package dev.pawin.tour_pro.tour_company;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.pawin.tour_pro.common.enumeration.TourCompanyStatus;
import dev.pawin.tour_pro.common.exception.EntityNotFound;
import dev.pawin.tour_pro.tour_company.dto.RegisterTourCompanyDto;
import dev.pawin.tour_pro.tour_company.model.TourCompany;

@WebMvcTest(TourCompanyController.class)
public class TourCompanyControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @MockitoBean
    private TourCompanyService tourCompanyService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void whenRegisterTourCompanyThenSuccess() throws Exception {
        // arrange
        var mockTourCompany = new TourCompany(
                1,
                "WinTour",
                TourCompanyStatus.WAITING.name());
        when(tourCompanyService.registerTourCompany(any(RegisterTourCompanyDto.class))).thenReturn(mockTourCompany);
        var payload = new RegisterTourCompanyDto(null, "WinTour", "WinUser", "WinPassword", null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tour-companies")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(mockTourCompany.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(mockTourCompany.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(mockTourCompany.status()));
    }

    @Test
    void whenApproveTourCompanyThenSuccess() throws Exception {
        // arrange
        var mockTourCompany = new TourCompany(
                1,
                "WinTour",
                TourCompanyStatus.APPROVED.name());
        when(tourCompanyService.approveTourCompany(anyInt())).thenReturn(mockTourCompany);

        mockMvc.perform(MockMvcRequestBuilders.patch(String.format("/api/v1/tour-companies/%d/approve", mockTourCompany.id())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(mockTourCompany.id()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(mockTourCompany.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(mockTourCompany.status()));
    }

    @Test
    void whenApproveTourCompanyButEntityNotFoundThenError() throws Exception {
        // arrange
        when(tourCompanyService.approveTourCompany(anyInt())).thenThrow(new EntityNotFound());

        mockMvc.perform(MockMvcRequestBuilders.patch(String.format("/api/v1/tour-companies/%d/approve", 1)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void whenApproveTourCompanyButInvalidArgumentThenError() throws Exception {
        // arrange
        var mockTourCompany = new TourCompany(
                1,
                "WinTour",
                TourCompanyStatus.WAITING.name());
        when(tourCompanyService.registerTourCompany(any(RegisterTourCompanyDto.class))).thenReturn(mockTourCompany);
        var payload = new RegisterTourCompanyDto(null, "", "", "", null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tour-companies")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
