
package com.mikolaj.e_library.model;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mikolaj.e_library.DTO.RentalStatus;
import com.mikolaj.e_library.Persistence.CustomDateDeserializer;
import com.mikolaj.e_library.Persistence.CustomDateSerializer;
import com.mikolaj.e_library.Persistence.RentalStatusConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="Rental")
public class Rental implements Serializable {
	public Rental() {
	}
	
	@Column(name="rental_id", nullable=false, unique=true, length=10)
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@org.hibernate.annotations.GenericGenerator(name="COM_MIKOLAJ_RENTAL_RENTALID_GENERATOR", strategy="identity")
	private int rentalId;
	
	@Column(name="rental_date", nullable=false)
	@Temporal(TemporalType.DATE)
	@JsonSerialize(using = CustomDateSerializer.class)
	@JsonDeserialize(using = CustomDateDeserializer.class)
	private java.util.Date rentalDate;
	
	@Column(name="rental_return_date", nullable=false)
	@Temporal(TemporalType.DATE)
	@JsonSerialize(using = CustomDateSerializer.class)
	@JsonDeserialize(using = CustomDateDeserializer.class)
	private java.util.Date rentalReturnDate;
	
	@Column(name="time_of_rental_in_weeks", nullable=false, length=10)
	private int timeOfRentalInWeeks;
	
	@Column(name="is_prolonged")
	private boolean isProlonged;
	
	@Column(name="penalty", length=10)
	private Integer penalty;
	
	@Column(name="status", nullable=false)
	@Convert(converter = RentalStatusConverter.class)
	private RentalStatus status = RentalStatus.INACTIVE;
	
//	@Column(name="reader", nullable=false, length=10)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "reader_id")
	private Reader reader;
	
//	@Column(name="BookCopy", nullable=false, length=10)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "copy_id")
	private BookCopy bookCopy;

	
	public String toString() {
		return String.valueOf(getRentalId());
	}
	
}
