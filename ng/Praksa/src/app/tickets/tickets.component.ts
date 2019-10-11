import { Component, OnInit } from '@angular/core';
import {from} from 'rxjs'
import { Ticket } from '../ticket';
import { TicketsService } from '../tickets.service';
import { Router, ActivatedRoute } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
@Component({
  selector: 'app-tickets',
  templateUrl: './tickets.component.html',
  styleUrls: ['./tickets.component.css']
})
export class TicketsComponent implements OnInit {

  tickets:Ticket[];
  pages:Number[]=[];
  current;
  myticket:String;
  constructor(private ticketsService:TicketsService, private router:Router, private active:ActivatedRoute,private cookies:CookieService) { 
   
  }

  ngOnInit() {
    this.myticket=this.cookies.get("ticket-id");
    if(this.myticket.length==0){
      this.myticket="invalid";
    }
    console.log(this.myticket);
    this.active.queryParams.subscribe(queryParams => {
      let loaded = queryParams["page"];
      if(loaded==null)
      {
        this.current=1;
      }
      else{
        this.current=+loaded;
      }
      this.getTickets(this.current);
      this.getPages();
    })
  }
  

  setPages(start,total){   // izuzetno ruzna fja za pagination ali radi kao
    if(total<=5)
    {
      for(let i=1;i<=5;i++)
      this.pages.push(i);
    }
    else if(start==2){
      this.pages.push(1);
      for(let i=1;i<=2;i++){
        this.pages.push(start+i-1);
      }
      this.pages.push(0);
      this.pages.push(total);
    }
    else if(total==start){

      this.pages.push(1);
        this.pages.push(0);
  
        for(let i=2;i>=1;i--){
          this.pages.push(total-i);
        }
        this.pages.push(total);
      }  
    else if(total - start == 1)
    {
      this.pages.push(1);
      this.pages.push(0);
      this.pages.push(total-2);
      this.pages.push(total-1);
      this.pages.push(total);
    }
  
    else if(start == 1)
    {
      for(let i=1;i<=3;i++)
      this.pages.push(start+i-1);
      this.pages.push(0);
      this.pages.push(total);
    }
  
    else{
      this.pages.push(start-1);
      this.pages.push(start);
      this.pages.push(start+1);
      this.pages.push(0);
      this.pages.push(total);
    }
  }

  getPages() {
    this.pages=[];
    this.ticketsService.getPages().subscribe(pages => {
      this.setPages(this.current,pages);
    })
  }

  getTickets(page):void { 
    this.ticketsService.getTickets(page).subscribe(tickets => this.tickets=tickets);
  }
}
