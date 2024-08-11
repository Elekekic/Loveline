package backend.Loveline_backend.controller;

import backend.Loveline_backend.dto.EventDTO;
import backend.Loveline_backend.entity.Event;
import backend.Loveline_backend.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class EventController {

    @Autowired
    private EventService eventService;

    // QUERY - FIND ALL EVENTS
    @GetMapping("/events")
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    // QUERY - FIND EVENT BY ID
    @GetMapping("/events/{id}")
    public Optional<Event> getEventById(@PathVariable int id) {
        return eventService.getEventById(id);
    }

    // QUERY - FIND EVENT BY DATE
    @GetMapping("/events/{date}")
    public Event getEventById(@PathVariable LocalDate date) {
        return eventService.getEventByDate(date);
    }

    // QUERY - FIND EVENT BY TITLE
    @GetMapping("/events/{title}")
    public Event getEventById(@PathVariable String title) {
        return eventService.getEventByTitle(title);
    }

    // CREATE EVENT METHOD
    @PostMapping("/events")
    public Event createEvent(@RequestBody @Validated EventDTO eventDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(bindingResult.getAllErrors().stream()
                    .map(objectError -> objectError.getDefaultMessage()).reduce("", (s, s2) -> s + s2));
        }
        return eventService.createEvent(eventDTO);
    }

    // UPDATE EVENT METHOD
    @PutMapping("/events/{id}")
    public Event updateEvent(@PathVariable int id, @RequestBody @Validated EventDTO eventDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(bindingResult.getAllErrors().stream()
                    .map(objectError -> objectError.getDefaultMessage()).reduce("", (s, s2) -> s + s2));
        }
        return eventService.updateEvent(id, eventDTO);
    }

    // DELETE EVENT METHOD
    @DeleteMapping("/events/{id}")
    public void deleteEvent(@PathVariable int id) {
        eventService.deleteEvent(id);
    }

}
