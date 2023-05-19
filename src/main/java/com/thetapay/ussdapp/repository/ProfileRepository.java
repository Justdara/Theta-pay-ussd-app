package com.thetapay.ussdapp.repository;

import com.thetapay.ussdapp.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    boolean existsByPhoneNumber(String phoneNumber);

    Profile findProfileByPhoneNumber(String phoneNumber);

    @Query("SELECT p.balance FROM Profile p WHERE p.phoneNumber = :phoneNumber")
    double findBalanceByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query("SELECT p.thetaAddress FROM Profile p WHERE p.phoneNumber = :phoneNumber")
    String findThetaAddressByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}

