package com.techJob.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.techJob.DTOs.artisan.ArtisanDTO;
import com.techJob.DTOs.artisan.ArtisanSkillDTO;
import com.techJob.DTOs.artisan.UpdateArtisanDTO;
import com.techJob.DTOs.chat.ConversationDTO;
import com.techJob.DTOs.chat.ConversationParticipantDTO;
import com.techJob.DTOs.chat.MessageDTO;
import com.techJob.DTOs.image.ImageDTO;
import com.techJob.DTOs.notifications.NotificationsDTO;
import com.techJob.DTOs.order.OrderDTO;
import com.techJob.DTOs.payments.PaymentDTO;
import com.techJob.DTOs.rating.CreateRatingDTO;
import com.techJob.DTOs.rating.RatingDTO;
import com.techJob.DTOs.serviceOffer.CreateExtraServiceOfferDTO;
import com.techJob.DTOs.serviceOffer.CreateOfferDTO;
import com.techJob.DTOs.serviceOffer.ExtraServiceOfferDTO;
import com.techJob.DTOs.serviceOffer.ServiceOfferDTO;
import com.techJob.DTOs.serviceOffer.UpdateExtraServiceOfferDTO;
import com.techJob.DTOs.serviceOffer.UpdateServiceOfferDTO;
import com.techJob.DTOs.user.AddressDTO;
import com.techJob.DTOs.user.CreateAddressDTO;
import com.techJob.DTOs.user.UpdateUserDTO;
import com.techJob.DTOs.user.UserDTO;
import com.techJob.domain.entity.Address;
import com.techJob.domain.entity.ArtisanProfile;
import com.techJob.domain.entity.ArtisanSkill;
import com.techJob.domain.entity.Conversation;
import com.techJob.domain.entity.ConversationParticipant;
import com.techJob.domain.entity.ExtraServiceOffer;
import com.techJob.domain.entity.Image;
import com.techJob.domain.entity.Message;
import com.techJob.domain.entity.Notifications;
import com.techJob.domain.entity.Order;
import com.techJob.domain.entity.Payment;
import com.techJob.domain.entity.Rating;
import com.techJob.domain.entity.ServiceOffer;
import com.techJob.domain.entity.User;

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
	
	ConversationDTO toDTO(Conversation conversation);
	
	ConversationParticipantDTO toDTO(ConversationParticipant participant);
	
	MessageDTO toDTO(Message message);
	
	
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