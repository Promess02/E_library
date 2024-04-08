
package com.mikolaj.e_library.model;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Setter
@AllArgsConstructor
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="BookRating", indexes={ @Index(name="FKBookRating94942", columnList="bookId"), @Index(name="FKBookRating629623", columnList="readerId") })
public class BookRating implements Serializable {
	public BookRating() {
	}
	
	@Column(name="bookRatingId", nullable=false, unique=true, length=10)	
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@org.hibernate.annotations.GenericGenerator(name="COM_MIKOLAJ_BOOKRATING_BOOKRATINGID_GENERATOR", strategy="identity")
	private int bookRatingId;
	
	@Column(name="rating", nullable=false, length=10)	
	private int rating;
	
	@Column(name="bookId", nullable=false, length=10)	
	private int bookId;
	
	@Column(name="readerId", nullable=false, length=10)	
	private int readerId;


	public String toString() {
		return String.valueOf(getBookRatingId());
	}
	
}
