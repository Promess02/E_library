
package com.mikolaj.e_library.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mikolaj.e_library.Persistence.CustomDateDeserializer;
import com.mikolaj.e_library.Persistence.CustomDateSerializer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name = "Book")
public class Book implements Serializable {
	public Book() {
	}
	
	@Column(name="bookId", nullable=false, unique=true, length=10)	
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@org.hibernate.annotations.GenericGenerator(name="COM_MIKOLAJ_BOOK_BOOKID_GENERATOR", strategy="identity")
	private int bookId;
	
	@Column(name="bookType", nullable=false)
	private String bookType;
	
	@Column(name="title", nullable=false)
	private String title;
	
	@Column(name="releaseDate", nullable=false)	
	@Temporal(TemporalType.DATE)
	@JsonSerialize(using = CustomDateSerializer.class)
	@JsonDeserialize(using = CustomDateDeserializer.class)
	private java.util.Date releaseDate;
	
	@Column(name="bookCategory", nullable=false)
	private String bookCategory;
	
	@Column(name="AverageBookRating", length=10)
	private Float averageBookRating;
	
	@Column(name="imageUrl")
	private String imageUrl;
	
	@Column(name="bookAuthor", nullable=false)
	private String bookAuthor;
	
	@Column(name="description")
	private String description;

	
	public String toString() {
		return String.valueOf(getBookId());
	}
	
}
