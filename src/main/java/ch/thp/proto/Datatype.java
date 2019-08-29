package ch.thp.proto;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(of = {"group", "value"})
public class Datatype {
    private String group;
    private String value;
    private String source;
}
