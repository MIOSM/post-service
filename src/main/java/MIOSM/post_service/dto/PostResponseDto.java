package MIOSM.post_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PostResponseDto {
    private UUID postId;
    private UUID userId;
    private String username;
    private String content;
    private List<String> imageUrls;
    private List<String> videoUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}