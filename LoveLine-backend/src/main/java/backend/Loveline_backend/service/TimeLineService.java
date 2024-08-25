package backend.Loveline_backend.service;

import backend.Loveline_backend.dto.TimelineDTO;
import backend.Loveline_backend.entity.Event;
import backend.Loveline_backend.entity.Timeline;
import backend.Loveline_backend.entity.User;
import backend.Loveline_backend.exception.TImelineNotFoundException;
import backend.Loveline_backend.exception.UserNotFoundException;
import backend.Loveline_backend.repository.TimelineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TimeLineService {

    @Autowired
    private TimelineRepository timelineRepository;

    @Autowired
    @Lazy
    private UserService userService;

    @Lazy
    @Autowired
    private EventService eventService;


    // QUERY - FIND ALL TIMELINES
    public List<Timeline> getAllTimelines() {
        return timelineRepository.findAll();
    }

    // QUERY - FIND TIMELINE BY ID
    public Timeline getTimelineById(int id) {
        Optional<Timeline> timelineOptional = timelineRepository.findById(id);
        if (timelineOptional.isPresent()) {
            return timelineOptional.get();
        } else {
            throw new TImelineNotFoundException("Timeline with id: " + id + " not found");
        }
    }

    // CREATE TIMELINE METHOD
    public Timeline createTimeline(TimelineDTO timelineDTO) {

        // CHECK IF USERS EXIST
        List<User> users = new ArrayList<>();

        for (User userSelected : timelineDTO.getUsers()) {
            Optional<User> user = userService.getUserById(userSelected.getId());
            if (user == null) {
                throw new UserNotFoundException("User with id: " + userSelected.getId() + " not found");
            }
            users.add(user.get());
        }

        Timeline timeline = new Timeline();
        timeline.setEvents(timelineDTO.getEvents());
        timeline.setUsers(users);
        timelineRepository.save(timeline);
        return timeline;
    }

    // DELETE TIMELINE METHOD
    public String deleteTimeline(int id) {
        Optional<Timeline> timelineOptional = timelineRepository.findById(id);
        if (timelineOptional.isPresent()) {

            // delete users and events first to prevent error
            List<User> users = timelineOptional.get().getUsers();
            for (User user : users) {
                userService.deleteUser(user.getId());
            }

            List<Event> events = timelineOptional.get().getEvents();
            for (Event event : events) {
                eventService.deleteEvent(event.getId());
            }

            timelineRepository.deleteById(id);
            return "Timeline with ID: " + id + " deleted";
        } else {
            throw new TImelineNotFoundException("Timeline with id: " + id + " not found");
        }

    }
}
