import { Component, OnInit, ViewChild } from '@angular/core';
import { TicketsService } from '../tickets.service';
import { CookieService } from 'ngx-cookie-service';
import { Ticket } from '../ticket';
import { TicketComponent } from '../ticket/ticket.component';
import * as Stomp from 'webstomp-client';
import * as SockJS from 'sockjs-client';
import { Observable } from 'rxjs';



@Component({
  selector: 'app-ticket-draw',
  templateUrl: './ticket-draw.component.html',
  styleUrls: ['./ticket-draw.component.css']
})
export class TicketDrawComponent implements OnInit {

  //#region WebSocket service?
  serverURL = "http://localhost:8080/socket";
  ws;
  private stompClient;
  connect():Promise<any>{ //change to promise/observable
    return new Promise( (resolve,reject) => {
      this.ws = new SockJS(this.serverURL);
      this.stompClient = Stomp.over(this.ws);
      this.stompClient.connect({}, 
        (result) => resolve(result),
        (error) => reject(error)
        )
    });
  }
  subscribeSocket():Observable<any>{
    return new Observable(observer => {
      this.stompClient.subscribe("/draw", (message)=>{
      observer.next(JSON.parse(message.body));
    })
  })
  }
  sendEmptyMessage(){
    this.stompClient.send("/app/draw/start",{});
  }
  disconnect(){
    if(this.ws!=null)
    this.ws.close();
    console.log("Disconnected");
  }
  //#endregion
  @ViewChild('ticket',{static:false}) ticket:TicketComponent;
  last:number;
  winningCombo=[];
  latestTicket;
  constructor(private ticketService:TicketsService,private cookies:CookieService) { this.winningCombo=[]; }

  async ngOnInit() {
    this.ticketService.reset().subscribe();
    console.log(this.cookies.get('ticket-id'));
    if(this.checkTicket()==true)
    {
        this.ticketService.showModal("Please create a ticket first","No tickets created");
    }
    else{
      await this.ticketService.getTicket(this.cookies.get('ticket-id')).toPromise().then(ticket=>this.latestTicket=ticket);
    }
    this.connect().then((result)=>{
      console.log("Connected");
      this.subscribeSocket().subscribe((message)=>this.populate(message));
    }).catch((error)=>console.log(error));
  }
  test(){
    this.sendEmptyMessage();
  }
  checkTicket(){
    console.log(this.cookies.get('ticket-id').length==0);
    return this.cookies.get('ticket-id').length==0;
  }
  populate(last:number){   //change to stats
    this.last=last;
    this.winningCombo.push(last);
    this.winningCombo.sort((a,b)=>a-b);
    let field = this.ticketService.fields.find(x=>x.value==last);
    if (field.marked==true){
      console.log("hit");
      field.hit=true;
    }
    else{
      field.miss=true;
      console.log("miss");
    }
    
  }

  draw(){
    if(this.checkTicket()==false)
    {
      this.ticketService.init().subscribe();
      for(let i=0;i<7;i++)
      {
        setTimeout(()=>{this.ticketService.drawNumber().subscribe(last=>{this.populate(last);});},1500*i); //replace with backend function?????
      }
    }
   
  }

}
