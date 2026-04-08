import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  private openChatSubject = new Subject<{ receiverId: number, receiverName: string }>();
  openChat$ = this.openChatSubject.asObservable();

  private newMessageSubject = new Subject<any>();
  onNewMessage$ = this.newMessageSubject.asObservable();

  private presenceSubject = new Subject<{ userId: number, online: boolean }>(); 
  presence$ = this.presenceSubject.asObservable();

  openChatWith(receiverId: number, receiverName: string) {
    this.openChatSubject.next({ receiverId, receiverName });
  }

  notifyNewMessage(msg: any) {
    this.newMessageSubject.next(msg);
  }



notifyPresence(userId: number, online: boolean) {
  this.presenceSubject.next({ userId, online });
}
}