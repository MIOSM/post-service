package MIOSM.post_service.repository;

import MIOSM.post_service.entity.PostByUser;
import MIOSM.post_service.entity.PostByUserKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import java.util.List;
import java.util.UUID;

public interface PostByUserRepository extends CassandraRepository<PostByUser, PostByUserKey> {
    List<PostByUser> findByKeyUserId(UUID userId);
} 