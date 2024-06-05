
package com.mikolaj.e_library.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mikolaj.e_library.DTO.RentalStatus;
import com.mikolaj.e_library.DTO.CustomDateDeserializer;
import com.mikolaj.e_library.DTO.CustomDateSerializer;
import com.mikolaj.e_library.Persistence.RentalStatusConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

@Entity
@Getter
@Setter
@AllArgsConstructor
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="Rental")
public class Rental implements Serializable {
	
	@Column(name="rental_id", nullable=false, unique=true, length=10)
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@org.hibernate.annotations.GenericGenerator(name="COM_MIKOLAJ_RENTAL_RENTALID_GENERATOR", strategy="identity")
	private int rentalId;
	
	@Column(name="rental_date")
//	@Temporal(TemporalType.DATE)
	@JsonSerialize(using = CustomDateSerializer.class)
	@JsonDeserialize(using = CustomDateDeserializer.class)
	private LocalDate rentalDate;
	
	@Column(name="rental_return_date", nullable=true)
	@JsonSerialize(using = CustomDateSerializer.class)
	@JsonDeserialize(using = CustomDateDeserializer.class)
	private LocalDate rentalReturnDate;
	
	@Column(name="time_of_rental_in_weeks", nullable=false, length=10)
	private int timeOfRentalInWeeks;
	
	@Column(name="is_prolonged")
	private boolean isProlonged;
	
	@Column(name="penalty", length=10)
	private Float penalty;
	
	@Column(name="status")
	@Convert(converter = RentalStatusConverter.class)
	private RentalStatus status = RentalStatus.INACTIVE;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "reader_id")
	private Reader reader;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "copy_id")
	private BookCopy bookCopy;

	public Rental(){
		isProlonged = false;
		penalty = 0f;
		status = RentalStatus.INACTIVE;
		rentalDate = LocalDate.now();
	}

	public Rental(Float penalty, RentalStatus status, Reader reader) {
		this.penalty = penalty;
		this.status = status;
		this.reader = reader;
	}

	public Rental(Reader reader, BookCopy bookCopy, int timeOfRentalInWeeks) {
		this.reader = reader;
		this.bookCopy = bookCopy;
		this.timeOfRentalInWeeks = timeOfRentalInWeeks;
		penalty = 0f;
		isProlonged = false;
		rentalDate = LocalDate.now();
	}

	public String toString() {
		return String.valueOf(getRentalId());
	}
	
}
