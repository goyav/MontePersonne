package com.ascenseur.elevator.events;

import com.ascenseur.elevator.modele.Elevator;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ElevatorButtonListener implements ApplicationListener<CallElevatorEvent> {

    @Override
    public void onApplicationEvent(CallElevatorEvent event) {

        try {
            Elevator.getElevator1().operateElevator(event.getCall());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
