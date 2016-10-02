package testsample.altvr.com.testsample.events;

import testsample.altvr.com.testsample.Constants;

public class SaveOrFavChangeEvent {
    public Constants eventType;

    public SaveOrFavChangeEvent(Constants eventType) {
        this.eventType = eventType;
    }
}
