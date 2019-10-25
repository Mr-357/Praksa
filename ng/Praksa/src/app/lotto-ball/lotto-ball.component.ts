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
        transform: 'rotate(0deg) translateX(0px) translateY(200px) rotate(-0deg)'
      }), {params : {length:15}}),
      transition('first=>second', animate('9000ms ease-in')),
      transition('second=>first', animate('50ms')),
      transition('*=>third',animate('150ms'))
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
        speed : this.randSpeed()
      }
    }
    this.currentState = this.states.pop();
    this.states.unshift(this.currentState);
    console.log(this.currentState);
  }
  @Input() number;
  displayNumber;
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
   anim(){
    this.currentState='third';
    this.changeState();
   }

  ngOnInit() {
    if(this.number<10){
      this.displayNumber='0'+this.number;
    }
    else{this.displayNumber=this.number;}
  }

}
