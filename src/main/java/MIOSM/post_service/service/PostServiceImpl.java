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
    private final MinioService minioService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PostResponseDto createPost(PostCreateRequestDto requestDto) {
        try {
            UUID postId = UUID.randomUUID();

            Post post = new Post();
            post.setPostId(postId);
            post.setUserId(requestDto.getUserId());
            post.setUsername(requestDto.getUsername());
            post.setContent(requestDto.getContent());
            post.setImageUrls(requestDto.getImageUrls());
            post.setVideoUrls(requestDto.getVideoUrls());
            
            log.debug("Saving new post with ID: {}", postId);
            Post savedPost = postRepository.saveAndFlush(post);

            PostByUser postByUser = new PostByUser();
            postByUser.setUserId(requestDto.getUserId());
            postByUser.setPostId(postId);
            postByUser.setUsername(requestDto.getUsername());
            postByUser.setContent(requestDto.getContent());
            postByUser.setCreatedAt(savedPost.getCreatedAt());
            
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
    @Transactional
    public Optional<PostResponseDto> updatePost(UUID postId, PostUpdateRequestDto dto) {
        return postRepository.findById(postId).map(post -> {
            post.setContent(dto.getContent());
            Post updatedPost = postRepository.save(post);

            postByUserRepository.findByPostId(postId).ifPresent(postByUser -> {
                postByUser.setContent(dto.getContent());
                postByUserRepository.save(postByUser);
            });
            
            return postMapper.postToPostResponseDto(updatedPost);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePost(UUID postId) {
        return postRepository.findById(postId).map(post -> {
            try {
                if (post.getImageUrls() != null) {
                    for (String imageUrl : post.getImageUrls()) {
                        try {
                            minioService.deleteMedia(imageUrl);
                            log.info("Deleted image from MinIO: {}", imageUrl);
                        } catch (Exception e) {
                            log.error("Failed to delete image from MinIO: {}", imageUrl, e);
                        }
                    }
                }
                
                if (post.getVideoUrls() != null) {
                    for (String videoUrl : post.getVideoUrls()) {
                        try {
                            minioService.deleteMedia(videoUrl);
                            log.info("Deleted video from MinIO: {}", videoUrl);
                        } catch (Exception e) {
                            log.error("Failed to delete video from MinIO: {}", videoUrl, e);
                        }
                    }
                }

                postByUserRepository.deleteByPostId(postId);
                postRepository.delete(post);
                
                log.info("Successfully deleted post with ID: {}", postId);
                return true;
            } catch (Exception e) {
                log.error("Error deleting post {}: {}", postId, e.getMessage(), e);
                throw new RuntimeException("Failed to delete post: " + e.getMessage(), e);
            }
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
    
    @Override
    public List<PostResponseDto> getLatestPosts(int limit) {
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
        return posts.stream()
            .limit(limit)
            .map(postMapper::postToPostResponseDto)
            .toList();
    }
}