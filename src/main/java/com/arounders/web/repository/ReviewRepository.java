package com.arounders.web.repository;

import com.arounders.web.entity.Review;

import java.util.List;

public interface ReviewRepository {
    List<Review> getReviewListOfBoard(Integer boardId);

    List<Review> getReviewListOfMember(Integer memberId);

    int insert(Review review);

    int update(Review review);

    int delete(Review review);
}
