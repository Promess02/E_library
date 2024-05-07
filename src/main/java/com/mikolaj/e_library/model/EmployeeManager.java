
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

	
	public String toString() {
		return String.valueOf(getEmpManId());
	}
	
}
