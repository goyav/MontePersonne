package com.ascenseur.elevator.modele;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
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
    int goldenFloor;
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
        this.executor = Executors.newCachedThreadPool();
    }




    /**
     *
     * @param call
     * @return
     */
    private int moveElevator(Call call) {
        Logger.getLogger("does the logger shows").log(new LogRecord(Level.INFO,"move elevator"));
        while (currentFloor != call.toFloor.getFloorNumber()) {
            currentFloor= summonElevator(call);
            // Simulate movement time (replace with actual movement logic)
            try {
                Thread.sleep(300); //TODO parametrer et decider si pertinent
                Logger.getLogger("move Elevator").log(Level.INFO,"sleep for 300 ms");
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            Logger.getLogger("Elevator arrived at floor: " ).log(Level.INFO,"Elevator arrived at floor: " + call.toFloor);
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

        callArrayList.add(elevator.operateElevator((new Call(ElevatorFloor.FLOOR_1, ElevatorFloor.FLOOR_5))));
        callArrayList.add(elevator.operateElevator((new Call(ElevatorFloor.FLOOR_3, ElevatorFloor.FLOOR_8))));
        callArrayList.add(elevator.operateElevator((new Call(ElevatorFloor.FLOOR_7, ElevatorFloor.FLOOR_2))));
        callArrayList.add(elevator.operateElevator((new Call(ElevatorFloor.FLOOR_4, ElevatorFloor.FLOOR_9))));
        callArrayList.add(elevator.operateElevator((new Call(ElevatorFloor.FLOOR_2, ElevatorFloor.FLOOR_6))));
        callArrayList.add(elevator.operateElevator((new Call(ElevatorFloor.FLOOR_8, ElevatorFloor.FLOOR_3))));
        callArrayList.add(elevator.operateElevator((new Call(ElevatorFloor.FLOOR_5, ElevatorFloor.FLOOR_1))));
        callArrayList.add(elevator.operateElevator((new Call(ElevatorFloor.FLOOR_9, ElevatorFloor.FLOOR_4))));
        callArrayList.add(elevator.operateElevator((new Call(ElevatorFloor.FLOOR_6,ElevatorFloor.FLOOR_10))));
        callArrayList.add(elevator.operateElevator((new Call(ElevatorFloor.FLOOR_2, ElevatorFloor.FLOOR_7))));
        callArrayList.add(elevator.operateElevator((new Call(ElevatorFloor.FLOOR_10,ElevatorFloor.FLOOR_5))));
        callArrayList.add(elevator.operateElevator((new Call(ElevatorFloor.FLOOR_1, ElevatorFloor.FLOOR_8))));
        callArrayList.add(elevator.operateElevator((new Call(ElevatorFloor.FLOOR_4,ElevatorFloor.FLOOR_10))));
        callArrayList.add(elevator.operateElevator((new Call(ElevatorFloor.FLOOR_7, ElevatorFloor.FLOOR_3))));
        callArrayList.add(elevator.operateElevator((new Call(ElevatorFloor.FLOOR_9, ElevatorFloor.FLOOR_2))));

        sortCalls(callArrayList);
        System.out.println("sorted");

       return callArrayList;
    }

    int summonElevator(Call call){
       return call.fromFloor.getFloorNumber();
    }


    /**
     * Method takes object Call which is added to a callQueue which will be processed
     * @param call
     * @return
     * @throws InterruptedException
     */
    //TODO will need to return return value when call will be made via button listener
    public Call operateElevator(Call call) throws InterruptedException {
        validateCall(call.fromFloor.getFloorNumber(), call.toFloor.getFloorNumber());
        callQueue.add(new Call(call.fromFloor, call.toFloor));
        try {
            processCalls();
            Thread.sleep(300); //TODO parametrer et decider si pertinent de garder le sleep
        }catch (Exception e){
            System.out.println("exception " + e + " was caught");
        }
        return call;
    }

    private void validateCall(int fromFloor, int toFloor) {
        if (fromFloor < 1 || fromFloor > floorCount || toFloor < 1 || toFloor > floorCount) {
            throw new IllegalArgumentException("Invalid floor number");
        }
    }


    int setGoldenFloor(Call call, int direction){
        int floorToReach = -999;

        if (direction >0){
            if (call.toFloor.getFloorNumber() > goldenFloor){
                floorToReach = call.toFloor.getFloorNumber();
            }
        }else {
            if (call.toFloor.getFloorNumber() < goldenFloor){
                floorToReach = call.toFloor.getFloorNumber();
            }
        }
         return floorToReach;
    }

    int getDirection(Call call){
        int direction = (call.toFloor.getFloorNumber() > call.fromFloor.getFloorNumber()) ? 1 : -1;
        return direction;
    }

    private void processCalls() {

        // TODO add goal floor in one direction if all calls follow and change if number greater in said direction
        // change goal floor after direction change and primary goal floor is reached
        // priority is still the main driver, it only takes the back seat on two conditions
        // going in the same direction or calls inside bracket of first call

        executor.submit(() -> {
            while (!callQueue.isEmpty()) {
                Call call = null; // Wait for next call or timeout
                try {
                    call = callQueue.poll(1, TimeUnit.SECONDS);
                    callQueue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (call != null) {
                    goldenFloor = setGoldenFloor(call, getDirection(call));
                    Logger.getLogger("process calls").log(Level.INFO,"processing call -- from: " + call.fromFloor() + " to: " + call.toFloor());
                    moveElevator(call);
                    Logger.getLogger("Elevator at floor: " + currentFloor).log(Level.INFO,"Elevator at floor: " + currentFloor);
                }
            }
        });
    }



    public record Call(ElevatorFloor fromFloor, ElevatorFloor toFloor) {
    }



}
