package com.ascenseur.elevator.events;

import com.ascenseur.elevator.modele.Elevator;
import org.springframework.context.ApplicationEvent;

public class CallElevatorEvent extends ApplicationEvent {
    private Elevator.Call call;

    CallElevatorEvent(Publisher source, Elevator.Call call){
        super(source);
        this.call = call;
    }

    public Elevator.Call getCall(){
        return this.call;
    }

}
