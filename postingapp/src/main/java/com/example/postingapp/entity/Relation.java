package com.example.postingapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="releation")
@IdClass(RelationId.class)
@Data
public class Relation {
	
	@Id
	@Column(name="user_id")
	private Integer userId;
	
	@Id
	@Column(name="follower_id")
	private Integer followerId;

}
