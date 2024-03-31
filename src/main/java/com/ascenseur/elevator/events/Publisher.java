package com.ascenseur.elevator.events;

import com.ascenseur.elevator.modele.Elevator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;



@Component
public class Publisher {
    @Autowired
    private final ApplicationEventPublisher eventPublisher;

    public Publisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    void publishEvent(final Elevator.Call call) {
        CallElevatorEvent callElevatorEvent = new CallElevatorEvent(this, call);
        eventPublisher.publishEvent(callElevatorEvent);
    }}
