package backend.Loveline_backend.repository;

import backend.Loveline_backend.entity.Timeline;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimelineRepository extends JpaRepository<Timeline, Integer> {
}
