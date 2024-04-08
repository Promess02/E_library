package com.mikolaj.e_library.model;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="NewsPost", indexes={ @Index(name="FKNewsPost131685", columnList="workerId") })
public class NewsPost implements Serializable {
	public NewsPost() {
	}
	
	@Column(name="postId", nullable=false, unique=true, length=10)	
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@org.hibernate.annotations.GenericGenerator(name="COM_MIKOLAJ_NEWSPOST_POSTID_GENERATOR", strategy="identity")
	private int postId;
	
	@Column(name="Name", nullable=false)
	private String name;
	
	@Column(name="contents", nullable=false)
	private String contents;
	
	@Column(name="imageUrl")
	private String imageUrl;
	
	@Column(name="workerId", nullable=false, length=10)	
	private int workerId;

	
	public String toString() {
		return String.valueOf(getPostId());
	}
	
}
