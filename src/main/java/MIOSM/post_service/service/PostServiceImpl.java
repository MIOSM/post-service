package MIOSM.post_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import MIOSM.post_service.dto.PostCreateRequestDto;
import MIOSM.post_service.dto.PostResponseDto;
import MIOSM.post_service.dto.PostUpdateRequestDto;
import MIOSM.post_service.mapper.PostMapper;
import MIOSM.post_service.entity.Post;
import MIOSM.post_service.entity.PostByUser;
import MIOSM.post_service.repository.PostRepository;
import MIOSM.post_service.repository.PostByUserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostByUserRepository postByUserRepository;
    private final PostMapper postMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PostResponseDto createPost(PostCreateRequestDto requestDto) {
        try {
            UUID postId = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();

            Post post = new Post();
            post.setPostId(postId);
            post.setUserId(requestDto.getUserId());
            post.setUsername(requestDto.getUsername());
            post.setContent(requestDto.getContent());
            post.setImageUrls(requestDto.getImageUrls());
            post.setVideoUrls(requestDto.getVideoUrls());
            post.setCreatedAt(now);
            post.setUpdatedAt(now);
            
            log.debug("Saving new post with ID: {}", postId);
            Post savedPost = postRepository.saveAndFlush(post);

            PostByUser postByUser = new PostByUser();
            postByUser.setUserId(requestDto.getUserId());
            postByUser.setPostId(postId);
            postByUser.setUsername(requestDto.getUsername());
            postByUser.setContent(requestDto.getContent());
            postByUser.setCreatedAt(now);
            
            log.debug("Saving post by user entry for post ID: {}", postId);
            postByUserRepository.saveAndFlush(postByUser);
            
            log.info("Successfully created post with ID: {}", postId);
            return postMapper.postToPostResponseDto(savedPost);
            
        } catch (Exception e) {
            log.error("Error creating post: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create post: " + e.getMessage(), e);
        }
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
            List<PostByUser> postsByUser = postByUserRepository.findByUserIdOrderByCreatedAtDesc(post.getUserId());
            postsByUser.stream()
                .filter(pbu -> pbu.getPostId().equals(postId))
                .findFirst()
                .ifPresent(pbu -> {
                    pbu.setContent(dto.getContent());
                    postByUserRepository.save(pbu);
                });
            return postMapper.postToPostResponseDto(post);
        });
    }

    @Override
    public boolean deletePost(UUID postId) {
        return postRepository.findById(postId).map(post -> {
            postRepository.deleteById(postId);
            postByUserRepository.deleteByPostId(postId);
            return true;
        }).orElse(false);
    }

    @Override
    public List<PostResponseDto> getPostsByUser(UUID userId) {
        List<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return posts.stream()
            .map(postMapper::postToPostResponseDto)
            .toList();
    }
    
    @Override
    public List<PostResponseDto> getPostsByUsername(String username) {
        List<Post> posts = postRepository.findByUsernameOrderByCreatedAtDesc(username);
        return posts.stream()
            .map(postMapper::postToPostResponseDto)
            .toList();
    }
}