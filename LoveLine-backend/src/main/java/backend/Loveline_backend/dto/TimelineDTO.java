package backend.Loveline_backend.dto;

import backend.Loveline_backend.entity.Event;
import backend.Loveline_backend.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class TimelineDTO {

    @NotNull(message = "The users cannot be null")
    private List<User> users;

    @NotNull(message = "The events cannot be null")
    private List<Event> events;
}
