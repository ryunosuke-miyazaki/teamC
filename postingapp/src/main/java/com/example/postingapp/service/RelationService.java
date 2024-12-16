package com.example.postingapp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.postingapp.entity.Relation;
import com.example.postingapp.entity.User;
import com.example.postingapp.repository.RelationRepository;

@Service
public class RelationService {
	private final RelationRepository relationRepository;
	
	public RelationService(RelationRepository relationRepository) {
		this.relationRepository = relationRepository;
	}
	
	//特定のユーザーのフォロワー一覧を検索する
	public List<Relation> findRelation(User user){
		int userId = user.getId();
		return relationRepository.findByUserId(userId);
	}
	
	//フォロー関係を作成する
	public void createRelation(Integer user,Integer follower) {
		Relation relation = new Relation();
		
		relation.setUserId(user);
		relation.setFollowerId(follower);
		
		relationRepository.save(relation);
	}
	
	//フォロー関係を削除する
	public void removeRelation(Integer user,Integer follower) {
		Relation relation = new Relation();
		
		relation.setUserId(user);
		relation.setFollowerId(follower);
		
		relationRepository.delete(relation);
	}
	
	//特定の２ユーザーのフォロー関係を取得する
	public Relation findRelationById(Integer userId, Integer followerId) {
		List<Relation> relations = relationRepository.findByUserId(userId);
		
		
		for( Relation relation : relations) {
			if(relation.getFollowerId() == followerId) {
				return relation;
			}
		}
		
		return null;
	}
	

}
