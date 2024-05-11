package com.mikolaj.e_library.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="news_post")
public class NewsPost implements Serializable {
	public NewsPost() {
	}
	
	@Column(name="post_id", nullable=false, unique=true, length=10)
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@org.hibernate.annotations.GenericGenerator(name="COM_MIKOLAJ_NEWSPOST_POSTID_GENERATOR", strategy="identity")
	private int postId;
	
	@Column(name="Name", nullable=false)
	private String name;
	
	@Column(name="contents", nullable=false)
	private String contents;

	@Column(name = "create_time")
	private LocalDateTime createTime;
	
	@Column(name="image_url")
	private String imageUrl;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "worker_id")
	private Worker worker;

	public NewsPost(String name) {
		this.name = name;
	}

	public String toString() {
		return String.valueOf(getPostId());
	}
	
}
