package backend.Loveline_backend.repository;

import backend.Loveline_backend.entity.Event;
import backend.Loveline_backend.entity.Timeline;
import backend.Loveline_backend.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer> {

     Optional<Event> findByTitle(String title);

     Optional<Event> findByTimeline(Timeline timeline);

     Optional<Event> findByDate(LocalDate date);

     Optional<Event> findByType(EventType type);

     Optional<Event> findByDescription(String description);

}
