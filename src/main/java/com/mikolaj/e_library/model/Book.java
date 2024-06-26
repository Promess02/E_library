
package com.mikolaj.e_library.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mikolaj.e_library.DTO.BookCategory;
import com.mikolaj.e_library.DTO.BookCategorySerializer;
import com.mikolaj.e_library.DTO.BookType;
import com.mikolaj.e_library.DTO.BookTypeSerializer;
import com.mikolaj.e_library.Persistence.BookCategoryConverter;
import com.mikolaj.e_library.Persistence.BookTypeConverter;
import com.mikolaj.e_library.DTO.CustomDateDeserializer;
import com.mikolaj.e_library.DTO.CustomDateSerializer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name = "Book")
public class Book implements Serializable {
	public Book() {
	}
	
	@Column(name="book_id", nullable=false, unique=true, length=10)
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@org.hibernate.annotations.GenericGenerator(name="COM_MIKOLAJ_BOOK_BOOKID_GENERATOR", strategy="identity")
	private int bookId;
	
	@Column(name="book_type")
	@Convert(converter = BookTypeConverter.class)
	@JsonSerialize(using = BookTypeSerializer.class)
	private BookType bookType;
	
	@Column(name="title", nullable=false)
	private String title;
	
	@Column(name="release_date")
//	@JsonFormat(pattern = "yyyy-MM-dd")
	@JsonSerialize(using = CustomDateSerializer.class)
	@JsonDeserialize(using = CustomDateDeserializer.class)
	private LocalDate releaseDate;
	
	@Column(name="book_category")
	@Convert(converter = BookCategoryConverter.class)
	@JsonSerialize(using = BookCategorySerializer.class)
	private BookCategory bookCategory;
	
	@Column(name="Average_book_rating", length=10)
	private Float averageBookRating;
	
	@Column(name="image_url")
	private String imageUrl;
	
	@Column(name="book_author")
	private String bookAuthor;
	
	@Column(name="description")
	private String description;

	public Book(BookType bookType, String title,
				LocalDate releaseDate, BookCategory bookCategory, String bookAuthor,
				Float averageBookRating) {
		this.bookType = bookType;
		this.title = title;
		this.releaseDate = releaseDate;
		this.bookCategory = bookCategory;
		this.bookAuthor = bookAuthor;
		this.averageBookRating = averageBookRating;
	}

	public Book(BookType bookType, String title, LocalDate releaseDate, BookCategory bookCategory, String imageUrl, String description, String bookAuthor) {
		this.bookType = bookType;
		this.title = title;
		this.releaseDate = releaseDate;
		this.bookCategory = bookCategory;
		this.imageUrl = imageUrl;
		this.description = description;
		this.bookAuthor = bookAuthor;
		this.averageBookRating = 0f;
	}

	public Book(String title) {
		this.title = title;
	}

	public String toString() {
		return String.valueOf(getBookId());
	}
	
}
