package MIOSM.post_service.repository;

import MIOSM.post_service.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    
    Optional<Like> findByPostIdAndUserId(UUID postId, UUID userId);
    
    boolean existsByPostIdAndUserId(UUID postId, UUID userId);
    
    void deleteByPostIdAndUserId(UUID postId, UUID userId);
    
    long countByPostId(UUID postId);
    
    List<Like> findByUserIdOrderByCreatedAtDesc(UUID userId);
    
    @Query("SELECT l.postId FROM Like l WHERE l.userId = :userId ORDER BY l.createdAt DESC")
    List<UUID> findPostIdsByUserIdOrderByCreatedAtDesc(@Param("userId") UUID userId);
    
    @Query("SELECT l.postId FROM Like l WHERE l.username = :username ORDER BY l.createdAt DESC")
    List<UUID> findPostIdsByUsernameOrderByCreatedAtDesc(@Param("username") String username);
    
    @Query("SELECT COUNT(l) FROM Like l WHERE l.postId IN :postIds")
    long countByPostIdIn(@Param("postIds") List<UUID> postIds);
}
