package com.ascenseur.elevator.modele;

public enum ElevatorState {

    INSTANCE();

    private int position;

    private ElevatorState() {
        this.position = 0;
    }

    private ElevatorState getInstance() {
        return INSTANCE;
    }

    private int getPosition() {
        return position;
    }

    private void setPosition(int position) {
        this.position = position;
    }
}
