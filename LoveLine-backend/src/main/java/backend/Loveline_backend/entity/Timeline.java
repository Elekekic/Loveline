package backend.Loveline_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "timelines")
public class Timeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(mappedBy = "timeline", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "timeline", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Event> events = new ArrayList<>();
}
