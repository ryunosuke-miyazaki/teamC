package com.example.postingapp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.postingapp.entity.Relation;
import com.example.postingapp.entity.Role;
import com.example.postingapp.entity.User;
import com.example.postingapp.form.SignupForm;
import com.example.postingapp.repository.RoleRepository;
import com.example.postingapp.repository.UserRepository;

@Service
public class UserService {
	
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	
	public UserService(UserRepository userRepository , RoleRepository roleRepository , PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	@Transactional
	public User createUser(SignupForm signupForm) {
		User user = new User();
		Role role = roleRepository.findByName("ROLE_GENERAL");
		
		user.setName(signupForm.getName());
		user.setEmail(signupForm.getEmail());
		user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
		user.setRole(role);
		user.setEnabled(false);
		
		return userRepository.save(user);
	}
	
	public boolean isEmailRegistered(String email) {
		User user = userRepository.findByEmail(email);
		return user != null;
	}
	
	public boolean isSamePassword(String password , String passwordConfirmation) {
		return password.equals(passwordConfirmation);
	}
	
	@Transactional
	public void enableUser(User user) {
		user.setEnabled(true);
		userRepository.save(user);
	}
	
	//新機能
	//全ユーザーを取得
	public List<User> findAll(){
		return userRepository.findAll();
	}
	
	//新機能
	//ユーザーIDからユーザーを取得
	public Optional<User> findUserById(Integer id) {
		return userRepository.findById(id);
	}
	
	//新機能
	//Relationのリストからフォローしているユーザーのリストを取得
	public List<User> findUsersByRelations(List<Relation> relations){
		List<User> follows = new ArrayList<User>();
		
		for(Relation relation : relations) {
			Integer userId = relation.getFollowerId();
			Optional<User> optionalUser = userRepository.findById(userId); 
			follows.add(optionalUser.get());
		}
		
		return follows;
	}

}
