
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
	private int bookRatingId;
	
	@Column(name="rating", nullable=false, length=10)	
	private int rating;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "book_id")
	private Book book;
	
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "reader_id")
	private Reader reader;

	public BookRating(int rating, Book book, Reader reader) {
		this.rating = rating;
		this.book = book;
		this.reader = reader;
	}

	public String toString() {
		return String.valueOf(getBookRatingId());
	}
}
