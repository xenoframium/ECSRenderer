package xenoframium.ecsrender;

import xenoframium.ecs.Component;
import xenoframium.ecs.Observable;
import xenoframium.ecs.Observer;
import xenoframium.glmath.linearalgebra.Mat4;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisjung on 1/10/17.
 */
public class PositioningComponent implements Component, Observable {
    Set<Observer> observers = new HashSet<>();
    Mat4 modelMatrix = new Mat4();

    void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(this);
        }
    }

    public Mat4 getModelMatrix() {
        return new Mat4(modelMatrix);
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }
}
