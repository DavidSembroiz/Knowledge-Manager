package behaviour;

public class Person {
	
	public enum State {
		OUTSIDE, INSIDE, RANDOM_WALKS, LUNCH
	}
	
	public enum Type {
		PROFESSOR, PAS, STUDENT
	}
	
	private String name;
	private String room;
	private State state;
	private Type type;
	
	public Person(String name, String room, State state, Type type) {
		this.name = name;
		this.room = room;
		this.state = state;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
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
	
	
}
