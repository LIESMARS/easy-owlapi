package cz.cvut.owl2query.model;

public class Timer {

    private final String name;

    private long last;
    private boolean running = false;

    public Timer(String string) {
        this.name = string;
    }

    public synchronized void start() {
        if ( running ) {
            throw new IllegalStateException("The timer must be stopped prior to restarting.");
        }

        running = true;
        last = System.currentTimeMillis();
    }

    public synchronized void stop() {
        if ( !running ) {
            throw new IllegalStateException("The timer must be started prior to stopping.");
        }
        last = System.currentTimeMillis() - last;
        running = false;
    }

    public long getLast() {
        if (running) {
            throw new IllegalStateException("The timer must be stopped prior to exposing time.");
        }
        return last;
    }
}
