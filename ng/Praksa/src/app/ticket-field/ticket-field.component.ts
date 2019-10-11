import { Component, OnInit, Input } from '@angular/core';
import { TicketsService } from '../tickets.service';

@Component({
  selector: 'app-ticket-field',
  templateUrl: './ticket-field.component.html',
  styleUrls: ['./ticket-field.component.css']
})
export class TicketFieldComponent implements OnInit {

  constructor(private ticketService:TicketsService) {
    this.marked=false;
  }
  marked:boolean;
  ngOnInit() {
    this.ticketService.register(this);
    if(this.picked)
      this.marked=true;
  }
  
  @Input() value;
  @Input() mode;
  @Input() picked;
  @Input() hit;
  @Input() miss;
  reset(){
    this.marked=false;
  }

  mark(){
    if(this.ticketService.addNumber(this.value).subscribe(value=>this.value).closed==false ){
      if(this.mode=="create")
      this.marked=!this.marked;
    }else
    {
      this.ticketService.showModal("You have picked all 7 numbers","Cannot pick any more numbers");
    }
  }
}
