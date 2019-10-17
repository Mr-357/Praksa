import { Component, OnInit, ViewChild } from '@angular/core';
import { TicketsService } from '../tickets.service';
import { CookieService } from 'ngx-cookie-service';
import { Ticket } from '../ticket';
import { TicketComponent } from '../ticket/ticket.component';


import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';


@Component({
  selector: 'app-ticket-draw',
  templateUrl: './ticket-draw.component.html',
  styleUrls: ['./ticket-draw.component.css']
})
export class TicketDrawComponent implements OnInit {

  //#region WebSocket
  private stompClient;
  test(){
   
    this.stompClient.send("/app/draw/start",{});
    
  }
  initSocket(){
    let websocket = new SockJS("http://localhost:8080/socket");
    this.stompClient = Stomp.over(websocket);
    let that = this; // 
    this.stompClient.connect({}, function(frame) {
      that.stompClient.subscribe("/draw", (message) =>{
        if(message.body)
          console.log(message.body);
          that.populate(message.body);
      });
    });
  }
  //#endregion
  @ViewChild('ticket',{static:false}) ticket:TicketComponent;
  last:number;
  winningCombo=[];
  latestTicket;
  constructor(private ticketService:TicketsService,private cookies:CookieService) { this.winningCombo=[];  this.initSocket();}

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

  }
  checkTicket(){
    console.log(this.cookies.get('ticket-id').length==0);
    return this.cookies.get('ticket-id').length==0;
  }
  populate(last:number){
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
