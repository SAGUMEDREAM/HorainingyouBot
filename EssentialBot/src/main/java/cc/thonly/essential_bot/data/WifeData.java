package cc.thonly.essential_bot.data;

import cc.thonly.horainingyoubot.converter.LongListConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "wife_data")
public class WifeData {
    @Id
    private Long userId;

    @Convert(converter = LongListConverter.class)
    @Column(name = "wife_list", columnDefinition = "TEXT")
    private List<Long> wifeList = new ArrayList<>();

    @Column(name = "today_count")
    private int todayCount;

    @Column(name = "last_draw_date")
    private LocalDate lastDrawDate;
}