import { Component, OnInit, TemplateRef, ViewChild, Input } from '@angular/core';
import { BsModalService, BsModalRef} from 'ngx-bootstrap/modal'
import { TicketsService } from '../tickets.service';
import { TouchSequence } from 'selenium-webdriver';
@Component({
  selector: 'app-modal',
  templateUrl: './modal.component.html',
  styleUrls: ['./modal.component.css']
})
export class ModalComponent implements OnInit {

  public modalRef:BsModalRef;
  title:String;
  message:String;
  constructor() { 
  
  }
  ngOnInit() {
 
  }
  setModal(value){
    this.modalRef=value;
  }

  setContent(title:String,message:String){
    this.title=title;
    this.message=message;
  }

  click(){
    
  }
  

}
