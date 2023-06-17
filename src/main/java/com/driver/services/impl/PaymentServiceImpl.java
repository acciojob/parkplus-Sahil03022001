package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {
        Reservation reservation = reservationRepository2.findById(reservationId).get();
        Spot spot = reservation.getSpot();
        int ratePerHour = spot.getPricePerHour();
        int numberOfHours = reservation.getNumberOfHours();
        int totalBill = ratePerHour * numberOfHours;

        if(totalBill > amountSent) {
            throw new Exception("Insufficient Amount");
        }

        mode = mode.toUpperCase();
        if((!mode.equals("CARD") && !mode.equals("UPI")
        && !mode.equals("CASH"))) {
            throw new Exception("Payment mode not detected");
        }

        Payment payment = new Payment();
        if(mode.equals("CASH")) {
            payment.setPaymentMode(PaymentMode.CASH);
        } else if (mode.equals("CARD")) {
            payment.setPaymentMode(PaymentMode.CARD);
        } else {
            payment.setPaymentMode(PaymentMode.UPI);
        }

        payment.setPaymentCompleted(true);
        payment.setReservation(reservation);
        reservation.setPayment(payment);

        reservationRepository2.save(reservation);

        return payment;
    }
}
