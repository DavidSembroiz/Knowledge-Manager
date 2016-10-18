package behaviour;

import behaviour.PeopleManager.Action;
import behaviour.PeopleManager.State;

public class Person {
	
	private String name;
	private State currentState;
	private Action currentAction;
	private UserProfile profile;
	private UserParams params;
	private int nextActionSteps;
	private int remainingSteps;
	private boolean acting;
	
	public Person(String name, UserProfile prof, UserParams param) {
		this.currentState = State.OUTSIDE;
		this.name = name;
		this.profile = prof;
		this.params = param;
		acting = false;
	}
	
	public void assignState(State st) {
		this.currentState = st;
	}

	public int getRemainingSteps() {
		return remainingSteps;
	}

	public void setRemainingSteps(int remainingSteps) {
		this.remainingSteps = remainingSteps;
	}
	
	public void decreaseRemainingSteps() {
		--remainingSteps;
	}

	public int getNextActionSteps() {
		return nextActionSteps;
	}

	public void setNextActionSteps(int nextActionSteps) {
		this.nextActionSteps = nextActionSteps;
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

	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}

	public UserProfile getProfile() {
		return profile;
	}

	public void setProfile(UserProfile profile) {
		this.profile = profile;
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

	public void setCurrentAction(Action currentAction) {
		this.currentAction = currentAction;
	}

	public UserParams getParams() {
		return params;
	}

	public void setParams(UserParams params) {
		this.params = params;
	}
	
	

}
