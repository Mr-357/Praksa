import { Component, OnInit, ViewChild, ViewChildren, QueryList, OnDestroy } from '@angular/core';
import { TicketsService } from '../tickets.service';
import { CookieService } from 'ngx-cookie-service';
import { Ticket } from '../ticket';
import { TicketComponent } from '../ticket/ticket.component';
import * as Stomp from 'webstomp-client';
import * as SockJS from 'sockjs-client';
import { Observable } from 'rxjs';
import { isArray } from 'util';
import { LottoBallComponent } from '../lotto-ball/lotto-ball.component';



@Component({
  selector: 'app-ticket-draw',
  templateUrl: './ticket-draw.component.html',
  styleUrls: ['./ticket-draw.component.css']
})
export class TicketDrawComponent implements OnInit,OnDestroy {
  ngOnDestroy(): void {
    this.disconnect();
  }

  //#region WebSocket service?
  serverURL = "http://116.202.13.157:8080/socket";
  ws;
  private stompClient;
  connect(): Promise<any> { //change to promise/observable
    return new Promise((resolve, reject) => {
      this.ws = new SockJS(this.serverURL);
      this.stompClient = Stomp.over(this.ws);
      this.stompClient.debug= () => {} ;
      this.stompClient.connect({},
        (result) => resolve(result),
        (error) => reject(error)
      )
    });
  }
  subscribeTopic(): Observable<any> {
    return new Observable(observer => {
      this.stompClient.subscribe("/app/draw", (message) => {
        observer.next(JSON.parse(message.body));
      })
    })
  }
  subscribeNumbers(): Observable<any> {
    return new Observable(observer => {
      this.stompClient.subscribe("/draw", (message) => {
        observer.next(JSON.parse(message.body));
      })
    })
  }
  sendEmptyMessage() {
    this.stompClient.send("/app/draw/start", {});
  }
  disconnect() {
    if (this.ws != null)
      this.ws.close();
    console.log("Disconnected");
  }
  //#endregion
  
  @ViewChild('ticket', { static: false }) ticket: TicketComponent;
  @ViewChildren(LottoBallComponent) balls : QueryList<LottoBallComponent>;
  last: number;
  threes; fours; fives; sixes; sevens;
  winningCombo = [];
  latestTicket;
  ready = false;
  ballnumbers=[];
  constructor(private ticketService: TicketsService, private cookies: CookieService) { 
    this.winningCombo = []; 
    for(let i=1;i<=39;i++){
      this.ballnumbers.push(i);
    }
  }

  async ngOnInit() {
    this.ready = false;
    //this.ticketService.reset().subscribe();
    this.ticketService.init().subscribe(complete => this.ready = true);
    console.log(this.cookies.get('ticket-id'));
    if (this.checkTicket() == true) {
      this.ticketService.showModal("Please create a ticket first", "No tickets created");
    }
    else {
      await this.ticketService.getTicket(this.cookies.get('ticket-id')).toPromise().then(ticket => this.latestTicket = ticket);
    }
    this.connect().then((result) => {
      console.log("Connected");
      this.subscribeTopic().subscribe((message) => this.populate(message));
      this.subscribeNumbers().subscribe((message) =>{
        this.populate(message);
        this.stop();
      } );
    }).catch((error) => console.log(error));
    this.balls.forEach(x=>x.changeState());
  }

  test() {
    this.sendEmptyMessage();
    this.balls.forEach(x=>x.changeState());
  }
  stop(){
    this.balls.forEach(x=>{
      if(!this.winningCombo.includes(x.number))
      {
        setTimeout(()=>x.changeState(),500);
        setTimeout(()=>x.changeState(),650);
      }
    });
  }
  pull(){
    let n = Math.floor(Math.random()*38)+1;
    this.balls.find(x=>x.number==(n)).anim(0);
  }

  checkTicket() {
    return this.cookies.get('ticket-id').length == 0;
  }

  populate(response) {   //change to stats model?
    if(isArray(response))
    {
      response.forEach(x=>this.populate(x));
    }
    else{
      this.last = response.lastDrawn;
      this.threes = response.threes;
      this.fours = response.fours;
      this.fives = response.fives;
      this.sixes = response.sixes;
      this.sevens = response.sevens;
      this.winningCombo.push(response.lastDrawn);
      this.winningCombo.sort((a, b) => a - b);
      let field = this.ticketService.fields.find(x => x.value == this.last);
      let ball = this.balls.find(x=>x.number == response.lastDrawn);
      ball.anim((this.winningCombo.length-1)*30);
      if (field.marked == true) {
        field.hit = true;
      }
      else {
        field.miss = true;
      }
      if(this.winningCombo.length==7){
        this.resetBalls();
      }
    }
    
  }
  resetBalls() {
    this.balls.forEach(x=>{
      if(!this.winningCombo.includes(x.number))
      {
        setTimeout(()=>x.goBack(),100);
      }
    });
  }
}
