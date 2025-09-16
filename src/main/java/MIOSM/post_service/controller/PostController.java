package MIOSM.post_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import MIOSM.post_service.dto.PostCreateRequestDto;
import MIOSM.post_service.dto.PostResponseDto;
import MIOSM.post_service.dto.PostUpdateRequestDto;
import MIOSM.post_service.service.PostService;
import MIOSM.post_service.service.MinioService;

import java.util.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;
    private final MinioService minioService;

    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(
            @RequestParam("userId") String userId,
            @RequestParam("username") String username,
            @RequestParam(value = "userAvatar", required = false) String userAvatar,
            @RequestParam("content") String content,
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            @RequestParam(value = "videos", required = false) MultipartFile[] videos) {
        
        try {
            UUID userUuid;
            try {
                userUuid = UUID.fromString(userId);
            } catch (IllegalArgumentException e) {
                userUuid = UUID.nameUUIDFromBytes(userId.getBytes());
            }
            
            PostCreateRequestDto requestDto = new PostCreateRequestDto();
            requestDto.setUserId(userUuid);
            requestDto.setUsername(username);
            requestDto.setUserAvatar(userAvatar);
            requestDto.setContent(content);
            
            List<String> imageUrls = new ArrayList<>();
            List<String> videoUrls = new ArrayList<>();

            if (images != null) {
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        String imageUrl = minioService.uploadMedia(image, "images");
                        imageUrls.add(imageUrl);
                    }
                }
            }

            if (videos != null) {
                for (MultipartFile video : videos) {
                    if (!video.isEmpty()) {
                        String videoUrl = minioService.uploadMedia(video, "videos");
                        videoUrls.add(videoUrl);
                    }
                }
            }
            
            requestDto.setImageUrls(imageUrls);
            requestDto.setVideoUrls(videoUrls);
            
            PostResponseDto responseDto = postService.createPost(requestDto);
            return ResponseEntity.ok(responseDto);
            
        } catch (Exception e) {
            log.error("Error creating post: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable UUID id) {
        Optional<PostResponseDto> post = postService.getPostById(id);
        return post.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<PostResponseDto> getPostsByUser(@PathVariable UUID userId) {
        return postService.getPostsByUser(userId);
    }
    
    @GetMapping("/user/username/{username}")
    public List<PostResponseDto> getPostsByUsername(
            @PathVariable String username,
            @RequestParam(value = "currentUserId", required = false) String currentUserId) {
        
        if (currentUserId != null && !currentUserId.isEmpty()) {
            try {
                UUID userUuid = UUID.fromString(currentUserId);
                return postService.getPostsByUsername(username, userUuid);
            } catch (IllegalArgumentException e) {
                UUID userUuid = UUID.nameUUIDFromBytes(currentUserId.getBytes());
                return postService.getPostsByUsername(username, userUuid);
            }
        }
        
        return postService.getPostsByUsername(username);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable UUID id, @RequestBody PostUpdateRequestDto dto) {
        return postService.updatePost(id, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable UUID id) {
        boolean deleted = postService.deletePost(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/latest")
    public List<PostResponseDto> getLatestPosts(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(value = "currentUserId", required = false) String currentUserId) {
        
        if (currentUserId != null && !currentUserId.isEmpty()) {
            try {
                UUID userUuid = UUID.fromString(currentUserId);
                return postService.getLatestPosts(limit, userUuid);
            } catch (IllegalArgumentException e) {
                UUID userUuid = UUID.nameUUIDFromBytes(currentUserId.getBytes());
                return postService.getLatestPosts(limit, userUuid);
            }
        }
        
        return postService.getLatestPosts(limit);
    }
    
    @PostMapping("/{postId}/like")
    public ResponseEntity<Map<String, Object>> likePost(
            @PathVariable UUID postId,
            @RequestParam("userId") String userId,
            @RequestParam("username") String username) {
        try {
            UUID userUuid;
            try {
                userUuid = UUID.fromString(userId);
            } catch (IllegalArgumentException e) {
                userUuid = UUID.nameUUIDFromBytes(userId.getBytes());
            }
            
            boolean liked = postService.likePost(postId, userUuid, username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", liked);
            response.put("message", liked ? "Post liked successfully" : "Post already liked");
            
            if (liked) {
                postService.getPostById(postId).ifPresent(post -> 
                    response.put("likeCount", post.getLikeCount())
                );
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error liking post: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to like post");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Map<String, Object>> unlikePost(
            @PathVariable UUID postId,
            @RequestParam("userId") String userId) {
        try {
            UUID userUuid;
            try {
                userUuid = UUID.fromString(userId);
            } catch (IllegalArgumentException e) {
                userUuid = UUID.nameUUIDFromBytes(userId.getBytes());
            }
            
            boolean unliked = postService.unlikePost(postId, userUuid);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", unliked);
            response.put("message", unliked ? "Post unliked successfully" : "Post not liked");
            
            if (unliked) {
                postService.getPostById(postId).ifPresent(post -> 
                    response.put("likeCount", post.getLikeCount())
                );
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error unliking post: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to unlike post");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @GetMapping("/{postId}/like-status")
    public ResponseEntity<Map<String, Object>> getLikeStatus(
            @PathVariable UUID postId,
            @RequestParam("userId") String userId) {
        try {
            UUID userUuid;
            try {
                userUuid = UUID.fromString(userId);
            } catch (IllegalArgumentException e) {
                userUuid = UUID.nameUUIDFromBytes(userId.getBytes());
            }
            
            boolean isLiked = postService.isPostLikedByUser(postId, userUuid);
            
            Map<String, Object> response = new HashMap<>();
            response.put("isLiked", isLiked);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error checking like status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/liked/user/{userId}")
    public List<PostResponseDto> getLikedPostsByUser(@PathVariable UUID userId) {
        return postService.getLikedPostsByUser(userId);
    }
    
    @GetMapping("/liked/user/username/{username}")
    public List<PostResponseDto> getLikedPostsByUsername(
            @PathVariable String username,
            @RequestParam(required = false) String currentUserId) {
        UUID currentUserUUID = null;
        if (currentUserId != null && !currentUserId.trim().isEmpty()) {
            try {
                currentUserUUID = UUID.fromString(currentUserId);
            } catch (IllegalArgumentException e) {
                currentUserUUID = UUID.nameUUIDFromBytes(currentUserId.getBytes());
            }
        }
        return postService.getLikedPostsByUsername(username, currentUserUUID);
    }
    
    @GetMapping("/user/{userId}/total-likes")
    public ResponseEntity<Map<String, Object>> getTotalLikesForUserPosts(@PathVariable UUID userId) {
        try {
            long totalLikes = postService.getTotalLikesForUserPosts(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("totalLikes", totalLikes);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting total likes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/user/username/{username}/total-likes")
    public ResponseEntity<Map<String, Object>> getTotalLikesForUserPostsByUsername(@PathVariable String username) {
        try {
            long totalLikes = postService.getTotalLikesForUserPostsByUsername(username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("totalLikes", totalLikes);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting total likes by username: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}