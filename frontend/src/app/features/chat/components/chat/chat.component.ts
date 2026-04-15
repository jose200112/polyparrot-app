import { Component, OnInit, OnDestroy, Input, ViewChild, ElementRef, AfterViewChecked, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { AuthService } from '../../../../core/services/auth.service';
import { ChatService } from '../../service/chat.service'; // ← añadir
import { Subscription } from 'rxjs';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.css'
})
export class ChatComponent implements OnInit, OnDestroy, AfterViewChecked {

  @Input() receiverId!: number;
  @Input() receiverName!: string;
  @ViewChild('messagesContainer') messagesContainer!: ElementRef;
  @Input() showBackButton = false;
  @Output() backClicked = new EventEmitter<void>();

  messages: any[] = [];
  newMessage = '';
  currentUserId!: number;
  currentUserName!: string;
  conversationId = '';
  private stompClient!: Client;
  connected = false;
  isReceiverOnline = false; 
  private presenceSub!: Subscription;

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private chatService: ChatService 
  ) {}



ngOnInit() {
  this.currentUserId = Number(this.authService.getUserId());
  this.currentUserName = this.authService.getName();
  this.conversationId = this.buildConversationId(this.currentUserId, this.receiverId);
  this.loadHistory();
  this.connectWebSocket();
  this.checkPresence();

  this.presenceSub = this.chatService.presence$.subscribe(({ userId, online }) => {
    if (userId === this.receiverId) {
      this.isReceiverOnline = online;
    }
  });
}

ngOnDestroy() {
  if (this.stompClient) this.stompClient.deactivate();
  if (this.presenceSub) this.presenceSub.unsubscribe(); // ← añadir
}

  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  buildConversationId(id1: number, id2: number): string {
    const min = Math.min(id1, id2);
    const max = Math.max(id1, id2);
    return `${min}_${max}`;
  }

  loadHistory() {
    this.http.get<any[]>(
      `${environment.chatServiceUrl}/chat/conversation/${this.currentUserId}/${this.receiverId}`
    ).subscribe({
      next: (res) => {
        this.messages = res;
        this.markAsRead();
      },
      error: () => {}
    });
  }

  markAsRead() {
    this.http.patch(
      `${environment.chatServiceUrl}/chat/conversation/${this.currentUserId}/${this.receiverId}/read`, {}
    ).subscribe();
  }

  checkPresence() {
    this.http.get<boolean>(
      `${environment.chatServiceUrl}/chat/presence/${this.receiverId}`
    ).subscribe({
      next: (online) => this.isReceiverOnline = online,
      error: () => this.isReceiverOnline = false
    });
  }

  connectWebSocket() {
    const token = this.authService.getToken();

    this.stompClient = new Client({
      webSocketFactory: () => new SockJS(`${environment.chatServiceUrl}/ws`),
  connectHeaders: {
  Authorization: `Bearer ${token}`,
  'X-Session-Type': 'chat'
},
      onConnect: () => {
        this.connected = true;
        this.stompClient.subscribe(
          `/topic/conversation/${this.conversationId}`,
          (message) => {
            const event = JSON.parse(message.body);

            switch (event.type) {

              case 'MESSAGE':
                if (!this.messages.find(m => m.id === event.id)) {
                  this.messages.push(event);
                  this.chatService.notifyNewMessage(event);
                  if (event.senderId !== this.currentUserId) {
                    this.markAsRead();
                  }
                }
                break;

              case 'READ':
                if (event.readerId !== this.currentUserId) {
                  this.messages.forEach(m => {
                    if (m.senderId === this.currentUserId) {
                      m.read = true;
                    }
                  });
                }
                break;

              case 'PRESENCE':
                if (event.userId === this.receiverId) {
                  this.isReceiverOnline = event.online;
                }
                break;

              default:
                // Mensajes sin type (los anteriores a este cambio)
                if (!this.messages.find(m => m.id === event.id)) {
                  this.messages.push(event);
                  this.chatService.notifyNewMessage(event);
                }
            }
          }
        );
      },
      onDisconnect: () => this.connected = false
    });

    this.stompClient.activate();
  }

  sendMessage() {
    if (!this.newMessage.trim() || !this.connected) return;

    const dto = {
      senderId: this.currentUserId,
      senderName: this.currentUserName,
      receiverId: this.receiverId,
      receiverName: this.receiverName,
      content: this.newMessage.trim()
    };

    this.stompClient.publish({
      destination: '/app/send',
      body: JSON.stringify(dto)
    });

    this.newMessage = '';
  }

  scrollToBottom() {
    try {
      this.messagesContainer.nativeElement.scrollTop =
        this.messagesContainer.nativeElement.scrollHeight;
    } catch (e) {}
  }

  isOwn(message: any): boolean {
    return message.senderId === this.currentUserId;
  }

  
}