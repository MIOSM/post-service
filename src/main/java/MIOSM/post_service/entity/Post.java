package MIOSM.post_service.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;
import java.time.Instant;

@Getter
@Setter
@Table("posts")
public class Post {
    @PrimaryKey
    private UUID postId;

    private UUID userId;
    private String username;
    private String content;
    private Instant createdAt;
} 