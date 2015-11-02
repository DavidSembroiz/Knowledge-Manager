package behaviour;

import domain.Utils;

public class Person {
	
	public enum State {
		OUTSIDE, INSIDE, RANDOM_WALKS, LUNCH, UNASSIGNED
	}
	
	public enum Type {
		PROFESSOR, PAS, STUDENT
	}
	
	private String name;
	private String location;
	private State state;
	private Type type;
	private boolean changed;
	private boolean eaten;
	private boolean entered;
	private int lunchReturn;
	private int randomWalksReturn;
	private int numRandomWalks;
	
	private int insideTime;
	
	public Person(String name, String location, State state, Type type) {
		this.name = name;
		this.location = location;
		this.state = state;
		this.type = type;
		this.changed = false;
		this.eaten = false;
		this.entered = false;
		this.lunchReturn = -1;
		this.randomWalksReturn = -1;
		this.numRandomWalks = 0;
		this.insideTime = 0;
	}
	
	

	public int getInsideTime() {
		return insideTime;
	}

	public void setInsideTime(int insideTime) {
		this.insideTime = insideTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public boolean hasChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	public boolean hasEaten() {
		return eaten;
	}

	public void setEaten(boolean eaten) {
		this.eaten = eaten;
	}
	
	public boolean hasEntered() {
		return entered;
	}

	public void setEntered(boolean entered) {
		this.entered = entered;
	}
	
	public int getLunchReturn() {
		return lunchReturn;
	}
	
	public void setLunchReturn(int current, int duration) {
		this.lunchReturn = current + duration;
	}
	
	public int getRandomWalksReturn() {
		return randomWalksReturn;
	}
	
	public void setRandomWalksReturn(int current, int duration) {
		this.randomWalksReturn = current + duration;
	}
	
	public void addRandomWalk() {
		this.numRandomWalks++;
	}
	
	public boolean canRandomWalk() {
		return numRandomWalks < Utils.MAX_RANDOM_WALKS;
	}
}
