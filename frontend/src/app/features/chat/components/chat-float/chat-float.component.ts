import { Component, OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../../core/services/auth.service';
import { ChatComponent } from '../chat/chat.component';
import { ChatService } from '../../service/chat.service';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

@Component({
  selector: 'app-chat-float',
  standalone: true,
  imports: [CommonModule, ChatComponent],
  templateUrl: './chat-float.component.html',
  styleUrl: './chat-float.component.css'
})
export class ChatFloatComponent implements OnInit, OnDestroy {

  isOpen = false;
  activeConversation: any = null;
  conversations: any[] = [];
  currentUserId!: number;
  private pollInterval: any;
  private stompClient!: Client;


  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private chatService: ChatService
  ) {}



ngOnInit() {
  this.currentUserId = Number(this.authService.getUserId());
  this.loadConversations();
  this.pollInterval = setInterval(() => this.loadConversations(), 30000);
  this.connectGlobalWebSocket(); 

  this.chatService.openChat$.subscribe(({ receiverId, receiverName }) => {
    this.openConversationWith(receiverId, receiverName);
  });

  this.chatService.onNewMessage$.subscribe((msg) => {
    this.updateConversationPreview(msg);
  });
}

ngOnDestroy() {
  clearInterval(this.pollInterval);
  if (this.stompClient) this.stompClient.deactivate(); 
}

connectGlobalWebSocket() {
  const token = this.authService.getToken();

  this.stompClient = new Client({
    webSocketFactory: () => new SockJS('http://localhost:8084/ws'),
connectHeaders: {
  Authorization: `Bearer ${token}`,
  'X-Session-Type': 'presence'
},
    onConnect: () => {
      // Suscribirse al topic personal de presencia
      this.stompClient.subscribe(
        `/topic/presence/${this.currentUserId}`,
        (message) => {
          const event = JSON.parse(message.body);
          if (event.type === 'PRESENCE') {
            // Actualizar estado online en la lista de conversaciones
            const conv = this.conversations.find(
              c => c.receiverId === event.userId
            );
            if (conv) {
              conv.online = event.online;
            }
            // Notificar al chat abierto si es con esa persona
            this.chatService.notifyPresence(event.userId, event.online);
          }
        }
      );
    }
  });

  this.stompClient.activate();
}

updateConversationPreview(msg: any) {
  const otherId = msg.senderId === this.currentUserId
    ? msg.receiverId
    : msg.senderId;

  const conv = this.conversations.find(c => c.receiverId === otherId);
  if (conv) {
    conv.lastMessage = msg.content;
    // Solo sumar badge si el chat con ese usuario NO está abierto ahora mismo
    if (msg.senderId !== this.currentUserId &&
        this.activeConversation?.receiverId !== otherId) {
      conv.unreadCount = (conv.unreadCount || 0) + 1;
    }
  }
}



  loadConversations() {
    this.http.get<any[]>(`http://localhost:8084/chat/conversations/${this.currentUserId}`)
      .subscribe({
        next: (res) => this.conversations = res,
        error: () => {}
      });
  }

  toggleOpen() {
    this.isOpen = !this.isOpen;
    if (!this.isOpen) this.activeConversation = null;
  }

openConversation(conversation: any) {
  const conv = this.conversations.find(c => c.receiverId === conversation.receiverId);
  if (conv) conv.unreadCount = 0;

  this.activeConversation = null;
  setTimeout(() => {
    this.activeConversation = conversation;
  }, 50);
}

openConversationWith(receiverId: number, receiverName: string) {
  this.isOpen = true;

  const conv = this.conversations.find(c => c.receiverId === receiverId);
  if (conv) conv.unreadCount = 0;

  this.activeConversation = null;
  setTimeout(() => {
    this.activeConversation = { receiverId, receiverName };
  }, 50);
}

  backToList() {
    this.activeConversation = null;
  }

  get totalUnread(): number {
    return this.conversations.reduce((sum, c) => sum + (c.unreadCount || 0), 0);
  }

  
}