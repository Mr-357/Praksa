import { Injectable } from '@angular/core';
import { Ticket, NumberHolder, IDHolder } from './ticket'
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { TicketFieldComponent } from './ticket-field/ticket-field.component';
import { BsModalService, BsModalRef } from 'ngx-bootstrap/modal';//
import { ModalComponent } from './modal/modal.component';//
import { HttpHeaders } from '@angular/common/http';

const httpOptions = {
  headers: new HttpHeaders({
    'responseType': 'text' as 'json'
  })
};
@Injectable({
  providedIn: 'root'
})
export class TicketsService {

  activeModal: BsModalRef;
  values: number[] = [];
  fields: TicketFieldComponent[] = [];
  private markedNum: BehaviorSubject<Number> = new BehaviorSubject<Number>(this.values.length);
  markedNum$ = this.markedNum.asObservable();
  constructor(private http: HttpClient, private modalService: BsModalService) { }

  register(field: TicketFieldComponent) {
    this.fields.push(field);
  }

  init() {
    return this.http.get('http://localhost:8080/prepare');
  }

  public showModal(message: String, title: String) {
    this.activeModal = this.modalService.show(ModalComponent);
    this.activeModal.content.setModal(this.activeModal);
    this.activeModal.content.setContent(title, message);
  }


  clear() {
    this.fields.forEach(x => x.reset());
    this.values = [];
    this.markedNum.next(this.values.length);
  }

  reset() {
    return this.http.get('http://localhost:8080/reset');
  }

  getNumbers() {
    return this.values;
  }

  getPages(): Observable<Number> {
    return this.http.get<Number>('http://localhost:8080/pages');
  }

  generateRandom() {
    this.clear();
    let randomList = [];
    for (let i = 1; i <= 39; i++) {
      randomList.push(i);
    }
    for (let i = 0; i < 7; i++) {
      let cmp = randomList.splice(Math.floor(Math.random() * randomList.length), 1)[0];
      this.fields.forEach(x => {
        if (x.value == cmp) {
          x.mark();
        }
      })
    }

  }

  getTickets(page): Observable<Ticket[]> {
    return this.http.get<Ticket[]>('http://localhost:8080/tickets?pageNumber=' + page);
  }

  getTicket(id: string): Observable<Ticket> {
    return this.http.get<Ticket>('http://localhost:8080/tickets/single?id=' + id);
  }

  createTicket(ticket: NumberHolder) {
    return this.http.post<IDHolder>('http://localhost:8080/register', ticket);
  }

  addNumber(value: number): Observable<number> {
    if (this.values.length == 7 && this.values.find(x => x == value) == undefined) {
      return throwError("Maximum number of numbers has been chosen");
    }
    if ((this.values.find(x => x == value) != undefined)) {
      this.values.splice(this.values.indexOf(value), 1);
    }
    else {
      this.values.push(value);

    }


    this.markedNum.next(this.values.length);

    return new Observable((value) => value.next());
  }

  drawNumber(): Observable<number> {
    return this.http.get<number>('http://localhost:8080/draw');
  }

  private handleError(error: HttpErrorResponse) {//change
    if (error.error instanceof ErrorEvent) {
      // A client-side or network error occurred. Handle it accordingly.
      console.error('An error occurred:', error.error.message);
    } else {
      // The backend returned an unsuccessful response code.
      // The response body may contain clues as to what went wrong,
      console.error(
        `Backend returned code ${error.status}, ` +
        `body was: ${error.error.message}`);
    }
    // return an observable with a user-facing error message
    //this.showModal(error.error.message,"Error");
    return throwError(
      'Something bad happened; please try again later.');
  };
}
