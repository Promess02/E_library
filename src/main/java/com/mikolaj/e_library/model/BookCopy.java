/**
 * "Visual Paradigm: DO NOT MODIFY THIS FILE!"
 * 
 * This is an automatic generated file. It will be regenerated every time 
 * you generate persistence class.
 * 
 * Modifying its content may cause the program not work, or your work may lost.
 */

/**
 * Licensee: 
 * License Type: Evaluation
 */
package com.mikolaj.e_library.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mikolaj.e_library.DTO.RentalStatus;
import com.mikolaj.e_library.Persistence.CustomDateDeserializer;
import com.mikolaj.e_library.Persistence.CustomDateSerializer;
import com.mikolaj.e_library.Persistence.RentalStatusConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Entity
@Setter
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="book_copy")
public class BookCopy implements Serializable {
	public BookCopy() {
	}
	
	@Column(name="copy_id", nullable=false, unique=true, length=10)
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@org.hibernate.annotations.GenericGenerator(name="COM_MIKOLAJ_BOOKCOPY_COPYID_GENERATOR", strategy="identity")
	private int copyId;
	
	@Column(name="rental_status", nullable=false)
	@Convert(converter = RentalStatusConverter.class)
	private RentalStatus rentalStatus = RentalStatus.FREE;
	
	@Column(name="shelf_place")
	private String shelfPlace;
	
	@Column(name="date_of_purchase", nullable=false)
	@Temporal(TemporalType.DATE)
	@JsonSerialize(using = CustomDateSerializer.class)
	@JsonDeserialize(using = CustomDateDeserializer.class)
	private java.util.Date dateOfPurchase;
	
	@Column(name="quality_status", nullable=false)
	private String qualityStatus;
	
//	@Column(name="book", nullable=false, length=10)
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "book_id")
	private Book book;
	
//	@Column(name="addedBy", nullable=false, length=10)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "worker_id")
	private Worker addedBy;

	public BookCopy(String shelfPlace) {
		this.shelfPlace = shelfPlace;
	}

	public String toString() {
		return String.valueOf(getCopyId());
	}
	
}
