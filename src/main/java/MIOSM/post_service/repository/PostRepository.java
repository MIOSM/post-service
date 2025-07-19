package MIOSM.post_service.repository;

import MIOSM.post_service.entity.Post;
import org.springframework.data.cassandra.repository.CassandraRepository;
import java.util.UUID;

public interface PostRepository extends CassandraRepository<Post, UUID> {
} 