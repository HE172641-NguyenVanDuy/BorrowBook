package com.borrowbook.duyanh.controller;

import com.borrowbook.duyanh.dto.request.PostDTO;
import com.borrowbook.duyanh.dto.response.ApiResponse;
import com.borrowbook.duyanh.dto.response.PageResponse;
import com.borrowbook.duyanh.entity.Post;
import com.borrowbook.duyanh.entity.User;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(name = "/post")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/create-post")
    public ResponseEntity<ApiResponse<Post>> createPost(@RequestBody @Valid PostDTO postDTO) {
        Post post = postService.createPost(postDTO);
        ApiResponse<Post> apiResponse = ApiResponse.<Post>builder()
                .result(post)
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/update-post/{userId}")
    public ResponseEntity<ApiResponse<Post>> updatePost(@PathVariable("userId") int id,
                                                        @RequestBody @Valid PostDTO postDTO ) {
        Post post = postService.updatePost(postDTO,id);
        ApiResponse<Post> apiResponse = ApiResponse.<Post>builder()
                .result(post)
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/delete-post/{postId}")
    public ResponseEntity<ApiResponse<Post>> deletePost(@PathVariable("postId") long id) {
        Post post = postService.getPostById(id);
        if(!postService.deletePost(post)) {
            throw new RuntimeException(ErrorCode.ERROR.getMessage());
        }
        ApiResponse<Post> apiResponse = ApiResponse.<Post>builder()
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get-post")
    public ResponseEntity<ApiResponse<Post>> getPostById(@PathVariable("postId") long id) {
        Post post = postService.getPostById(id);
        ApiResponse<Post> apiResponse = ApiResponse.<Post>builder()
                .code(200)
                .result(post)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/all-post-bookId/{bookId}")
    public ResponseEntity<ApiResponse<PageResponse<Post>>> getAllPostByBookId(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "2") int size,
            @RequestParam(value = "sortBy", defaultValue = "desc") String sortBy,
            @PathVariable("bookId") int bookId) {
        PageResponse<Post> posts = postService.getAllPostByBookId(page, size, sortBy, bookId);
        ApiResponse<PageResponse<Post>> apiResponse = ApiResponse.<PageResponse<Post>>builder()
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .result(posts)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
