package ch.thp.proto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
public class Source {

    private String name;

    public Source(String name) {
        this.name = name;
    }

    Random random = new Random();

    public Datatype from(){

        Datatype datatype = new Datatype(String.valueOf(random.nextInt(2)),
                String.valueOf(random.nextInt(2)),
                name);
        log.info("generated: {}", datatype);
        return datatype;
    }
}
