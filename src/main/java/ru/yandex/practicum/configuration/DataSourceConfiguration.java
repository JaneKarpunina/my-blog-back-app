package ru.yandex.practicum.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import ru.yandex.practicum.utils.Utils;

import java.io.IOException;

@Configuration
public class DataSourceConfiguration {

    private final static String UPLOADS = "uploads/";

    @EventListener
    public void onContextClosed(ContextClosedEvent event) throws IOException {
        Utils.deleteDirectory(UPLOADS);
    }

}