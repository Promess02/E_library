
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
@Table(name="Rental", indexes={ @Index(name="FKRental429721", columnList="BookCopyId"), @Index(name="FKRental906961", columnList="readerId") })
public class Rental implements Serializable {
	public Rental() {
	}
	
	@Column(name="rentalId", nullable=false, unique=true, length=10)	
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@org.hibernate.annotations.GenericGenerator(name="COM_MIKOLAJ_RENTAL_RENTALID_GENERATOR", strategy="identity")
	private int rentalId;
	
	@Column(name="rentalDate", nullable=false)	
	@Temporal(TemporalType.DATE)	
	private java.util.Date rentalDate;
	
	@Column(name="rentalReturnDate", nullable=false)	
	@Temporal(TemporalType.DATE)	
	private java.util.Date rentalReturnDate;
	
	@Column(name="timeOfRentalInWeeks", nullable=false, length=10)	
	private int timeOfRentalInWeeks;
	
	@Column(name="isProlonged", length=2)
	private byte[] isProlonged;
	
	@Column(name="penalty", length=10)
	private Integer penalty;
	
	@Column(name="status", nullable=false)
	private String status;
	
	@Column(name="readerId", nullable=false, length=10)	
	private int readerId;
	
	@Column(name="BookCopyId", nullable=false, length=10)	
	private int bookCopyId;

	
	public String toString() {
		return String.valueOf(getRentalId());
	}
	
}
