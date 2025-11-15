package dev.pawin.tour_pro.tour_company;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.crypto.password.PasswordEncoder;

import dev.pawin.tour_pro.common.enumeration.TourCompanyStatus;
import dev.pawin.tour_pro.common.exception.EntityNotFound;
import dev.pawin.tour_pro.tour_company.dto.RegisterTourCompanyDto;
import dev.pawin.tour_pro.tour_company.model.TourCompany;
import dev.pawin.tour_pro.tour_company.model.TourCompanyLogin;
import dev.pawin.tour_pro.tour_company.model.TourCompanyWallet;
import dev.pawin.tour_pro.tour_company.repository.TourCompanyLoginRepository;
import dev.pawin.tour_pro.tour_company.repository.TourCompanyRepository;
import dev.pawin.tour_pro.tour_company.repository.TourCompanyWalletRepository;

@ExtendWith(MockitoExtension.class)
public class TourCompanyServiceTest {

    @InjectMocks
    private TourCompanyServiceImpl tourCompanyService;

    @Mock
    private TourCompanyRepository tourCompanyRepository;
    @Mock
    private TourCompanyLoginRepository tourCompanyLoginRepository;
    @Mock
    private TourCompanyWalletRepository tourCompanyWalletRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void whenRegisterTourThenSuccess() {
        // arrange
        var mockTourCompany = new TourCompany(
                1,
                "WinTour",
                TourCompanyStatus.WAITING.name());
        when(tourCompanyRepository.save(any(TourCompany.class))).thenReturn(mockTourCompany);

        when(passwordEncoder.encode(anyString())).thenReturn("encryptedPassword");
        var companyCredential = new TourCompanyLogin(1, AggregateReference.to(mockTourCompany.id()),
                mockTourCompany.name(), "encryptedPassword");
        when(tourCompanyLoginRepository.save(any(TourCompanyLogin.class))).thenReturn(companyCredential);

        var payload = new RegisterTourCompanyDto(null, "WinTour", "WinUser", "WinPassword", null);

        // act
        var actual = tourCompanyService.registerTourCompany(payload);

        // assert
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(mockTourCompany.id(), actual.id().intValue());
        Assertions.assertEquals(mockTourCompany.name(), actual.name());
        Assertions.assertEquals(mockTourCompany.status(), actual.status());

    }

    @Test
    void whenApproveTourThenSuccess() {
        //arrange
        var mockTourCompany = new TourCompany(
                1,
                "WinTour",
                TourCompanyStatus.WAITING.name());
        when(tourCompanyRepository.findById(anyInt())).thenReturn(Optional.of(mockTourCompany));
        var approvedTourCompany = new TourCompany(mockTourCompany.id(), mockTourCompany.name(), TourCompanyStatus.APPROVED.name());
        when(tourCompanyRepository.save(any(TourCompany.class))).thenReturn(approvedTourCompany);
        var tourCompanyWallet = new TourCompanyWallet(1,AggregateReference.to(approvedTourCompany.id()), Instant.now(), new BigDecimal("0.00"));
        when(tourCompanyWalletRepository.save(any(TourCompanyWallet.class))).thenReturn(tourCompanyWallet);

        //act
        var actual = tourCompanyService.approveTourCompany(mockTourCompany.id());

        //assert
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(mockTourCompany.id(), actual.id().intValue());
        Assertions.assertEquals(mockTourCompany.name(), actual.name());
        Assertions.assertEquals(TourCompanyStatus.APPROVED.name(), actual.status());
    }

    @Test
    void whenApproveTourButTourCompanyNotFoundThenError() {
        when(tourCompanyRepository.findById(anyInt())).thenThrow(new EntityNotFound());

        Assertions.assertThrows(EntityNotFound.class, () -> {
            tourCompanyService.approveTourCompany(1);
        });
    }
}
