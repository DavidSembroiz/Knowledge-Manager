package iot;

public class Event implements Comparable<Event> {

	private String person;
	private String action;
	private int step;
	
	public Event(String person, String action, int step) {
		this.person = person;
		this.action = action;
		this.step = step;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	@Override
	public int compareTo(Event other) {
		return Integer.compare(step, other.getStep());
	}
}
