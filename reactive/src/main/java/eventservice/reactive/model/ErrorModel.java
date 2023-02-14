package eventservice.reactive.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ErrorModel {

    private LocalDate timestamp;
    private Integer status;
    private String errorMessage;
    private String message;
    private String path;

}
