package com.ascenseur.elevator.modele;

import lombok.Getter;

@Getter
public enum ElevatorFloor {
    FLOOR_P2(-2),
    FLOOR_P1(-1),
    FLOOR_0(0),
    FLOOR_1(1),
    FLOOR_2(2),
    FLOOR_3(3),
    FLOOR_4(4),
    FLOOR_5(5),
    FLOOR_6(6),
    FLOOR_7(7),
    FLOOR_8(8),
    FLOOR_9(9),
    FLOOR_10(10);

    private int floorNumber;

    ElevatorFloor(int floorNumber) {
        this.floorNumber = floorNumber;
    }
}
