package com.example.postingapp.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.postingapp.entity.Post;
import com.example.postingapp.entity.Relation;
import com.example.postingapp.entity.User;
import com.example.postingapp.security.UserDetailsImpl;
import com.example.postingapp.service.PostService;
import com.example.postingapp.service.RelationService;
import com.example.postingapp.service.UserService;

@Controller
@RequestMapping("/follow")
public class RelationController {
	private final RelationService relationService;
	private final UserService userService;
	private final PostService postService;
	
	public RelationController(RelationService relationService,
							  UserService userService,
							  PostService postService) 
	{
		this.relationService = relationService;
		this.userService = userService;
		this.postService = postService;
	}
	
	//フォロー操作
	@PostMapping("/{id}")
	public String follow(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
						 @PathVariable(name = "id") Integer followerId,
						 Model model) 
	{
		Integer userId = userDetailsImpl.getUser().getId();
		relationService.createRelation(userId,followerId);
		
		User follower = userService.findUserById(followerId).get();
		List<Post> posts = postService.findPostByUserOrderByUpdatedAt(follower);
		
		Relation relation =relationService.findRelationById(userId,followerId);
		
		model.addAttribute("follower",follower );
		model.addAttribute("user", follower);
		model.addAttribute("posts", posts);
		model.addAttribute(relation);
		
		return "/posts/userpage";
	}
	
	//フォロー解除
	@PostMapping("/{id}/remove")
	public String remove(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
						 @PathVariable(name = "id") Integer followerId,
						 Model model) 
	{
		Integer userId = userDetailsImpl.getUser().getId();
		relationService.removeRelation(userId,followerId);
		
		User follower = userService.findUserById(followerId).get();
		List<Post> posts = postService.findPostByUserOrderByUpdatedAt(follower);
		
		model.addAttribute("remove",follower );
		model.addAttribute("user", follower);
		model.addAttribute("posts", posts);
		
		return "/posts/userpage";
	}

}
