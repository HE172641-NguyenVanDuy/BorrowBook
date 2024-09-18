package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.PostDTO;
import com.borrowbook.duyanh.dto.response.PageResponse;
import com.borrowbook.duyanh.entity.Post;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PostService {

    Post createPost(PostDTO postDTO );

    Post updatePost(PostDTO postDTO, long id);

    boolean deletePost(Post post);

    PageResponse<Post> getAllPostByBookId(int size, int page, String sortOrder, int bookId);

    Post getPostById(long id);
}
