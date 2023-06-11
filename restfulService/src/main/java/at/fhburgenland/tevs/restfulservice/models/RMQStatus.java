package at.fhburgenland.tevs.restfulservice.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RMQStatus {

    Status status;
    RequestType requestType;
    Map<String, Status> startupStatusMap;

    public RMQStatus(Status status, RequestType requestType) {
        this.status = status;
        this.requestType = requestType;
    }
}
