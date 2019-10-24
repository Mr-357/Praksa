import { Component, OnInit,Input } from '@angular/core';
import { trigger, state, style, animate, transition } from '@angular/animations';
var length =15;

@Component({
  selector: 'app-lotto-ball',
  templateUrl: './lotto-ball.component.html',
  styleUrls: ['./lotto-ball.component.css'],
  animations: [
    trigger('changeDivSize', [
      state('first', style({
        transform: 'rotate(-360deg) translateX({{length}}px) translateY({{length}}px) rotate(360deg)'
      }),{params : {length:15}}),
      state('second', style({
        transform: 'rotate(360deg) translateX(-{{length}}px) translateY(-{{length}}px) rotate(-360deg)'
      }), {params : {length:15}}),
      state('third', style({
        transform: 'rotate(-360deg) translateX({{length}}px) translateY({{length}}px) rotate(360deg)'
      }), {params : {length:15}}),
      transition('*=>*', animate('1000ms')),
     

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
        length : this.rand()
      }
    }
    this.currentState = this.states.shift();
    this.states.push(this.currentState);
    console.log(this.currentState);
  }
  @Input() number;
  constructor() { 
    this.states.push('first');
    this.states.push('second');
    this.states.push('third');
    setInterval(()=>this.changeState(),1300);
  }
  rand(){
   return Math.floor(Math.random()*150);
    //console.log(length);
  }


  ngOnInit() {
  }

}
