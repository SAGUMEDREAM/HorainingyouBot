package cc.thonly.horainingyoubot.data.db;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "record_log")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RecordLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
    private LocalDateTime time;
    private String info;
}
