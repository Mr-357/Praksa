import { Component, OnInit, ViewChildren, QueryList, ElementRef } from '@angular/core';
import { Ticket, NumberHolder } from '../ticket'
import { TicketsService } from '../tickets.service';
import {CookieService} from 'ngx-cookie-service'
import { Router } from '@angular/router';
@Component({
  selector: 'app-ticketcreator',
  templateUrl: './ticketcreator.component.html',
  styleUrls: ['./ticketcreator.component.css']
})
export class TicketcreatorComponent implements OnInit {

  constructor(private ticketService:TicketsService,private cookies:CookieService,private router:Router) { 
   
  }

  num;

  ngOnInit() {
    
   this.ticketService.markedNum$.subscribe(x=>this.num=x);
   
  }

  refresh(){
    window.location.reload();
  }


  resetTicket(){
    this.ticketService.clear();
  }

  randomTicket(){
    this.ticketService.generateRandom();
  }

  async registerTicket() {
    let numbers = new NumberHolder;
    numbers.numbers=this.ticketService.getNumbers();
    if(numbers.numbers.length==7)
    {
     
      await this.ticketService.createTicket(numbers).subscribe(async id=>
        {
          this.cookies.delete('ticket-id');
          await this.cookies.set('ticket-id',id.ID.toString())
        });
      this.resetTicket();
      setTimeout(()=>{this.router.navigate(['/draw'])},100);
    }
    else{
      this.ticketService.showModal("You have not picked all 7 numbers","Invalid ticket");
    }
  }
}
