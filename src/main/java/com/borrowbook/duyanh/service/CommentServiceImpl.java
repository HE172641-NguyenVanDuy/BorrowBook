package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.CommentDTO;
import com.borrowbook.duyanh.dto.response.ApiResponse;
import com.borrowbook.duyanh.entity.Comment;
import com.borrowbook.duyanh.entity.Post;
import com.borrowbook.duyanh.entity.User;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.repository.CommentRepository;
import com.borrowbook.duyanh.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    private UserService userService;

    private final PostRepository postRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository,
                              PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Override
    public Comment createComment(CommentDTO dto) {
        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        User user = userService.getMyInfo();
        Post post = postRepository.findById(dto.getPostId()).orElseThrow(
                () -> new RuntimeException(ErrorCode.NOT_FOUND.getMessage()));
        comment.setUser(user);
        comment.setPost(post);
        comment.setDateTime(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Override
    @Modifying
    public Comment updateComment(CommentDTO dto, long id) {
        Comment comment = getCommentById(id);
        comment.setContent(dto.getContent());
        User user = userService.getMyInfo();
        Post post = postRepository.findById(dto.getPostId()).orElseThrow(
                () -> new RuntimeException(ErrorCode.NOT_FOUND.getMessage()));
        comment.setUser(user);
        comment.setPost(post);
        return commentRepository.saveAndFlush(comment);
    }


    @Override
    public List<Comment> getAllCommentByPostId(long pid) {
        return commentRepository.getAllCommentByPostId(pid);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    @Override
    public ApiResponse<String> deleteComment(long cid) {
        Comment comment = getCommentById(cid);
        String msg = "";
        try {
            commentRepository.delete(comment);
            msg = ErrorCode.DELETE_COMMENT_SUCCESS.getMessage();
        } catch (Exception e) {
            msg = ErrorCode.DELETE_COMMENT_FAIL.getMessage();
        }
        return ApiResponse.<String>builder()
                .code(200)
                .result(msg)
                .build();
    }

    @Override
    public Comment getCommentById(long id) {
        return commentRepository.findById(id).orElseThrow(
                () -> new RuntimeException(ErrorCode.NOT_FOUND.getMessage()));
    }
}
