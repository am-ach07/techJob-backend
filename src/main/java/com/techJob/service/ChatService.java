package com.techJob.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.techJob.DTOs.PaginationAndSortDTO;
import com.techJob.DTOs.chat.ConversationDTO;
import com.techJob.DTOs.chat.MessageDTO;
import com.techJob.domain.entity.Conversation;
import com.techJob.domain.entity.ConversationParticipant;
import com.techJob.domain.entity.Message;
import com.techJob.domain.entity.User;
import com.techJob.exception.user.UserNotFoundException;
import com.techJob.mapper.GeneralMapper;
import com.techJob.repository.ConversationParticipantRepository;
import com.techJob.repository.ConversationRepository;
import com.techJob.repository.MessageRepository;
import com.techJob.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class ChatService {

	
	
	private final UserRepository userRepository;
	private final ConversationRepository conversationRepository;
	private final GeneralMapper generalMapper;
	private final ConversationParticipantRepository participantRepository;
	private final MessageRepository messageRepository;



	public ChatService(
			UserRepository userRepository,
			ConversationRepository conversationRepository,
			GeneralMapper generalMapper,
			ConversationParticipantRepository participantRepository,
			MessageRepository messageRepository) {
		this.userRepository = userRepository;
		this.conversationRepository = conversationRepository;
		this.generalMapper = generalMapper;
		this.participantRepository = participantRepository;
		this.messageRepository = messageRepository;
	}





	private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByUsernameOrEmail(email, email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }
	
	
	private void validateChatPermission(User currentUser, User otherUser) {

	    boolean currentIsArtisan = currentUser.getArtisanProfile() != null;
	    boolean otherIsArtisan   = otherUser.getArtisanProfile() != null;

	    boolean isAllowed =
	            (currentIsArtisan && !otherIsArtisan) ||
	            (!currentIsArtisan && otherIsArtisan);

	    if (!isAllowed) {
	        throw new IllegalArgumentException(
	                "You can only chat with users of the opposite type"
	        );
	    }
	}
		
		
		
	
	
	private void validateParticipant(Conversation conversation, User currentUser) {

	    boolean exists =
	            participantRepository.existsByConversationAndUser(conversation, currentUser);

	    if (!exists) {
	        throw new IllegalArgumentException("Access denied");
	    }
	}
	
	@Transactional
	public ConversationDTO createPrivateConversation(String otherUserPublicID) {
	    User currentUser = getCurrentUser();
	    User otherUser = userRepository
	            .findByPublicID(otherUserPublicID)
	            .orElseThrow(() -> new UserNotFoundException(otherUserPublicID));
	    if (currentUser.equals(otherUser)) {
	        throw new IllegalArgumentException("Cannot create conversation with yourself");
	    }
	    // تحقق من وجود محادثة بين المستخدمين فقط
	    Optional<Conversation> existing =
	            participantRepository.findConversationBetweenUsers(currentUser, otherUser);
	    if (existing.isPresent()) {
	        // تحقق أن المحادثة تحتوي على مشاركين فقط
	        Conversation conv = existing.get();
	        if (conv.getParticipants().size() == 2) {
	            return generalMapper.toDTO(conv);
	        }
	    }
	    validateChatPermission(currentUser, otherUser);
	    Conversation conversation = new Conversation();
	    conversation.setConversationPublicID(UUID.randomUUID().toString());
	    conversation.setCreatedAt(LocalDateTime.now());
	    conversationRepository.save(conversation);
	    ConversationParticipant p1 = new ConversationParticipant();
	    p1.setConversation(conversation);
	    p1.setUser(currentUser);
	    ConversationParticipant p2 = new ConversationParticipant();
	    p2.setConversation(conversation);
	    p2.setUser(otherUser);
	    participantRepository.saveAll(List.of(p1, p2));
	    return generalMapper.toDTO(conversation);
	}
	





	@Transactional
	public MessageDTO sendMessage(String conversationPublicID, String content) {

	    User currentUser = getCurrentUser();

	    Conversation conversation =
	            conversationRepository
	                    .findByConversationPublicID(conversationPublicID)
	                    .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

	    validateParticipant(conversation, currentUser);

	    Message message = new Message();
	    message.setMessagePublicID(UUID.randomUUID().toString());
	    message.setContent(content);
	    message.setConversation(conversation);
	    message.setSender(currentUser);
	    message.setCreatedAt(LocalDateTime.now());

	    messageRepository.save(message);

	    conversation.setLastMessagePreview(
	            content.length() > 50 ? content.substring(0, 50) : content
	    );
	    conversation.setLastMessageAt(LocalDateTime.now());
	    conversation.setUpdatedAt(LocalDateTime.now());
	    
	    conversationRepository.save(conversation);
	    
	    
	    participantRepository.incrementUnreadCount(conversation, currentUser);

	    return generalMapper.toDTO(message);
	}

	public Page <ConversationDTO> getUserConversations(PaginationAndSortDTO dto) {
		
		
		
		
		Pageable pageable = PageRequest.of(
                dto.getPage(),
                dto.getSize(),
                dto.getSort() != null ? dto.getSort().toSpringSort() : Sort.by("lastMessageAt").descending()
        );		
		
	    User currentUser = getCurrentUser();
	    Page<Conversation> page = conversationRepository.findAllByParticipantsUser(currentUser, pageable);
	    
	    for (Conversation conversation : page) {
	    	
	    	validateParticipant(conversation, currentUser);
	    }
	    return page.map(generalMapper::toDTO);
	}

	@Transactional
	public Page<MessageDTO> getConversationMessages(String conversationPublicID,
	                                                PaginationAndSortDTO dto) {

	    User currentUser = getCurrentUser();

	    Conversation conversation =
	            conversationRepository
	                    .findByConversationPublicID(conversationPublicID)
	                    .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

	    // 🔐 تأكد أن المستخدم participant
	    validateParticipant(conversation, currentUser);

	    Pageable pageable = PageRequest.of(
	            dto.getPage(),
	            dto.getSize(),
	            Sort.by("createdAt").descending()
	    );

	    Page<Message> page =
	            messageRepository.findAllByConversation(conversation, pageable);

	    // ✅ الآن فقط بعد التحقق
	    participantRepository.resetUnreadCount(conversation, currentUser);

	    return page.map(generalMapper::toDTO);
	}

	public void markConversationAsRead(String conversationPublicID) {
	    User currentUser = getCurrentUser();
	    Conversation conversation =
	            conversationRepository
	                    .findByConversationPublicID(conversationPublicID)
	                    .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

	    participantRepository.markAsRead(conversation, currentUser);
	}
	
	
	
	
}