
package com.mikolaj.e_library.model;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Getter
@Setter
@Table(name="employee_manager")
public class EmployeeManager implements Serializable {
	public EmployeeManager() {
	}
	
	@Column(name="emp_man_id", nullable=false, unique=true, length=10)
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@org.hibernate.annotations.GenericGenerator(name="COM_MIKOLAJ_EMPLOYEEMANAGER_EMPMANID_GENERATOR", strategy="identity")
	private int empManId;
	
	@Column(name="monthly_pay", length=10)
	private Integer monthlyPay;
	
//	@Column(name="user", nullable=false, length=10)
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "Id")
	private User user;

	@Column(name = "pesel", nullable = false)
	private String pesel;

	@Column(name = "pay_account_number", nullable = false)
	private String payAccountNumber;

	@Column(name = "address")
	private String address;

	public EmployeeManager(Integer monthlyPay, User user) {
		this.monthlyPay = monthlyPay;
		this.user = user;
		this.pesel = pesel;
		this.payAccountNumber = payAccountNumber;
		this.address = address;
	}

	public EmployeeManager(Integer monthlyPay, User user,
						   String PESEL, String payAccountNumber, String address) {
		this.monthlyPay = monthlyPay;
		this.user = user;
		this.pesel = PESEL;
		this.payAccountNumber = payAccountNumber;
		this.address = address;
	}



	public String toString() {
		return String.valueOf(getEmpManId());
	}
	
}
