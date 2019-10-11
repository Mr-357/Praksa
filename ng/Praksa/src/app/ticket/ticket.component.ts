import { Component, OnInit, Input, AfterViewInit } from '@angular/core';
import { Ticket, NumberHolder } from '../ticket';
import { TicketsService } from '../tickets.service';

@Component({
  selector: 'app-ticket',
  templateUrl: './ticket.component.html',
  styleUrls: ['./ticket.component.css']
})
export class TicketComponent implements OnInit,AfterViewInit {

  ticketHelper:NumberHolder;
  mode:string="create";
  @Input() ticket:Ticket;
  holder:NumberHolder;
  constructor(private ticketService:TicketsService) { 
    this.ticketHelper=new NumberHolder();
    this.ticketHelper.numbers= [];
    for(let i = 1;i<=39;i++)
    {
      this.ticketHelper.numbers.push(i);
    }
  }
  
  ngOnInit() {
    if(this.ticket!=null){
      this.mode="draw";
      this.holder = new NumberHolder();
      this.holder.convert(this.ticket);
      this.ticketService.fields = [];
    }
  }

  picked(i:number):boolean{
    if(this.ticket!=null){
      return this.holder.numbers.includes(i);
    }
    return false;
  }

  ngAfterViewInit(): void {
 
  }

}
