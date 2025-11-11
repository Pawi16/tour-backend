package dev.pawin.tour_pro.tour_company;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.pawin.tour_pro.common.enumeration.TourCompanyStatus;
import dev.pawin.tour_pro.common.exception.EntityNotFound;
import dev.pawin.tour_pro.tour_company.dto.RegisterTourCompanyDto;
import dev.pawin.tour_pro.tour_company.model.TourCompany;
import dev.pawin.tour_pro.tour_company.model.TourCompanyLogin;
import dev.pawin.tour_pro.tour_company.model.TourCompanyWallet;
import dev.pawin.tour_pro.tour_company.repository.TourCompanyLoginRepository;
import dev.pawin.tour_pro.tour_company.repository.TourCompanyRepository;
import dev.pawin.tour_pro.tour_company.repository.TourCompanyWalletRepository;

@Service
public class TourCompanyServiceImpl implements TourCompanyService {

    private Logger logger = LoggerFactory.getLogger(TourCompanyServiceImpl.class);
    private final TourCompanyRepository tourCompanyRepository;
    private final TourCompanyLoginRepository tourCompanyLoginRepository;
    private final TourCompanyWalletRepository tourCompanyWalletRepository;
    private final PasswordEncoder passwordEncoder;

    public TourCompanyServiceImpl(TourCompanyRepository tourCompanyRepository,
            TourCompanyLoginRepository tourCompanyLoginRepository, PasswordEncoder passwordEncoder, TourCompanyWalletRepository tourCompanyWalletRepository) {
        this.tourCompanyRepository = tourCompanyRepository;
        this.tourCompanyLoginRepository = tourCompanyLoginRepository;
        this.tourCompanyWalletRepository = tourCompanyWalletRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public TourCompany registerTourCompany(RegisterTourCompanyDto payload) {
        logger.debug("[registerTour] new tour company is registering...");
        var tourCompanyName = payload.name();
        var tourCompanyStatus = TourCompanyStatus.WAITING.name();
        var tourCompany = new TourCompany(
                null,
                tourCompanyName,
                tourCompanyStatus);
        var newTourCompany = tourCompanyRepository.save(tourCompany);
        logger.debug("[registerTour] register new tour company success: {}", newTourCompany);

        // Create login credential
        createCompanyCredential(newTourCompany, payload);
        return newTourCompany;
    }

    @Override
    @Transactional
    public TourCompany approveTourCompany(Integer id) {
        TourCompany tourCompany = tourCompanyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound(String.format("Tour Company Id: %s not found", id)));
        var approvedTourCompany = new TourCompany(id, tourCompany.name(), TourCompanyStatus.APPROVED.name());
        var updatedTourCompany = tourCompanyRepository.save(approvedTourCompany);
        
        // Create wallet
        createCompanyWallet(updatedTourCompany);
        
        
        return updatedTourCompany;
    }

    private void createCompanyCredential(TourCompany tourCompany, RegisterTourCompanyDto payload) {
        AggregateReference<TourCompany, Integer> tourCompanyReference = AggregateReference.to(tourCompany.id());
        var encryptedPassword = passwordEncoder.encode(payload.password());
        TourCompanyLogin tourCompanyCredential = new TourCompanyLogin(null, tourCompanyReference, payload.username(), encryptedPassword);
        tourCompanyLoginRepository.save(tourCompanyCredential);
        logger.info("Created credential for company: {}", tourCompany.id());
    }

    private void createCompanyWallet(TourCompany tourCompany){
        AggregateReference<TourCompany, Integer> tourCompanyReference = AggregateReference.to(tourCompany.id());
        Instant currentTimestamp = Instant.now();
        BigDecimal initBalance = new BigDecimal("0.00");
        var wallet = new TourCompanyWallet(null,tourCompanyReference,currentTimestamp ,initBalance);
        tourCompanyWalletRepository.save(wallet);
        logger.info("Created wallet for company: {}", tourCompany.id());
    }

}
