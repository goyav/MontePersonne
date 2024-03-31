package com.ascenseur.elevator.modele;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class Elevator {
    static final int MAXFLOOR = 15;
    static final int MINFLOOR = -2;
    private final int floorCount;
    private final BlockingQueue<Call> callQueue;
    private final ExecutorService executor;
    ArrayList calls = new ArrayList();
    private int currentFloor;
    //TODO two elevators, but only using one. Implement the use of two in future version
    private static Elevator elevator1;
    private static Elevator elevator2;

    public static Elevator getElevator1() {
        return new Elevator(15);
    }

    private Elevator(int floorCount) {
        this.floorCount = floorCount;
        this.currentFloor = ElevatorFloor.FLOOR_0.getFloorNumber(); // Assuming starts at ground floor
        this.callQueue = new LinkedBlockingQueue<>();
        this.executor = Executors.newSingleThreadExecutor();
    }


    /**
     * Method to move elevator
     * @param fromFloor
     * @param toFloor
     */
    private int moveElevator(ElevatorFloor fromFloor, ElevatorFloor toFloor) {
        int direction = (toFloor.getFloorNumber() > fromFloor.getFloorNumber()) ? 1 : -1;
        while (currentFloor != toFloor.getFloorNumber()) {
            currentFloor += direction;
            // Simulate movement time (replace with actual movement logic)
            try {
                Thread.sleep(300); //TODO parametrer et decider si pertinent
                System.out.println("call is made or what");
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            Logger.getLogger("Elevator arrived at floor: " + currentFloor);
            System.out.println(currentFloor);
        }
        return currentFloor;
    }

//TODO sort idea for calls ======> check order of call first then check if other call is in intervall


    /**
     * finds the highest or lowest call from initial call in queue in order to know when to revert direction
     * @param linkedBlockingQueue
     * @return List of object Call
     */
    private List<Call> sortCalls(LinkedBlockingQueue<Call> linkedBlockingQueue) {

        return linkedBlockingQueue
                .stream()
                .sorted(Comparator
                        .<Call>comparingInt(call -> call.fromFloor().getFloorNumber())
                        .thenComparingInt(call -> call.toFloor().getFloorNumber()))
                .toList();
    }


    public Boolean highestFloor(Integer currentFloor, LinkedBlockingQueue<Call> linkedBlockingQueue){

        int highest = Integer.MIN_VALUE; // Initialize with minimum possible int value
        for (Call call : linkedBlockingQueue) {
            highest = Math.max(highest, call.toFloor().getFloorNumber()); // Update highest if current element is greater
        }
        return currentFloor==highest;

    }


    public Boolean lowestFloor(Integer currentFloor, LinkedBlockingQueue<Call> linkedBlockingQueue){

        int lowest = Integer.MAX_VALUE; // Initialize with maximum possible int value
        for (Call call : linkedBlockingQueue) {
            lowest = Math.min(lowest, call.toFloor().getFloorNumber()); // Update lowest if current element is lower
        }
        return currentFloor==lowest;

    }


    public LinkedBlockingQueue<Call> testMethod() throws InterruptedException {
        LinkedBlockingQueue<Call> callArrayList = new LinkedBlockingQueue<>();

        Elevator elevator = Elevator.getElevator1();

        callArrayList.add(elevator.callElevator((new Call(ElevatorFloor.FLOOR_1, ElevatorFloor.FLOOR_5))));
        callArrayList.add(elevator.callElevator((new Call(ElevatorFloor.FLOOR_3, ElevatorFloor.FLOOR_8))));
        callArrayList.add(elevator.callElevator((new Call(ElevatorFloor.FLOOR_7, ElevatorFloor.FLOOR_2))));
        callArrayList.add(elevator.callElevator((new Call(ElevatorFloor.FLOOR_4, ElevatorFloor.FLOOR_9))));
        callArrayList.add(elevator.callElevator((new Call(ElevatorFloor.FLOOR_2, ElevatorFloor.FLOOR_6))));
        callArrayList.add(elevator.callElevator((new Call(ElevatorFloor.FLOOR_8, ElevatorFloor.FLOOR_3))));
        callArrayList.add(elevator.callElevator((new Call(ElevatorFloor.FLOOR_5, ElevatorFloor.FLOOR_1))));
        callArrayList.add(elevator.callElevator((new Call(ElevatorFloor.FLOOR_9, ElevatorFloor.FLOOR_4))));
        callArrayList.add(elevator.callElevator((new Call(ElevatorFloor.FLOOR_6,ElevatorFloor.FLOOR_10))));
        callArrayList.add(elevator.callElevator((new Call(ElevatorFloor.FLOOR_2, ElevatorFloor.FLOOR_7))));
        callArrayList.add(elevator.callElevator((new Call(ElevatorFloor.FLOOR_10,ElevatorFloor.FLOOR_5))));
        callArrayList.add(elevator.callElevator((new Call(ElevatorFloor.FLOOR_1, ElevatorFloor.FLOOR_8))));
        callArrayList.add(elevator.callElevator((new Call(ElevatorFloor.FLOOR_4,ElevatorFloor.FLOOR_10))));
        callArrayList.add(elevator.callElevator((new Call(ElevatorFloor.FLOOR_7, ElevatorFloor.FLOOR_3))));
        callArrayList.add(elevator.callElevator((new Call(ElevatorFloor.FLOOR_9, ElevatorFloor.FLOOR_2))));




        System.out.println("called");
        sortCalls(callArrayList);
        System.out.println("sorted");

       return callArrayList;
    }

    public Call callElevator(Call call) throws InterruptedException {
        validateCall(call.fromFloor.getFloorNumber(), call.toFloor.getFloorNumber());
        callQueue.add(new Call(call.fromFloor, call.toFloor));
        try {
            processCalls();
            Thread.sleep(300); //TODO parametrer et decider si pertinent
        }catch (Exception e){
            System.out.println(e);
        }
        return call;
    }

    private void validateCall(int fromFloor, int toFloor) {
        if (fromFloor < 1 || fromFloor > floorCount || toFloor < 1 || toFloor > floorCount) {
            throw new IllegalArgumentException("Invalid floor number");
        }
    }

    private void processCalls() {
        System.out.println("processing calls");

        executor.submit(() -> {
            while (!callQueue.isEmpty()) {
                Call call = null; // Wait for next call or timeout
                try {
                    call = callQueue.poll(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (call != null) {
                        moveElevator(call.fromFloor(), call.toFloor());
                    Logger.getLogger("Elevator at floor: " + currentFloor);
                }
            }
        });
    }



    public record Call(ElevatorFloor fromFloor, ElevatorFloor toFloor) {
    }

}
