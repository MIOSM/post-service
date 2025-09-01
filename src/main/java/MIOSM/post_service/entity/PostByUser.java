package MIOSM.post_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "posts_by_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostByUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID userId;
    
    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID postId;
    
    @Column(nullable = false, length = 32)
    private String username;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
}