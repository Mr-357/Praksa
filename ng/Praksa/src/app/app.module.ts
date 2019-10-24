import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { TooltipModule } from 'ngx-bootstrap/tooltip';
import { ModalModule, BsModalRef } from 'ngx-bootstrap/modal';
import { AppComponent } from './app.component';
import { TicketsComponent } from './tickets/tickets.component';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { TicketcreatorComponent } from './ticketcreator/ticketcreator.component';
import { TicketFieldComponent } from './ticket-field/ticket-field.component';
import { TicketDrawComponent } from './ticket-draw/ticket-draw.component';
import { ModalComponent } from './modal/modal.component';
import { TicketComponent } from './ticket/ticket.component';
import { Routes, RouterModule } from '@angular/router';
import { ErrorComponentComponent } from './error-component/error-component.component'
import { CookieService } from 'ngx-cookie-service';
import { LottoBallComponent } from './lotto-ball/lotto-ball.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
const appRoutes:Routes = [
  {path: '',component: TicketsComponent},
  {path: 'pick',component: TicketcreatorComponent},       //child components
  {path: 'draw',component: TicketDrawComponent},
  {path: 'not-found', component: ErrorComponentComponent},
  {path:'**', redirectTo:'not-found'}
];

@NgModule({
  declarations: [
    AppComponent,
    TicketsComponent,
    TicketcreatorComponent,
    TicketFieldComponent,
    TicketDrawComponent,
    ModalComponent,
    TicketComponent,
    ErrorComponentComponent,
    LottoBallComponent
  ],
  imports: [
    BrowserModule,
    BsDropdownModule.forRoot(),
    TooltipModule.forRoot(),
    ModalModule.forRoot(),
    HttpClientModule,
    RouterModule.forRoot(appRoutes),
    BrowserAnimationsModule 
  ],
  providers: [BsModalRef,CookieService],
  bootstrap: [AppComponent],
  entryComponents: [ModalComponent]
})
export class AppModule { }
