package ch.thp.proto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
public class Source {
    int i = 0;

    private String name;

    public Source(String name) {
        this.name = name;
    }

    Random random = new Random();

    public Datatype from(){
        if(i>10){
            return null;
        }
        String group = null;
        if(name.equals("B") && i<2){
            group = 3+"";
        } else {
            group = String.valueOf(random.nextInt(2));
        }

        Datatype datatype = new Datatype(group,
                String.valueOf(random.nextInt(2)),
                name);
        log.info("generated: {}", datatype);
        i++;
        return datatype;
    }
}
