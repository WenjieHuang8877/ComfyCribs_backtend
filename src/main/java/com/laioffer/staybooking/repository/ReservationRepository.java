package com.laioffer.staybooking.repository;

import com.laioffer.staybooking.model.Reservation;
import com.laioffer.staybooking.model.Stay;
import com.laioffer.staybooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    List<Reservation> findByGuest(User guest);
    List<Reservation> findByStay(Stay stay);
    Reservation findByIdAndGuest(Long id, User guest);
    // need to check active reservations before deleting a stay
    List<Reservation> findByStayAndCheckoutDateAfter(Stay stay, LocalDate date);

}
