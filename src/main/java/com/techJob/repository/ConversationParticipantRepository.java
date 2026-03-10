package com.techJob.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.techJob.domain.entity.Conversation;
import com.techJob.domain.entity.ConversationParticipant;
import com.techJob.domain.entity.User;

public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, Long> {

	@Modifying
	@Query("""
			UPDATE ConversationParticipant cp
			SET cp.unreadCount = cp.unreadCount + 1
			WHERE cp.conversation = :conversation
			AND cp.user <> :sender
			""")
	void incrementUnreadCount(Conversation conversation, User sender);

	@Modifying
	@Query("""
			UPDATE ConversationParticipant cp
			SET cp.unreadCount = 0,
			    cp.lastReadAt = CURRENT_TIMESTAMP
			WHERE cp.conversation = :conversation
			AND cp.user = :user
			""")
	void markAsRead(Conversation conversation, User user);

	@Query("""
			SELECT CASE WHEN COUNT(cp) > 0 THEN true ELSE false END
			FROM ConversationParticipant cp
			WHERE cp.conversation = :conversation
			AND cp.user = :user
			""")
	boolean existsByConversationAndUser(Conversation conversation, User user);

	@Query("""
			SELECT c
			FROM Conversation c
			JOIN c.participants p1
			JOIN c.participants p2
			WHERE p1.user = :user1
			AND p2.user = :user2
			GROUP BY c
			HAVING COUNT(c) = 1 AND COUNT(c.participants) = 2
			""")
	Optional<Conversation> findConversationBetweenUsers(User user1, User user2);

	@Modifying
	@Query("""
			UPDATE ConversationParticipant cp
			SET cp.unreadCount = 0,
			    cp.lastReadAt = CURRENT_TIMESTAMP
			WHERE cp.conversation = :conversation
			AND cp.user = :user
			""")
	void resetUnreadCount(Conversation conversation, User user);

}