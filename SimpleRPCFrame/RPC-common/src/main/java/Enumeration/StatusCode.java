package Enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusCode {
    SUCCESS(200),
    FAIL(300);

    private final int code;
}
