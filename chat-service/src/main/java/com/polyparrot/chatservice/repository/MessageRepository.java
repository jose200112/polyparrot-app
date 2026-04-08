package com.polyparrot.chatservice.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.polyparrot.chatservice.entity.Message;

public interface MessageRepository extends MongoRepository<Message, String> {

 List<Message> findByConversationIdOrderBySentAtAsc(String conversationId);

 long countByConversationIdAndReceiverIdAndReadFalse(String conversationId, Long receiverId);
}