export class Ticket{
    id:string;
    numbers:string;
    dateCreated:Date;
}
export class NumberHolder{
    constructor(){
        this.numbers=[];
    }
    convert(ticket:Ticket){
        this.numbers = ticket.numbers.split(',').map(x=>parseInt(x));
    }
    numbers:number[];
}
export class IDHolder {
    ID:String;
}