package ru.yandex.practicum.configuration;

import org.h2.Driver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import ru.yandex.practicum.utils.Utils;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;

@Configuration
public class DataSourceConfiguration {

    private final static String UPLOADS = "uploads/";
    // После инициализации контекста выполняем наполнение схемы базы данных
    @EventListener
    public void populate(ContextRefreshedEvent event) {
        DataSource dataSource = event.getApplicationContext().getBean(DataSource.class);

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("schema.sql")); // Файл должен находиться в ресурсах
        populator.execute(dataSource);
    }

    @EventListener
    public void onContextClosed(ContextClosedEvent event) throws IOException {
        Utils.deleteDirectory(UPLOADS);
    }

}