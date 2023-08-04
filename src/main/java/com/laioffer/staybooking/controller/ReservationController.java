package com.laioffer.staybooking.controller;

import org.springframework.web.bind.annotation.RestController;
import com.laioffer.staybooking.exception.InvalidReservationDateException;
import com.laioffer.staybooking.model.Reservation;
import com.laioffer.staybooking.model.User;
import com.laioffer.staybooking.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

// The Rest Controller for Reservation
@RestController
public class ReservationController {
    private ReservationService reservationService;

    // Autowired annotation is used for automatic dependency injection.
    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // GetMapping annotation is used to map HTTP GET requests onto specific handler methods.
    // This method will return a list of reservations for the currently authenticated user.
    @GetMapping(value = "/reservations")
    public List<Reservation> listReservations(Principal principal) {
        return reservationService.listByGuest(principal.getName());
    }

    // PostMapping annotation is used to map HTTP POST requests onto specific handler methods.
    // This method will add a reservation to the database.
    @PostMapping("/reservations")
    public void addReservation(@RequestBody Reservation reservation, Principal principal) {
        LocalDate checkinDate = reservation.getCheckinDate();
        LocalDate checkoutDate = reservation.getCheckoutDate();
        // Throws an exception if the reservation dates are invalid.
        if (checkinDate.equals(checkoutDate) || checkinDate.isAfter(checkoutDate) || checkinDate.isBefore(LocalDate.now())) {
            throw new InvalidReservationDateException("Invalid date for reservation");
        }
        // Sets the guest of the reservation to the currently authenticated user.
        reservation.setGuest(new User.Builder().setUsername(principal.getName()).build());
        // Add the reservation to the database.
        reservationService.add(reservation);
    }

    // DeleteMapping annotation is used to map HTTP DELETE requests onto specific handler methods.
    // This method will delete a reservation from the database.
    @DeleteMapping("/reservations/{reservationId}")
    public void deleteReservation(@PathVariable Long reservationId, Principal principal) {
        reservationService.delete(reservationId, principal.getName());
    }
}