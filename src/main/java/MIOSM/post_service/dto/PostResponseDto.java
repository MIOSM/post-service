package MIOSM.post_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
import java.time.Instant;

@Getter
@Setter
public class PostResponseDto {
    private UUID postId;
    private UUID userId;
    private String username;
    private String content;
    private Instant createdAt;
} 