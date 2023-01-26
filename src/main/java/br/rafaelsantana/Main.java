package br.rafaelsantana;

import br.rafaelsantana.kafka.producers.IPStackProducer;
import br.rafaelsantana.model.IPStack;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Date;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);

        Constants constants = context.getBean(Constants.class);
        IPStackProducer producer = context.getBean(IPStackProducer.class);

        producer.sendIPStack(new IPStack(
                        "Main",
                        new Date().getTime(),
                        "151.101.65.69"
                ),
                constants.INPUT_TOPIC);

        producer.sendIPStack(new IPStack(
                        "Main",
                        new Date().getTime(),
                        "208.80.154.224"
                ),
                constants.INPUT_TOPIC);
    }
}