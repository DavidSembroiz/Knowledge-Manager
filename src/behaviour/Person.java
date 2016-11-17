package behaviour;

import behaviour.PeopleManager.Action;
import behaviour.PeopleManager.State;
import behaviour.PeopleManager.Type;
import domain.Debugger;

public class Person {
	
	private String name;
	private State currentState;
	private Action currentAction;
	private String location;
    private String pastLocation;
    private String type;
	private UserProfile profile;
	private UserParams params;
	private int nextActionSteps;
	private int remainingSteps;
	private boolean acting;
	
	public Person(String name, String type, UserProfile prof, UserParams param) {
		this.currentAction = Action.MOVE;
		this.currentState = State.OUTSIDE;
		this.name = name;
        this.type = type;
		this.profile = prof;
		this.params = param;
		this.nextActionSteps = -9999;
		this.remainingSteps = -9999;
		this.location = "";
		this.acting = false;
	}

	public int getRemainingSteps() {
		return remainingSteps;
	}
	
	public void decreaseRemainingSteps() {
		--remainingSteps;
	}

	public int getNextActionSteps() {
		return nextActionSteps;
	}
	
	public void decreaseNextActionSteps() {
		--nextActionSteps;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public State getCurrentState() {
		return currentState;
	}

	public UserProfile getProfile() {
		return profile;
	}

	public boolean isActing() {
		return acting;
	}

	public void setActing(boolean acting) {
		this.acting = acting;
	}

	public Action getCurrentAction() {
		return currentAction;
	}

	public UserParams getParams() {
		return params;
	}

	public void assignAction(Action a, String dest, int next, int duration) {
		this.currentAction = a;
		this.location = dest;
		this.nextActionSteps = next;
		this.remainingSteps = duration;
        this.setActing(false);
		if (Debugger.isEnabled()) {
			Debugger.log("Action " + a.toString() +
						 " assigned to Person " + this.getName() +
					     " next " + next +
					     " duration " + duration);
		}
											   
	}

	public String getLocation() {
		return location;
	}

	public void changeState() {
		if (Debugger.isEnabled()) Debugger.log("Executing action...");
		if (currentAction.equals(Action.MOVE)) {
			if (Debugger.isEnabled()) Debugger.log("Person " + this.getName() +
					   " changed from " + currentState.toString() +
					   " to " + State.ROOM.toString());
			currentState = State.ROOM;
			
		}
		else if (currentAction.equals(Action.ENTER)) {
			if (Debugger.isEnabled()) Debugger.log("Person " + this.getName() +
					   " changed from " + currentState.toString() +
					   " to " + State.INSIDE.toString());
			currentState = State.INSIDE;
		}
		else if (currentAction.equals(Action.EXIT)) {
			if (Debugger.isEnabled()) Debugger.log("Person " + this.getName() +
					   " changed from " + currentState.toString() +
					   " to " + State.OUTSIDE.toString());
			currentState = State.OUTSIDE;
		}
		else if (currentAction.equals(Action.LUNCH)) {
			if (Debugger.isEnabled()) Debugger.log("Person " + this.getName() +
					   " changed from " + currentState.toString() +
					   " to " + State.SALON.toString());
			currentState = State.SALON;
		}
	}

	public boolean isInside() {
		return !(currentState.equals(State.OUTSIDE));
	}
	
	public boolean hadLunch() {
		return params.hadLunch();
	}

	public void setHadLunch(boolean b) {
		params.setHadLunch(b);
	}

    public boolean hadEntered() {
        return params.hadEntered();
    }

    public void sethadEntered(boolean b) {
        params.setHadEntered(b);
    }

    public boolean isProfessor() {
        return type.equals(Type.PROFESSOR.toString().toLowerCase());
    }

    public boolean isStudent() {
        return type.equals(Type.STUDENT.toString().toLowerCase());
    }

    public boolean isPas() {
        return type.equals(Type.PAS.toString().toLowerCase());
    }

    public String getPastLocation() {
        return pastLocation;
    }

    public void setPastLocation(String pastLocation) {
        this.pastLocation = pastLocation;
    }
}
