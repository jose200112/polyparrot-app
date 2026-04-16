package com.polyparrot.chatservice.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.polyparrot.chatservice.entity.Conversation;

public interface ConversationRepository extends MongoRepository<Conversation, String> {

    @Query("{ $or: [ { 'userId1': ?0 }, { 'userId2': ?0 } ] }")
    List<Conversation> findByUserId(Long userId);

    Optional<Conversation> findByConversationId(String conversationId);
}