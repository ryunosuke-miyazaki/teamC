package com.example.postingapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.postingapp.entity.Relation;

public interface RelationRepository extends JpaRepository<Relation, Integer>{
	public List<Relation> findByUserId(Integer userId);
	
}
