package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.CommentDTO;
import com.borrowbook.duyanh.entity.Comment;
import com.borrowbook.duyanh.entity.Post;
import com.borrowbook.duyanh.entity.User;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.repository.CommentRepository;
import com.borrowbook.duyanh.repository.PostRepository;
import com.borrowbook.duyanh.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository,
                              UserRepository userRepository,
                              PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @Override
    public Comment createComment(CommentDTO dto) {
        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        User user = userRepository.findById(dto.getUid()).orElseThrow(
                () -> new RuntimeException(ErrorCode.NOT_FOUND.getMessage()));
        Post post = postRepository.findById(dto.getPostId()).orElseThrow(
                () -> new RuntimeException(ErrorCode.NOT_FOUND.getMessage()));
        comment.setUser(user);
        comment.setPost(post);
        comment.setDateTime(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    @Override
    public Comment updateComment(CommentDTO dto, long id) {
        Comment comment = getCommentById(id);
        comment.setContent(dto.getContent());
        User user = userRepository.findById(dto.getUid()).orElseThrow(
                () -> new RuntimeException(ErrorCode.NOT_FOUND.getMessage()));
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

    @Override
    public boolean deleteComment(long cid) {
        Comment comment = getCommentById(cid);
        try {
            commentRepository.delete(comment);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Comment getCommentById(long id) {
        return commentRepository.findById(id).orElseThrow(
                () -> new RuntimeException(ErrorCode.NOT_FOUND.getMessage()));
    }
}
