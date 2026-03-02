package cc.thonly.horainingyoubot.plugin.intelligence_agencies.data;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "collect_cache")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CollectCache {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cqcode;
    private LocalDateTime time;
}
