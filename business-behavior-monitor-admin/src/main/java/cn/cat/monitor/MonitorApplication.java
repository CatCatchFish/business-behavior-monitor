package cn.cat.monitor;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Configurable
public class MonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitorApplication.class);
    }

}