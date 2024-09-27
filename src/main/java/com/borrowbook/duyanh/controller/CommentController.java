package com.borrowbook.duyanh.controller;

import com.borrowbook.duyanh.dto.request.CommentDTO;
import com.borrowbook.duyanh.dto.response.ApiResponse;
import com.borrowbook.duyanh.entity.Comment;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/create-post")
    public ResponseEntity<ApiResponse<Comment>> createComment(@RequestBody @Valid CommentDTO dto) {
        Comment comment = commentService.createComment(dto);
        ApiResponse<Comment> apiResponse = ApiResponse.<Comment>builder()
                .code(201)
                .result(comment)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get-comment-pid/{pid}")
    public ResponseEntity<ApiResponse<List<Comment>>> getAllCommentByPostId(@PathVariable("pid") long pid) {
        List<Comment> comments = commentService.getAllCommentByPostId(pid);
        ApiResponse<List<Comment>> apiResponse = ApiResponse.<List<Comment>>builder()
                .code(201)
                .result(comments)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/update-post/{pid}")
    public ResponseEntity<ApiResponse<Comment>> updateComment(@PathVariable("pid") long pid,
                                                              @RequestBody @Valid CommentDTO dto) {
        Comment comment = commentService.updateComment(dto,pid);
        ApiResponse<Comment> apiResponse = ApiResponse.<Comment>builder()
                .code(200)
                .result(comment)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("delete-post/{pid}")
    public ResponseEntity<ApiResponse<String>> deleteComment(@PathVariable("pid") long pid) {
        ApiResponse<String> apiResponse = commentService.deleteComment(pid);
        return ResponseEntity.ok(apiResponse);
    }
}
