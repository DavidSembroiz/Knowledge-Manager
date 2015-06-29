package behaviour;

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
	
	public Person(String name, String location, State state, Type type) {
		this.name = name;
		this.location = location;
		this.state = state;
		this.type = type;
		this.changed = false;
		this.eaten = false;
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
}
