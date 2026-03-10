package com.techJob.controller.rest;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techJob.DTOs.PaginationAndSortDTO;
import com.techJob.DTOs.chat.ConversationDTO;
import com.techJob.DTOs.chat.MessageDTO;
import com.techJob.response.ApiResponse;
import com.techJob.response.ApiResponseFactory;
import com.techJob.service.ChatService;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatRestController {

	private final ChatService chatService;

	public ChatRestController(ChatService chatService) {
		super();
		this.chatService = chatService;
	}
	
	
	
	@GetMapping("/conversations")
	public ResponseEntity<ApiResponse<Page<ConversationDTO>>> getUserConversations(
			@RequestParam (required =false) PaginationAndSortDTO dto ) {

		Page<ConversationDTO> conversations = chatService.getUserConversations(dto);
		return ApiResponseFactory.ok(conversations,"Conversations retrieved successfully");
	}
	//test 
	@PostMapping("/conversations/{userId}")
	public ResponseEntity<ApiResponse<ConversationDTO>> createConversation(
			@PathVariable String userId) {

		ConversationDTO conversation = chatService.createPrivateConversation(userId);
		return ApiResponseFactory.ok(conversation,"Conversation created successfully");
	}
	
	@GetMapping("/{conversationId}/messages")
	public ResponseEntity<ApiResponse<Page<MessageDTO>>> getConversationMessages(
			@PathVariable String conversationId,
			@RequestParam (required =false) PaginationAndSortDTO dto) {

		Page<MessageDTO> messages = chatService.getConversationMessages(conversationId, dto);
		
		return ApiResponseFactory.ok(messages,"Messages retrieved successfully");
	}
	
	
	
	@PostMapping("/{conversationId}/messages")
	public ResponseEntity<ApiResponse<MessageDTO>> sendMessage(
			@PathVariable String conversationId,
			@RequestBody String content) {

		MessageDTO message= chatService.sendMessage(conversationId, content);
		
		return ApiResponseFactory.ok(message,"Message sent successfully");
	}
	
}