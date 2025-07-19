package MIOSM.post_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import MIOSM.post_service.dto.PostCreateRequestDto;
import MIOSM.post_service.dto.PostResponseDto;
import MIOSM.post_service.dto.PostUpdateRequestDto;
import MIOSM.post_service.mapper.PostMapper;
import MIOSM.post_service.entity.Post;
import MIOSM.post_service.entity.PostByUser;
import MIOSM.post_service.entity.PostByUserKey;
import MIOSM.post_service.repository.PostRepository;
import MIOSM.post_service.repository.PostByUserRepository;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostByUserRepository postByUserRepository;
    private final PostMapper postMapper;

    @Override
    public PostResponseDto createPost(PostCreateRequestDto requestDto) {
        UUID postId = UUID.randomUUID();
        Instant createdAt = Instant.now();

        Post post = postMapper.postCreateRequestDtoToPost(requestDto);
        post.setPostId(postId);
        post.setCreatedAt(createdAt);
        postRepository.save(post);

        PostByUserKey key = new PostByUserKey();
        key.setUserId(requestDto.getUserId());
        key.setCreatedAt(createdAt);
        key.setPostId(postId);

        PostByUser postByUser = new PostByUser();
        postByUser.setKey(key);
        postByUser.setUsername(requestDto.getUsername());
        postByUser.setContent(requestDto.getContent());
        postByUserRepository.save(postByUser);

        return postMapper.postToPostResponseDto(post);
    }

    @Override
    public Optional<PostResponseDto> getPostById(UUID postId) {
        return postRepository.findById(postId)
                .map(postMapper::postToPostResponseDto);
    }

    @Override
    public Optional<PostResponseDto> updatePost(UUID postId, PostUpdateRequestDto dto) {
        return postRepository.findById(postId).map(post -> {
            post.setContent(dto.getContent());
            postRepository.save(post);
            // Update posts_by_user
            PostByUserKey key = new PostByUserKey();
            key.setUserId(post.getUserId());
            key.setCreatedAt(post.getCreatedAt());
            key.setPostId(postId);
            PostByUser postByUser = new PostByUser();
            postByUser.setKey(key);
            postByUser.setUsername(post.getUsername());
            postByUser.setContent(dto.getContent());
            postByUserRepository.save(postByUser);
            return postMapper.postToPostResponseDto(post);
        });
    }

    @Override
    public boolean deletePost(UUID postId) {
        return postRepository.findById(postId).map(post -> {
            postRepository.deleteById(postId);
            // Delete from posts_by_user
            PostByUserKey key = new PostByUserKey();
            key.setUserId(post.getUserId());
            key.setCreatedAt(post.getCreatedAt());
            key.setPostId(postId);
            postByUserRepository.deleteById(key);
            return true;
        }).orElse(false);
    }

    @Override
    public List<PostResponseDto> getPostsByUser(UUID userId) {
        List<PostByUser> postsByUser = postByUserRepository.findByKeyUserId(userId);
        return postsByUser.stream().map(pbu -> {
            // Fetch full Post entity for complete info
            return postRepository.findById(pbu.getKey().getPostId())
                .map(postMapper::postToPostResponseDto)
                .orElse(null);
        }).filter(java.util.Objects::nonNull).toList();
    }
} 