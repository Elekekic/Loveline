package backend.Loveline_backend.dto;

import backend.Loveline_backend.entity.Timeline;
import backend.Loveline_backend.entity.User;
import backend.Loveline_backend.enums.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EventDTO {

    @NotBlank(message = "The title cannot be empty")
    private String title;

    @NotBlank(message = "The description cannot be empty")
    private String description;

    @NotNull(message = "The date cannot be null")
    private LocalDate date;

    @NotNull(message = "The event type cannot be null")
    private EventType type;

    @NotBlank(message = "The timeline cannot be empty")
    private Timeline timeline;

    @NotBlank(message = "The user cannot be empty")
    private User user;

}
