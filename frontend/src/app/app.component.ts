import { Component, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div style="font-family: sans-serif; padding: 20px; max-width: 600px; margin: auto;">
      <h2>ARIA - PoC RAG Local</h2>
      
      <div style="border: 1px solid #ccc; padding: 10px; height: 350px; overflow-y: auto; margin-bottom: 10px; border-radius: 8px; background: #f9f9f9;">
        <div *ngFor="let msg of messages()" style="margin-bottom: 10px;">
          <strong [style.color]="msg.role === 'User' ? 'blue' : 'green'">{{ msg.role }}:</strong> 
          <span style="white-space: pre-wrap;">{{ msg.text }}</span>
        </div>
      </div>

      <div style="display: flex; gap: 10px;">
        <input [(ngModel)]="question" placeholder="Pergunte sobre os PDFs..." style="flex: 1; padding: 10px; border-radius: 4px; border: 1px solid #ccc;" (keyup.enter)="send()" />
        <button (click)="send()" [disabled]="isLoading()" style="padding: 10px 20px; cursor: pointer; background: #0056b3; color: white; border: none; border-radius: 4px;">Enviar</button>
      </div>
      
      <p *ngIf="isLoading()" style="color: gray; font-style: italic; margin-top: 10px;">Consultando o agente... 🤖</p>
    </div>
  `
})
export class AppComponent {
  private http = inject(HttpClient);
  
  question = '';
  messages = signal<{role: string, text: string}[]>([]);
  isLoading = signal(false);

  send() {
    if (!this.question.trim()) return;
    
    this.messages.update(m => [...m, { role: 'Bradesco', text: this.question }]);
    this.isLoading.set(true);
    
    const currentQ = this.question;
    this.question = '';

    this.http.post<{ answer: string }>('/api/chat', { question: currentQ }).subscribe({
      next: (res) => {
        this.messages.update(m => [...m, { role: 'ARIA', text: res.answer }]);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.messages.update(m => [...m, { role: 'Sistema', text: 'Erro ao conectar com o backend Spring.' }]);
        this.isLoading.set(false);
      }
    });
  }
}
