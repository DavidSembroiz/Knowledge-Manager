package data;

import javafx.util.Pair;

import java.util.ArrayList;


public class Schedule {


    private class Element {
        private String elementId;
        private ArrayList<Pair<Integer, String>> times;

        Element(String elementId, ArrayList<Pair<Integer, String>> times) {
            this.elementId = elementId;
            this.times = times;
        }

        String getElementId() {
            return elementId;
        }


        ArrayList<Pair<Integer, String>> getTimes() {
            return times;
        }

    }


    private String _id;
    private ArrayList<Element> elements;

    public Schedule(String _id, ArrayList<Element> elements) {
        this.elements = elements;
        this._id = _id;
    }

    public void addTimeToSchedule(String elementId, int time, String st) {
        Element e = contains(elementId);
        if (e != null) {
            e.getTimes().add(new Pair<>(time, st));
        }
        else {
            ArrayList<Pair<Integer, String>> times = new ArrayList<>();
            times.add(new Pair<>(time, st));
            this.elements.add(new Element(elementId, times));
        }
    }

    public String get_id() {
        return _id;
    }

    private Element contains(String elementId) {
        for (Element e : elements) if (e.getElementId().equals(elementId)) return e;
        return null;
    }
}
