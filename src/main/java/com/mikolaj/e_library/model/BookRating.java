
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
@Table(name="book_rating")
public class BookRating implements Serializable {
	public BookRating() {
	}
	
	@Column(name="book_rating_id", nullable=false, unique=true, length=10)
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@org.hibernate.annotations.GenericGenerator(name="COM_MIKOLAJ_BOOKRATING_BOOKRATINGID_GENERATOR", strategy="identity")
	private int bookRatingId;
	
	@Column(name="rating", nullable=false, length=10)	
	private int rating;
	
//	@Column(name="book", nullable=false, length=10)
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "book_id")
	private Book book;
	
//	@Column(name="reader", nullable=false, length=10)
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "reader_id")
	private Reader Reader;


	public String toString() {
		return String.valueOf(getBookRatingId());
	}
	
}
