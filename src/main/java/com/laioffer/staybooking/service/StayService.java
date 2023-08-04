package com.laioffer.staybooking.service;

import com.laioffer.staybooking.exception.StayDeleteException;
import com.laioffer.staybooking.exception.StayNotExistException;
import com.laioffer.staybooking.model.*;
import com.laioffer.staybooking.repository.ReservationRepository;
import com.laioffer.staybooking.repository.StayRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.laioffer.staybooking.repository.LocationRepository;

@Service
public class StayService {

    private final StayRepository stayRepository;
    private final ImageStorageService imageStorageService;
    private final LocationRepository locationRepository;
    private final GeoCodingService geoCodingService;
    private final ReservationRepository reservationRepository;

    public StayService(StayRepository stayRepository, LocationRepository locationRepository, ImageStorageService imageStorageService, GeoCodingService geoCodingService, ReservationRepository reservationRepository) {
        this.stayRepository = stayRepository;
        this.locationRepository = locationRepository;
        this.imageStorageService = imageStorageService;
        this.geoCodingService = geoCodingService;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Retrieves a list of stays associated with a user.
     *
     * @param username the username of the user
     * @return a list of stays
     */
    public List<Stay> listByUser(String username) {
        return stayRepository.findByHost(new User.Builder().setUsername(username).build());
    }

    /**
     * Retrieves a stay by its ID and associated host.
     *
     * @param stayId   the ID of the stay
     * @param username the username of the host
     * @return the stay
     * @throws StayNotExistException if the stay doesn't exist
     */
    public Stay findByIdAndHost(Long stayId, String username) throws StayNotExistException {
        User user = new User.Builder().setUsername(username).build();
        Stay stay = stayRepository.findByIdAndHost(stayId, user);
        if (stay == null) {
            throw new StayNotExistException("Stay doesn't exist");
        }
        return stay;
    }

    /**
     * Adds a new stay with the provided information and images.
     *
     * @param stay   the stay to be added
     * @param images the array of images associated with the stay
     */
    @Transactional
    public void add(Stay stay, MultipartFile[] images) {
        List<String> mediaLinks = Arrays.stream(images).parallel().map(
                image -> imageStorageService.save(image)
        ).collect(Collectors.toList());

        List<StayImage> stayImages = new ArrayList<>();
        for (String mediaLink : mediaLinks) {
            stayImages.add(new StayImage(mediaLink, stay));
        }

        stay.setImages(stayImages);
        stayRepository.save(stay);

        Location location = geoCodingService.getLatLng(stay.getId(), stay.getAddress());
        locationRepository.save(location);
    }

    /**
     * Deletes a stay with the provided ID and associated host.
     *
     * @param stayId   the ID of the stay to be deleted
     * @param username the username of the host
     * @throws StayNotExistException  if the stay doesn't exist
     * @throws StayDeleteException    if the stay cannot be deleted due to active reservations
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long stayId, String username) throws StayNotExistException, StayDeleteException {
        Stay stay = stayRepository.findByIdAndHost(stayId, new User.Builder().setUsername(username).build());
        if (stay == null) {
            throw new StayNotExistException("Stay doesn't exist");
        }
        //如果在当前系统时间后还有reservation存在，那么不能删除该stay
        List<Reservation> reservations = reservationRepository.findByStayAndCheckoutDateAfter(stay, LocalDate.now());
        if (reservations != null && reservations.size() > 0) {
            throw new StayDeleteException("Cannot delete stay with active reservations");
        }
        stayRepository.deleteById(stayId);
    }
}