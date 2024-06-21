package ru.otus.hw.indicator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class EntityNotFoundHealthIndicator implements HealthIndicator {

    private boolean hasError = false;

    @Override
    public Health health() {
        if (hasError) {
            return Health.down()
                    .withDetail("Error", "Entity not found Exception while update book").build();
        } else {
            return Health.up().build();
        }
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }
}
