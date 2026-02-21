package com.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.DTOs.artisan.ArtisanDTO;
import com.DTOs.artisan.ArtisanSkillDTO;
import com.DTOs.artisan.UpdateArtisanDTO;
import com.DTOs.image.ImageDTO;
import com.DTOs.notifications.NotificationsDTO;
import com.DTOs.order.OrderDTO;
import com.DTOs.payments.PaymentDTO;
import com.DTOs.rating.CreateRatingDTO;
import com.DTOs.rating.RatingDTO;
import com.DTOs.serviceOffer.CreateExtraServiceOfferDTO;
import com.DTOs.serviceOffer.CreateOfferDTO;
import com.DTOs.serviceOffer.ExtraServiceOfferDTO;
import com.DTOs.serviceOffer.ServiceOfferDTO;
import com.DTOs.serviceOffer.UpdateExtraServiceOfferDTO;
import com.DTOs.serviceOffer.UpdateServiceOfferDTO;
import com.DTOs.user.AddressDTO;
import com.DTOs.user.CreateAddressDTO;
import com.DTOs.user.UpdateUserDTO;
import com.DTOs.user.UserDTO;
import com.domain.entity.Address;
import com.domain.entity.ArtisanProfile;
import com.domain.entity.ArtisanSkill;
import com.domain.entity.ExtraServiceOffer;
import com.domain.entity.Image;
import com.domain.entity.Notifications;
import com.domain.entity.Order;
import com.domain.entity.Payment;
import com.domain.entity.Rating;
import com.domain.entity.ServiceOffer;
import com.domain.entity.User;

@Mapper(componentModel = "spring")
public interface GeneralMapper {
	
	
	UserDTO toDTO(User user);
	
	AddressDTO toDTO(Address address);
	
	NotificationsDTO toDTO(Notifications notifications);
	
	ServiceOfferDTO toDTO(ServiceOffer serviceOffer);
	
	ExtraServiceOfferDTO toDTO(ExtraServiceOffer extraServiceOffer);
	
	OrderDTO toDTO(Order order);
	
	ArtisanDTO toDTO(ArtisanProfile artisan);
	
	RatingDTO toDTO(Rating rating);
	
	PaymentDTO toDTO (Payment payment);
	
	ImageDTO toDTO(Image image);
	
	
	Address toEntity(CreateAddressDTO dto);
	
	ArtisanSkill toEntity(ArtisanSkillDTO dto);
	List<ArtisanSkill> toEntity(List<ArtisanSkillDTO> dto);

	ServiceOffer toEntity(CreateOfferDTO dto);
	ServiceOffer toEntity(ServiceOfferDTO dto);
	
	
	ExtraServiceOffer toEntity(CreateExtraServiceOfferDTO dto);
	ExtraServiceOffer toEntity(ExtraServiceOfferDTO dto);
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateUserFromDTO(UpdateUserDTO dto, @MappingTarget User entity);
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateArtisanFromDTO(UpdateArtisanDTO dto, @MappingTarget ArtisanProfile entity);
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateOfferFromDTO(UpdateServiceOfferDTO dto,@MappingTarget ServiceOffer offer);
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateRatingFromDTO(CreateRatingDTO dto,@MappingTarget Rating rating);
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateExtraServiceOfferFromDTO(UpdateExtraServiceOfferDTO dto, @MappingTarget ExtraServiceOffer entity);
	
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateAddressFromDTO(CreateAddressDTO dto,@MappingTarget Address address);
	
	
	
}