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
@Table(name="Worker")
public class Worker implements Serializable {
	public Worker() {
	}
	
	@Column(name="worker_id", nullable=false, unique=true, length=10)
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@org.hibernate.annotations.GenericGenerator(name="COM_MIKOLAJ_WORKER_WORKERID_GENERATOR", strategy="identity")
	private int workerId;
	
	@Column(name="monthly_pay", nullable=true, length=10)
	private int monthlyPay;

	@Column(name = "pesel", nullable = false)
	private String pesel;

	@Column(name = "pay_account_number", nullable = false)
	private String payAccountNumber;

	@Column(name = "address")
	private String address;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "Id")
	private User user;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "emp_man_id")
	private EmployeeManager employedBy;

	public Worker(Integer monthlyPay, User user, EmployeeManager employedBy) {
		this.monthlyPay = monthlyPay;
		this.user = user;
		this.employedBy = employedBy;
	}

	public Worker(int monthlyPay, User user, EmployeeManager employedBy, String pesel, String payAccountNumber, String address) {
		this.monthlyPay = monthlyPay;
		this.pesel = pesel;
		this.payAccountNumber = payAccountNumber;
		this.address = address;
		this.user = user;
		this.employedBy = employedBy;
	}

	public String toString() {
		return String.valueOf(getWorkerId());
	}
	
}
