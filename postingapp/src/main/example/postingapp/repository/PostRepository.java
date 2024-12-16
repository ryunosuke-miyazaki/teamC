package com.example.postingapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.postingapp.entity.Post;
import com.example.postingapp.entity.User;

public interface PostRepository extends JpaRepository<Post, Integer>{
	public List<Post> findByUserOrderByCreatedAtDesc(User user);
	public Post findFirstByOrderByIdDesc();
	
	//課題追記分
	public List<Post> findByUserOrderByUpdatedAt(User user);
	
	//課題追記分
	public List<Post> findByOrderByUpdatedAtDesc();
	
	//追加機能分
	@Query("SELECT p FROM Post p WHERE p.user.id IN :followerIds ORDER BY p.updatedAt DESC")
    public List<Post> findFollowerPosts(@Param("followerIds") List<Integer> followerIds);

}
