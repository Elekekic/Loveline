package backend.Loveline_backend.dto;

import backend.Loveline_backend.entity.Event;
import backend.Loveline_backend.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class TimelineDTO {

    @NotNull(message = "The user cannot be null")
    private User user;

    @NotNull(message = "The lover cannot be null")
    @JoinColumn(name = "lover_id")
    private User lover;

    @NotNull(message = "The events cannot be null")
    private List<Event> events;
}
