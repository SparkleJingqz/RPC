package Server.Enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BalancerType {

    RANDOM(0),
    ROUND(1);

    private final int code;
}
