package com.hms.service;

import com.hms.entity.AppUser;
import com.hms.entity.Property;
import com.hms.entity.Review;
import com.hms.payload.AppUserDto;
import com.hms.payload.PropertyDto;
import com.hms.payload.ReviewDto;
import com.hms.repository.PropertyRepository;
import com.hms.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PropertyRepository propertyRepository;
    private final PropertiesService propertiesService;
    private final AppUserService appUserService;

    // -------------------- Constructor --------------------- //

    public ReviewService(ReviewRepository reviewRepository,
                         PropertyRepository propertyRepository,
                         PropertiesService propertiesService,
                         AppUserService appUserService) {
        this.reviewRepository = reviewRepository;
        this.propertyRepository = propertyRepository;
        this.propertiesService = propertiesService;
        this.appUserService = appUserService;
    }

    // --------------------- Convertors --------------------- //

    public Review convertDtoToEntity(ReviewDto reviewDto, Long propertyId, AppUser appUserId) {
        Review reviews = new Review();
        reviews.setRatings(reviewDto.getRatings());
        reviews.setDescriptions(reviewDto.getDescription());
        reviews.setPropertyId(propertyRepository.findById(propertyId).get());
        reviews.setAppUserId(appUserId);
        return reviews;
    }

    public ReviewDto convertEntityToDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setRatings(review.getRatings());
        reviewDto.setDescription(review.getDescriptions());
        PropertyDto propertyDto = propertiesService.convertEntityToDto(review.getPropertyId());
        AppUserDto appUserDto = appUserService.mapToDto(review.getAppUserId());
        reviewDto.setPropertyId(propertyDto);
        reviewDto.setAppUserId(appUserDto);
        return reviewDto;
    }

    // ----------------------- Create ----------------------- //

    public boolean verifyUniqueReview(Long propertyId, AppUser appUserId) {
        return reviewRepository.existsByAppUserIdAndPropertyId(appUserId, propertyRepository.findById(propertyId).get());
    }

    public ReviewDto addNewReviews(ReviewDto reviewDto, Long propertyId, AppUser appUserId) {
        return convertEntityToDto(reviewRepository.save(convertDtoToEntity(reviewDto, propertyId, appUserId)));
    }

    // ------------------------ Read ------------------------ //

    public List<ReviewDto> getAllReviews(AppUser appUserId) {
        return reviewRepository.findByAppUserId(appUserId).stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    // ----------------------- Update ----------------------- //
    // ----------------------- Create ----------------------- //
}
