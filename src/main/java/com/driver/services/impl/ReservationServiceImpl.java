package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {

        User user;
        try {
            user = userRepository3.findById(userId).get();
        } catch (Exception e) {
            throw  new Exception("Cannot make reservation");
        }

        ParkingLot parkingLot;
        try {
            parkingLot = parkingLotRepository3.findById(parkingLotId).get();
        } catch (Exception e) {
            throw  new Exception("Cannot make reservation");
        }

        List<Spot> spotList = parkingLot.getSpotList();
        int minRate = Integer.MAX_VALUE;
        Spot perfectSpot = null;

        for(Spot spot : spotList) {
            int wheelsAllowed;
            if(spot.getSpotType().equals(SpotType.OTHERS)) {
                wheelsAllowed = Integer.MAX_VALUE;
            } else if(spot.getSpotType().equals(SpotType.FOUR_WHEELER)) {
                wheelsAllowed = 4;
            } else {
                wheelsAllowed = 2;
            }

            if(!spot.getOccupied() && wheelsAllowed >= numberOfWheels) {
                int currRate = spot.getPricePerHour();
                if(currRate < minRate) {
                    minRate = currRate;
                    perfectSpot = spot;
                }
            }
        }

        if(perfectSpot == null) {
            throw  new Exception("Cannot make reservation");
        }

        perfectSpot.setOccupied(true);
        Reservation reservation = new Reservation();
        reservation.setNumberOfHours(timeInHours);
        reservation.setSpot(perfectSpot);
        reservation.setUser(user);

        perfectSpot.getReservationList().add(reservation);
        user.getReservationList().add(reservation);

//        reservationRepository3.save(reservation);

        spotRepository3.save(perfectSpot);
        userRepository3.save(user);

        return reservation;
    }
}
