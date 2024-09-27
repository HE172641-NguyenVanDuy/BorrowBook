package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.PostDTO;
import com.borrowbook.duyanh.dto.response.ApiResponse;
import com.borrowbook.duyanh.dto.response.PageResponse;
import com.borrowbook.duyanh.entity.Post;
import com.borrowbook.duyanh.entity.User;
import com.borrowbook.duyanh.exception.AppException;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;

    @Autowired
    private UserService userService;

    @Autowired
    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }


    @Override
    @Transactional
    public Post createPost(PostDTO postDTO) {
        Post post = new Post();
        post.setCreateDate(LocalDateTime.now());
        User user = userService.getMyInfo();
        post.setUser(user);
        post.setContent(postDTO.getContent());
        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Post updatePost(PostDTO postDTO, long id) {
        Post post = getPostById(id);
        User user = userService.getMyInfo();
        post.setUser(user);
        post.setContent(postDTO.getContent());
        return postRepository.saveAndFlush(post);
    }

    @Override
    @Transactional
    public ApiResponse<String> deletePost(Post post) {
        String msg = "";
        try {
            postRepository.delete(post);
            msg = ErrorCode.DELETE_POST_FAIL.getMessage();
        } catch (Exception e) {
            msg = ErrorCode.DELETE_POST_FAIL.getMessage();
        }
        return ApiResponse.<String>builder()
                .code(200)
                .result(msg)
                .build();
    }

    @Override
    public PageResponse<Post> getAllPostByBookId(int size, int page, String sortOrder, int bookId) {
        Sort sort = Sort.by("create_date");
        if ("DESC".equalsIgnoreCase(sortOrder)) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }

        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Post> pageData = postRepository.getAllPostByBookId(pageable,bookId);
        return PageResponse.<Post>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElement(pageData.getTotalElements())
                .data(pageData.getContent())
                .build();
    }

    @Override
    public Post getPostById(long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.NOT_FOUND));
        return post;
    }
}
