package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.CommentDTO;
import com.borrowbook.duyanh.entity.Comment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {

    Comment createComment(CommentDTO dto);

    Comment updateComment(CommentDTO dto, long id);

    List<Comment> getAllCommentByPostId(long pid);

    boolean deleteComment(long cid);

    Comment getCommentById(long id);
}
