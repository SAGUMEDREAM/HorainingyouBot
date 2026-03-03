package cc.thonly.horainingyoubot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController()
@RequestMapping("/api/temp_file")
public class TempFileController {
    private final Map<UUID, byte[]> files = new HashMap<>();

    /**
     * 获取音频文件
     * @param voiceId 音频的 UUID
     * @return 音频的字节数据
     */
    @GetMapping("/get_voice/{voiceId}")
    public ResponseEntity<byte[]> getVoice(@PathVariable UUID voiceId) {
        byte[] voiceData = files.get(voiceId);

        if (voiceData == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "audio/wav");

        return ResponseEntity.ok()
                .headers(headers)
                .body(voiceData);
    }



    public UUID saveFile(byte[] voiceData) {
        UUID voiceId = UUID.randomUUID();
        this.files.put(voiceId, voiceData);
        return voiceId;
    }

    public void clear() {
        this.files.clear();
    }
}
