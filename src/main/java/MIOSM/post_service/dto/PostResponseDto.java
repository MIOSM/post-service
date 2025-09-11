package MIOSM.post_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponseDto {
    private UUID postId;
    private UUID userId;
    private String username;
    private String userAvatar;
    private String content;
    private List<String> imageUrls;
    private List<String> videoUrls;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private LocalDateTime createdAt;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private LocalDateTime updatedAt;

    public String getCreatedAt() {
        if (createdAt == null) return null;
        return ZonedDateTime.of(createdAt, ZoneId.systemDefault())
                          .withZoneSameInstant(ZoneId.of("UTC"))
                          .format(java.time.format.DateTimeFormatter.ISO_INSTANT);
    }
    
    public String getUpdatedAt() {
        if (updatedAt == null) return null;
        return ZonedDateTime.of(updatedAt, ZoneId.systemDefault())
                          .withZoneSameInstant(ZoneId.of("UTC"))
                          .format(java.time.format.DateTimeFormatter.ISO_INSTANT);
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}