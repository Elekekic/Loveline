package backend.Loveline_backend.service;

import backend.Loveline_backend.dto.EventDTO;
import backend.Loveline_backend.entity.Event;
import backend.Loveline_backend.entity.Timeline;
import backend.Loveline_backend.entity.User;
import backend.Loveline_backend.enums.EventType;
import backend.Loveline_backend.exception.EventAlreadyExistsException;
import backend.Loveline_backend.exception.EventNotFoundException;
import backend.Loveline_backend.exception.TImelineNotFoundException;
import backend.Loveline_backend.exception.UserNotFoundException;
import backend.Loveline_backend.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    @Lazy
    private TimeLineService timeLineService;

    @Autowired
    @Lazy
    private UserService userService;


    // QUERY - FIND ALL EVENTS
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    // QUERY - FIND EVENT BY ID
    public Optional<Event> getEventById(int id) {
        Optional<Event> eventOptional = eventRepository.findById(id);
        if (eventOptional.isPresent()) {
            return eventRepository.findById(id);
        } else {
            throw new EventNotFoundException("Event with id: " + id + " not found");
        }
    }

    // QUERY - FIND EVENT BY TITLE
    public Event getEventByTitle(String title) {
        Optional<Event> eventOptional = eventRepository.findByTitle(title);
        if (eventOptional.isPresent()) {
            return eventOptional.get();
        } else {
            throw new EventNotFoundException("Event with title: " + title + " not found");
        }
    }

    // QUERY - FIND EVENT BY DATE
    public Event getEventByDate(LocalDate date) {
        Optional<Event> eventOptional = eventRepository.findByDate(date);
        if (eventOptional.isPresent()) {
            return eventOptional.get();
        } else {
            throw new EventNotFoundException("Event with date: " + date + " not found");
        }
    }

    // QUERY - FIND EVENT BY TYPE
    public Event getEventByType(EventType type) {
        Optional<Event> eventOptional = eventRepository.findByType(type);
        if (eventOptional.isPresent()) {
            return eventOptional.get();
        } else {
            throw new EventNotFoundException("Event with type: " + type + " not found");
        }
    }

    // QUERY - FIND EVENT BY TIMELINE
    public Event getEventByTimeline(Timeline timeline) {
        Optional<Event> eventOptional = eventRepository.findByTimeline(timeline);
        if (eventOptional.isPresent()) {
            return eventOptional.get();
        } else {
            throw new EventNotFoundException("Event with timeline: " + timeline + " not found");
        }
    }


    // CREATE EVENT METHOD
    public Event createEvent(EventDTO eventDTO) {

        // CHECK IF TIMELINE EXISTS
        Timeline timeline = timeLineService.getTimelineById(eventDTO.getTimeline().getId());
        if (timeline == null) {
            throw new TImelineNotFoundException("Timeline with id: " + eventDTO.getTimeline().getId() + " not found");
        }

        // CHECK IF USER EXISTS
        Optional<User> userOptional = userService.getUserById(eventDTO.getUser().getId());
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User with id: " + eventDTO.getUser().getId() + " not found");
        } else userOptional.get();

        // CHECK IF EVENT TITLE ALREADY EXISTS
        Optional<Event> eventOptional = eventRepository.findByTitle(eventDTO.getTitle());
        if (eventOptional.isPresent()) {
            throw new EventAlreadyExistsException("Event with title: " + eventDTO.getTitle() + " already exists");
        }

        // CREATE EVENT
        Event event = new Event();
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setTimeline(timeline);
        event.setUser(userOptional.get());
        event.setType(eventDTO.getType());
        event.setDate(eventDTO.getDate());

        // SAVE EVENT
        return eventRepository.save(event);
    }


    // UPDATE EVENT METHOD
    public Event updateEvent(int id, EventDTO eventDTO) {

        // CHECK IF EVENT EXISTS
        Optional<Event> eventExists = eventRepository.findById(id);
        if (eventExists.isEmpty()) {
            throw new EventNotFoundException("Event with id: " + id + " not found");
        }

        // CHECK IF EVENT TITLE ALREADY EXISTS
        Optional<Event> eventOptional = eventRepository.findByTitle(eventDTO.getTitle());
        if (eventOptional.isPresent()) {
            throw new EventAlreadyExistsException("Event with title: " + eventDTO.getTitle() + " already exists");
        }

        // UPDATE THE USER IN THE EVENT
        Optional<User> userOptional = userService.getUserById(eventDTO.getUser().getId());
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User with id: " + eventDTO.getUser().getId() + " not found");
        }

        // UPDATE EVENT
        Event event = new Event();
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setTimeline(eventDTO.getTimeline());
        event.setUser(userOptional.get());
        event.setType(eventDTO.getType());
        event.setDate(eventDTO.getDate());

        // SAVE EVENT
        return eventRepository.save(event);
    }


    // DELETE EVENT METHOD
    public String deleteEvent(int id) {
        Optional<Event> eventOptional = eventRepository.findById(id);
        if (eventOptional.isPresent()) {

            eventOptional.get().setTimeline(null);
            eventOptional.get().setUser(null);

            eventRepository.deleteById(id);
            return "Event with ID: " + id + " deleted";
        } else throw new EventNotFoundException("Event with id: " + id + " not found");
    }
}
