package at.fhburgenland.tevs.restfulservice.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Status {
    private String username;
    private String statusText;
}
