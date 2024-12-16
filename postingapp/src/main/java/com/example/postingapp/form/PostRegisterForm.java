package com.example.postingapp.form;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostRegisterForm {
	
	//課題追記分
	@Length(max=40 ,message="タイトルの文字数は40字以内です。")
	@NotBlank(message = "タイトルを入力してください。")
	private String title;
	
	//課題追記分
	@Length(max=200, message="本文の文字数は200字以内です")
	@NotBlank(message = "本文を入力してください。")
	private String content;

}
