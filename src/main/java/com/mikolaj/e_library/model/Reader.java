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
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="Reader", indexes={ @Index(name="FKReader340389", columnList="UserId") })
public class Reader implements Serializable {
	public Reader() {
	}
	
	@Column(name="readerId", nullable=false, unique=true, length=10)	
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@org.hibernate.annotations.GenericGenerator(name="COM_MIKOLAJ_READER_READERID_GENERATOR", strategy="identity")
	private int readerId;
	
	@Column(name="penalty", nullable=true, length=10)	
	private Integer penalty;
	
	@Column(name="UserId", nullable=false, length=10)	
	private int userId;
	
	private void setReaderId(int value) {
		this.readerId = value;
	}
	
	public int getReaderId() {
		return readerId;
	}
	
	public int getORMID() {
		return getReaderId();
	}
	
	public void setPenalty(int value) {
		setPenalty(Integer.valueOf(value));
	}
	
	public void setPenalty(Integer value) {
		this.penalty = value;
	}
	
	public Integer getPenalty() {
		return penalty;
	}
	
	public void setUserId(int value) {
		this.userId = value;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public String toString() {
		return String.valueOf(getReaderId());
	}
	
}
