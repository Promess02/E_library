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
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="Worker", indexes={ @Index(name="FKWorker234681", columnList="UserId"), @Index(name="FKWorker175065", columnList="employedBy") })
public class Worker implements Serializable {
	public Worker() {
	}
	
	@Column(name="workerId", nullable=false, unique=true, length=10)	
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@org.hibernate.annotations.GenericGenerator(name="COM_MIKOLAJ_WORKER_WORKERID_GENERATOR", strategy="identity")
	private int workerId;
	
	@Column(name="monthlyPay", nullable=true, length=10)	
	private Integer monthlyPay;
	
	@Column(name="UserId", nullable=false, length=10)	
	private int userId;
	
	@Column(name="employedBy", nullable=false, length=10)	
	private int employedBy;
	
	public String toString() {
		return String.valueOf(getWorkerId());
	}
	
}
