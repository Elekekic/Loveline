package backend.Loveline_backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Error {

    private String messaggio;
    private LocalDateTime dataErrore;
}
