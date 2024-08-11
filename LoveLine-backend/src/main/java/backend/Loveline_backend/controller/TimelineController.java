package backend.Loveline_backend.controller;

import backend.Loveline_backend.dto.TimelineDTO;
import backend.Loveline_backend.entity.Timeline;
import backend.Loveline_backend.service.TimeLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TimelineController {

    @Autowired
    private TimeLineService timeLineService;

    // QUERY - FIND ALL TIMELINES
    @GetMapping("/timelines")
    public List<Timeline> getAllTimelines() {
        return timeLineService.getAllTimelines();
    }

    // QUERY - FIND TIMELINE BY ID
    @GetMapping("/timelines/{id}")
    public Timeline getTimelineById(@PathVariable int id) {
        return timeLineService.getTimelineById(id);
    }

    // CREATE TIMELINE METHOD
    @PostMapping("/timelines")
    public Timeline createTimeline(@RequestBody TimelineDTO timelineDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(bindingResult.getAllErrors().stream()
                    .map(objectError -> objectError.getDefaultMessage()).reduce("", (s, s2) -> s + s2));
        }
        return timeLineService.createTimeline(timelineDTO);
    }

    // DELETE TIMELINE METHOD
   @DeleteMapping("/timelines/{id}")
    public String deleteTimeline(@PathVariable int id, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(bindingResult.getAllErrors().stream()
                    .map(objectError -> objectError.getDefaultMessage()).reduce("", (s, s2) -> s + s2));
        }
        return timeLineService.deleteTimeline(id);
    }
}
