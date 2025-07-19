package MIOSM.post_service.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Setter
@Table("posts_by_user")
public class PostByUser {
    @PrimaryKey
    private PostByUserKey key;
    private String username;
    private String content;
} 