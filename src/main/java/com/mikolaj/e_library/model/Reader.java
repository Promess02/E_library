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

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="Reader")
public class Reader implements Serializable {

	@Column(name="reader_id", nullable=false, unique=true, length=10)
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@org.hibernate.annotations.GenericGenerator(name="COM_MIKOLAJ_READER_READERID_GENERATOR", strategy="identity")
	private int readerId;

    @Column(name="penalty", nullable=true, length=10)
	private Integer penalty;
	
//	@Column(name="user", nullable=false, length=10)
	@JoinColumn(name = "Id")
	@OneToOne
	private User user;


    public String toString() {
		return String.valueOf(getReaderId());
	}
	
}
