package com.example.postingapp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.postingapp.entity.Post;
import com.example.postingapp.entity.Relation;
import com.example.postingapp.entity.User;
import com.example.postingapp.form.PostEditForm;
import com.example.postingapp.form.PostRegisterForm;
import com.example.postingapp.security.UserDetailsImpl;
import com.example.postingapp.service.PostService;
import com.example.postingapp.service.RelationService;
import com.example.postingapp.service.UserService;

@Controller
@RequestMapping("/posts")
public class PostController {
	private final PostService postService;
	private final UserService userService;
	private final RelationService relationService;
	
	public PostController(PostService postService,
						  UserService userService,
						  RelationService relationService) 
	{
		this.postService = postService;
		this.userService = userService;
		this.relationService = relationService;
	}
	
	//投稿一覧ページへのアクセス
	@GetMapping
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		//User user =userDetailsImpl.getUser();
		
		//新機能分
		List<Post> posts = postService.findAll();
		//List<Post> posts = postService.findPostByUserOrderByCreatedAtDesc(user);
		
		//新機能分
		//投稿にユーザーネームを表示させるためにユーザー情報を取得する
		List<User> userList = userService.findAll();
		
		//新機能分
		//フォロー関係を取得する
		User user = userDetailsImpl.getUser();
		List<Relation> relations = relationService.findRelation(user);
		List<User> follows = userService.findUsersByRelations(relations);

		model.addAttribute("posts", posts);
		model.addAttribute("user",userList);
		model.addAttribute("follows", follows);
		
		return "posts/index";
	}
	
	//投稿詳細ページへアクセス
	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id, 
					   @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
					   RedirectAttributes redirectAttributes, Model model) {
		
		Optional<Post> optionalPost = postService.findPostById(id);
		
		if(optionalPost.isEmpty()) {
			redirectAttributes.addFlashAttribute("errorMessage","投稿が存在しません");
			
			return "redirect:/posts";
		}
		
		Post post = optionalPost.get();
		Integer postOwner = post.getUser().getId();
		Integer user = userDetailsImpl.getUser().getId();
		Relation relation = relationService.findRelationById(user, postOwner);
		
		model.addAttribute("post",post);
		model.addAttribute("relation",relation);
		
		return "posts/show";
	}
	
	//投稿作成ページへアクセス
	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("postRegisterForm", new PostRegisterForm());
		
		return "posts/register";
	}
	
	//投稿作成して投稿一覧へアクセス
	@PostMapping("/create")
	public String create(@ModelAttribute @Validated PostRegisterForm postRegisterForm,
						 BindingResult bindingResult,
						 @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
						 RedirectAttributes redirectAttributes,
						 Model model)
	{
		if(bindingResult.hasErrors()) {
			model.addAttribute("postRegisterForm", postRegisterForm);
			
			return "posts/register";
		}
		
		User user = userDetailsImpl.getUser();
		
		postService.createPost(postRegisterForm, user);
		redirectAttributes.addFlashAttribute("successMessage", "投稿が完了しました。");
		
		return "redirect:/posts";
	}
	
	//投稿編集ページへアクセス
	@GetMapping("/{id}/edit")
	public String edit(@PathVariable(name = "id") Integer id,
					   @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
					   RedirectAttributes redirectAttributes,
					   Model model) 
	{
		Optional<Post> optionalPost = postService.findPostById(id);
		User user = userDetailsImpl.getUser();
		
		if(optionalPost.isEmpty() || !optionalPost.get().getUser().equals(user)) {
			redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");
			
			return "redirect:/posts";
		}
		
		Post post = optionalPost.get();
		model.addAttribute("post", post);
		model.addAttribute("postEditForm", new PostEditForm(post.getTitle(), post.getContent()));
		
		return "posts/edit";
	}
	
	//投稿を編集して詳細ページへアクセス
	@PostMapping("/{id}/update")
	public String update(@ModelAttribute @Validated PostEditForm postEditForm,
						 BindingResult bindingResult,
						 @PathVariable(name="id") Integer id,
						 @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
						 RedirectAttributes redirectAttributes,
						 Model model) 
	{
		Optional<Post> optionalPost = postService.findPostById(id);
		User user = userDetailsImpl.getUser();
		
		if(optionalPost.isEmpty() || !optionalPost.get().getUser(). equals(user)) {
			redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");
			
			return "redirect:/posts";
		}
		
		Post post = optionalPost.get();
		
		if(bindingResult.hasErrors()) {
			model.addAttribute("post", post);
			model.addAttribute("postEditForm", postEditForm);
			
			return "posts/edit";
		}
		
		postService.updatePost(postEditForm, post);
		redirectAttributes.addFlashAttribute("successMessage", "投稿を編集しました。");
		
		
		return "redirect:/posts/" + id;
	}
	
	//投稿を削除して投稿一覧ページへアクセス
	@PostMapping("/{id}/delete")
	public String delete(@PathVariable(name = "id") Integer id,
						 @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
						 RedirectAttributes redirectAttributes,
						 Model model)
	{
		Optional<Post> optionalPost = postService.findPostById(id);
		User user = userDetailsImpl.getUser();
		
		if(optionalPost.isEmpty() || !optionalPost.get().getUser().equals(user)) {
			redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");
			
			return "redirect:/posts";
		}
		
		Post post = optionalPost.get();
		postService.deletePost(post);
		redirectAttributes.addFlashAttribute("successMessage", "投稿を削除しました。");
		
		return "redirect:/posts";
	}
	
	//マイページへのアクセス
	@GetMapping("/myposts")
	public String myPage(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		User user = userDetailsImpl.getUser();
		
		List<Post> posts = postService.findPostByUserOrderByCreatedAt(user);
		
		model.addAttribute("posts", posts);	
		
		return "/posts/mypage";
	}
	
	//特定のユーザーのユーザーページにアクセス
	@GetMapping("/userposts/{id}")
	public String userPage(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
						   @PathVariable(name = "id") Integer otherUserId,
						   Model model) 
	{	
		User user = userDetailsImpl.getUser();
		Integer userId = user.getId();
		
		if(userId == otherUserId) {
			List<Post> posts = postService.findPostByUserOrderByCreatedAt(user);
			model.addAttribute("posts", posts);	
			
			return "/posts/mypage";
		}
		
		User otherUser = userService.findUserById(otherUserId).get();
		Relation relation = relationService.findRelationById(userId, otherUserId);
		List<Post> posts = postService.findPostByUserOrderByUpdatedAt(otherUser);
		
		
		model.addAttribute("user", otherUser);
		model.addAttribute("posts", posts);
		model.addAttribute("relation", relation);
		
		return "posts/userpage";
	}
	
	//フォロワーの投稿のみ表示するページにアクセス
	@GetMapping("/followersposts")
	public String followersPost(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
								Model model) 
	{
		User user = userDetailsImpl.getUser();
		List<Relation> followers = relationService.findRelation(user);
		List<Post> posts = postService.findFollowerPosts(followers);
		
		model.addAttribute("posts", posts);
		
		return "/posts/followers";
	}

}
