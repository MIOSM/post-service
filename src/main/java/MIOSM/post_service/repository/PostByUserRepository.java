package MIOSM.post_service.repository;

import MIOSM.post_service.entity.PostByUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostByUserRepository extends JpaRepository<PostByUser, Long> {
    List<PostByUser> findByUserIdOrderByCreatedAtDesc(UUID userId);
    void deleteByPostId(UUID postId);
}