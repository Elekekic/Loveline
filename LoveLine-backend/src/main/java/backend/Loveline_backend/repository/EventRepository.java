package backend.Loveline_backend.repository;

import backend.Loveline_backend.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Integer> {
}
