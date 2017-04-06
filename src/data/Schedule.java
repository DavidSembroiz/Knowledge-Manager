package data;

import behaviour.Person;
import iot.Manager;
import javafx.util.Pair;

import java.util.ArrayList;


public class Schedule {

    public class Element {
        private String elementType;
        private int elementIndex;
        private ArrayList<Pair<Integer, String>> times;

        Element(String elementId, ArrayList<Pair<Integer, String>> times) {
            this.elementType = elementId.split("_")[0];
            this.elementIndex = Integer.parseInt(elementId.split("_")[1]);
            this.times = times;
        }

        public String getElementType() {
            return elementType;
        }

        public int getElementIndex() {
            return elementIndex;
        }

        public ArrayList<Pair<Integer, String>> getTimes() {
            return times;
        }

        void updateTime(int jump) {
            if (times.size() == 1 && times.get(0).getKey() <= Manager.CURRENT_STEP) {
                times.set(0, new Pair(times.get(0).getKey() - jump, times.get(0).getValue()));
            }
            else {
                for (int i = 0; i < times.size() - 1; ++i) {
                    Pair<Integer, String> t1 = times.get(i);
                    Pair<Integer, String> t2 = times.get(i + 1);
                    if (t1.getKey() <= Manager.CURRENT_STEP && t2.getKey() > Manager.CURRENT_STEP) {
                        times.set(i, new Pair(t1.getKey() - jump, t1.getValue()));
                        return;
                    }
                }
            }
        }
    }


    private String _id;
    private ArrayList<Element> elements;
    private String _rev;

    public Schedule(String _id, ArrayList<Element> elements) {
        this.elements = elements;
        this._id = _id;
    }

    public void adjustSchedule(int jump, Person p) {
        Element comp = getComputerById(p);
        Element hvac = getHvac();
        if (comp != null) comp.updateTime(jump);
        if (hvac != null) hvac.updateTime(jump);

    }

    private Element getHvac() {
        for (Element e : elements) {
            if (e.getElementType().equals("hvac")) return e;
        }
        return null;
    }

    private Element getComputerById(Person p) {
        for (Element e : elements) {
            if (e.getElementType().equals("computer") && e.getElementIndex() == p.getParams().getComputerId()) {
                return e;
            }
        }
        return null;
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

    String get_rev() {
        return _rev;
    }

    void set_rev(String _rev) {
        this._rev = _rev;
    }

    public ArrayList<Element> getElements() {
        return elements;
    }

    public String get_id() {
        return _id;
    }

    private Element contains(String elementId) {

        for (Element e : elements) {
            String current = e.getElementType() + "_" + e.getElementIndex();
            if (current.equals(elementId)) return e;
        }
        return null;
    }
}
