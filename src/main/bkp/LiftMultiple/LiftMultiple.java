package com.test;

import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Data
class Lift{
    int floors;
    int capacity;
    int currentCapacity;
    int currentFloor;
    HashSet<Integer> incomingRequests;
    Map<Integer,Integer> outgoingRequests;
    LiftState currentState,movingUpState,movingDownState,movingUpToPickFirst,movingDownToPickFirst,idleState;
    public Lift(int capacity,int numberOfFloors){
        this.capacity = capacity;
        this.floors = numberOfFloors;
        this.currentFloor = 0;
        this.currentCapacity = 0;
        this.incomingRequests = new HashSet<>();
        this.outgoingRequests = new HashMap<>();
        this.idleState = new IdleState(this);
        this.currentState = idleState;
        this.movingUpState = new MovingUpState(this);
        this.movingDownState = new MovingDownState(this);
        this.movingUpToPickFirst = new MovingUpToPickFirst(this);
        this.movingDownToPickFirst = new MovingDownToPickFirst(this);
    }

    public void setState(char direction){
        if(direction == 'U'){
            this.currentState = movingUpState;
            return;
        }
        if(direction == 'D'){
            this.currentState = movingDownState;
            return;
        }
        this.currentState = idleState;
    }

    public void tick(){
        this.currentState.tick();
        if(incomingRequests.isEmpty() && outgoingRequests.isEmpty()) {
            this.setState('I');
        }
    }

    public char getDirection(){
        return currentState.getDirection();
    }

    public int getTimeToReachFloor(int floor,char direction){
        return this.currentState.timeToReachFloor(floor,direction);
    }

    public int getCurrentPeopleCount(){
        int count = 0;
        for(Integer i:outgoingRequests.values()){
            count = count+i;
        }
        return count;
    }

    public int countPeople(int floor,char direction){
        return this.currentState.countPeople(floor,direction);
    }

    void addIncomingRequest(int floor,char direction){
        if(this.getDirection() == 'I'){
            if(floor == currentFloor){
                this.setState(direction);
            }else if(floor>currentFloor){
                this.currentState = direction == 'U' ? movingUpState : movingUpToPickFirst;
            }else{
                this.currentState = direction == 'D' ? movingDownState : movingDownToPickFirst;
            }
        }
        this.incomingRequests.add(floor);
    }

    void addOutgoingRequest(int floor,char direction){
        this.outgoingRequests.put(floor,outgoingRequests.getOrDefault(floor,0)+1);
    }

}

abstract class LiftState{
    protected Lift lift;
    LiftState(Lift lift){
        this.lift = lift;
    }

    public abstract int timeToReachFloor(int floor,char direction);
    public abstract char getDirection();
    public int countPeople(int floor,char direction){
        return 0;
    }
    public abstract void tick();
}

class MovingUpState extends LiftState{
    MovingUpState(Lift lift){
        super(lift);
    }
    @Override
    public int timeToReachFloor(int floor,char direction){
        if(direction != lift.getDirection() || lift.getCurrentFloor()>floor){
            return -1;
        }
        return floor-lift.getCurrentFloor();
    }
    @Override
    public char getDirection(){
        return 'U';
    }
    @Override
    public int countPeople(int floor,char direction){
        int count =0;
        if(direction != 'U'){
            return 0;
        }
        for(Integer flrNumber:lift.outgoingRequests.keySet()){
            if(flrNumber>floor){
                count = count+lift.outgoingRequests.getOrDefault(flrNumber,0);
            }
        }
        return count;
    }

    @Override
    public void tick() {
        lift.incomingRequests.remove(lift.getCurrentFloor());
        if (lift.incomingRequests.isEmpty() && lift.outgoingRequests.isEmpty()) {
            return;
        }
        lift.setCurrentFloor(lift.getCurrentFloor() + 1);
        lift.outgoingRequests.remove(lift.getCurrentFloor());
    }
}

class MovingDownState extends LiftState{
    MovingDownState(Lift lift){
        super(lift);
    }
    @Override
    public int timeToReachFloor(int floor,char direction){
        if(direction != lift.getDirection() || lift.getCurrentFloor()<floor){
            return -1;
        }
        return lift.getCurrentFloor()-floor;
    }
    @Override
    public char getDirection(){
        return 'D';
    }
    @Override
    public int countPeople(int floor,char direction){
        int count =0;
        if(direction != 'D'){
            return 0;
        }
        for(Integer flrNumber:lift.outgoingRequests.keySet()){
            if(flrNumber<floor){
                count = count+lift.outgoingRequests.getOrDefault(flrNumber,0);
            }
        }
        return count;
    }

    @Override
    public void tick(){
        lift.incomingRequests.remove(lift.getCurrentFloor());
        if(lift.incomingRequests.isEmpty() && lift.outgoingRequests.isEmpty()){
            return;
        }
        lift.setCurrentFloor(lift.getCurrentFloor()-1);
        lift.outgoingRequests.remove(lift.getCurrentFloor());
    }
}

class IdleState extends LiftState{
    IdleState(Lift lift){
        super(lift);
    }
    @Override
    public int timeToReachFloor(int floor,char direction){
        return Math.abs(lift.getCurrentFloor()-floor);
    }
    @Override
    public char getDirection(){
        return 'I';
    }

    @Override
    public void tick(){
    }
}

class MovingUpToPickFirst extends LiftState{
    public MovingUpToPickFirst(Lift lift){
        super(lift);
    }

    @Override
    public int timeToReachFloor(int floor,char direction){
        int nextStop = nextStop();
        if(direction!='D' || floor > nextStop)
            return -1;
        return Math.abs(2*nextStop-lift.getCurrentFloor()-floor);
    }
    @Override
    public char getDirection(){
        return 'U';
    }

    private int nextStop(){
        int stop = -1;
        for(Integer next: lift.incomingRequests){
            if(stop<next)
                stop = next;
        }
        return stop;
    }

    @Override
    public void tick(){
        lift.setCurrentFloor(lift.getCurrentFloor()+1);
        int nextStop = nextStop();
        if(lift.getCurrentFloor() == nextStop){
            lift.setState('D');
        }
    }

}

class MovingDownToPickFirst extends LiftState{
    public MovingDownToPickFirst(Lift lift){
        super(lift);
    }

    @Override
    public int timeToReachFloor(int floor,char direction){
        int nextStop = nextStop();
        if(direction != 'U' || nextStop > floor){
            return -1;
        }
        return Math.abs(lift.getCurrentFloor()+floor-2*nextStop);
    }
    @Override
    public char getDirection(){
        return 'D';
    }

    private int nextStop(){
        int stop = lift.floors+1;
        for(Integer next: lift.incomingRequests){
            if(stop>next)
                stop = next;
        }
        return stop;
    }

    @Override
    public void tick(){
        lift.setCurrentFloor(lift.getCurrentFloor()-1);
        int nextStop = nextStop();
        if(lift.getCurrentFloor() == nextStop){
            lift.setState('U');
        }
    }

}

class LiftSystem{

    int numberOfFloor;
    int capacity;
    int numberOfLifts;
    private Lift[] lifts;
    public LiftSystem(){
    }

    public void init(int numberOfFloor,int capacity,int numberOfLifts){
        this.numberOfLifts = numberOfLifts;
        this.numberOfFloor = numberOfFloor;
        this.capacity = capacity;
        lifts  = new Lift[numberOfLifts];
        for(int i=0;i<numberOfLifts;i++)
            lifts[i] = new Lift(capacity,numberOfFloor);
    }

    public int requestLift(int floor,char direction){
        int timeToReach = -1;
        int lift = -1;
        for(int i=0;i < numberOfLifts;i++){
            int t = lifts[i].getTimeToReachFloor(floor,direction);
            if(t <0  || lifts[i].countPeople(floor,direction) >= capacity)
                continue;
            if( timeToReach < 0 || t < timeToReach) {
                timeToReach = t;
                lift = i;
            }
        }
        if(lift >= 0 && lift < numberOfLifts){
            lifts[lift].addIncomingRequest(floor,direction);
        }
        return lift;
    }

    public void pressButtonForFloor(int liftIndex,int floor){
        lifts[liftIndex].addOutgoingRequest(floor,lifts[liftIndex].getDirection());
    }

    public String getLiftStatus(int liftIndex){
        if(liftIndex < 0 || liftIndex>=numberOfLifts)
            return "";
        Lift lift = lifts[liftIndex];
        return lift.getCurrentFloor()+"-"+lift.getDirection()+"-"+lift.getCurrentPeopleCount();
    }

    public void tick(){
        for(Lift l : lifts){
            l.tick();
        }
    }
}


public class LiftMultiple {

    public static void main(String []args){
        LiftSystem liftSystem = new LiftSystem();
        liftSystem.init(6,2,2);
        liftSystem.requestLift(0, 'U');
        liftSystem.requestLift(5, 'D');
        liftSystem.pressButtonForFloor(0, 4);
        liftSystem.tick();
        System.out.println(liftSystem.getLiftStatus(0));
        System.out.println(liftSystem.getLiftStatus(1));
        liftSystem.requestLift(3, 'D');
        liftSystem.requestLift(6, 'D');
        liftSystem.requestLift(0, 'U');
        liftSystem.tick();
        liftSystem.tick();
        System.out.println(liftSystem.getLiftStatus(0));
        System.out.println(liftSystem.getLiftStatus(1));
        liftSystem.tick();
        System.out.println(liftSystem.getLiftStatus(0));
        System.out.println(liftSystem.getLiftStatus(1));
    }

}
