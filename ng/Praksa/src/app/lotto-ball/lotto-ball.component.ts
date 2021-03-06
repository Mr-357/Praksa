import { Component, OnInit,Input } from '@angular/core';
import { trigger, state, style, animate, transition } from '@angular/animations';
var length = 15;

@Component({
  selector: 'app-lotto-ball',
  templateUrl: './lotto-ball.component.html',
  styleUrls: ['./lotto-ball.component.css'],
  animations: [
    trigger('changeDivSize', [
      state('first', style({
        transform: 'rotate(0deg) translateX(0px) translateY(0px) rotate(0deg)'
      }),{params : {length:15}}),
      state('second', style({
        transform: 'rotate({{speed}}deg) translateX(-{{length}}px) translateY(-{{length}}px) rotate(-{{speed}}deg)'
      }), {params : {length:15,speed:10000}}),
      state('third', style({
        transform: 'rotate(0deg) translateX(160px) translateY({{offset}}px) rotate(-0deg)'
      }), {params : {offset:0}}),
      transition('first=>second', animate('9000ms ease-in')),
      transition('second=>first', animate('50ms')),
      transition('*=>third',animate('250ms ease-out'))
    ]),
  ]
})
export class LottoBallComponent implements OnInit {
  currentState = 'first';
  states = [];
  animationConfig;
  changeState() {
    
    this.animationConfig= {
      value: this.currentState,// === 'initial' ? 'final' : 'initial',
      params:{
        length : this.rand(),
        speed : this.randSpeed(),
        offset: this.offset
      }
    }
    this.currentState = this.states.pop();
    this.states.unshift(this.currentState);
  }
  @Input() number;
  displayNumber;
  offset=-85;
  constructor() { 
    this.states.push('first');
    this.states.push('second');
    this.currentState='first';
   // setInterval(()=>this.changeState(),5950);
  }
  rand(){
   return Math.floor(Math.random()*60)+20;
  }
  randSpeed(){
    return Math.floor(Math.random()*3000)+6000;
   }
   anim(offset){
    this.currentState='third';
    this.offset = offset-85;
    this.changeState();
   }
   goBack(){
     this.currentState='first';
     this.changeState();
   }
  ngOnInit() {
    if(this.number<10){
      this.displayNumber='0'+this.number;
    }
    else{this.displayNumber=this.number;}
  }

}
