package at.fhburgenland.tevs.restfulservice.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Status {
    private String username;
    private String statusText;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timeStamp;
}
