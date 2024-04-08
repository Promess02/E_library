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

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Entity
@Setter
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="BookCopy", indexes={ @Index(name="FKBookCopy866098", columnList="addedBy"), @Index(name="FKBookCopy48701", columnList="BookId") })
public class BookCopy implements Serializable {
	public BookCopy() {
	}
	
	@Column(name="copyId", nullable=false, unique=true, length=10)	
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@org.hibernate.annotations.GenericGenerator(name="COM_MIKOLAJ_BOOKCOPY_COPYID_GENERATOR", strategy="identity")
	private int copyId;
	
	@Column(name="rentalStatus", nullable=false)
	private String rentalStatus;
	
	@Column(name="shelfPlace")
	private String shelfPlace;
	
	@Column(name="dateOfPurchase", nullable=false)	
	@Temporal(TemporalType.DATE)	
	private java.util.Date dateOfPurchase;
	
	@Column(name="qualityStatus", nullable=false)
	private String qualityStatus;
	
	@Column(name="BookId", nullable=false, length=10)	
	private int bookId;
	
	@Column(name="addedBy", nullable=false, length=10)	
	private int addedBy;

	public String toString() {
		return String.valueOf(getCopyId());
	}
	
}
