import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { AuthService } from './auth.service';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

@Injectable({ providedIn: 'root' })
export class NotificationService {

  private newNotificationSubject = new Subject<any>();
  newNotification$ = this.newNotificationSubject.asObservable();
  private stompClient?: Client;
  private readAllSubject = new Subject<void>();
  readAll$ = this.readAllSubject.asObservable();

  constructor(private authService: AuthService) {}

  connect() {
    const userId = this.authService.getUserId();
    const token = this.authService.getToken();
    if (!userId || !token) return;

    this.stompClient = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8083/ws-notifications'),
      connectHeaders: {
        Authorization: `Bearer ${token}` 
      },
      onConnect: () => {
        this.stompClient?.subscribe(
          `/topic/notifications/${userId}`,
          (message) => {
            const notification = JSON.parse(message.body);
            this.newNotificationSubject.next(notification);
          }
        );
      }
    });

    this.stompClient.activate();
  }

  disconnect() {
    this.stompClient?.deactivate();
    this.stompClient = undefined;
  }

notifyReadAll() {
  this.readAllSubject.next();
}
}