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
@Table(name="warehouse_manager")
public class WarehouseManager implements Serializable {
	public WarehouseManager() {
	}
	
	@Column(name="ware_man_id", nullable=false, unique=true, length=10)
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@org.hibernate.annotations.GenericGenerator(name="COM_MIKOLAJ_WAREHOUSEMANAGER_WAREMANID_GENERATOR", strategy="identity")
	private int wareManId;
	
	@Column(name="monthly_pay", nullable=true, length=10)
	private Integer monthlyPay;
	
//	@Column(name="user", nullable=false, length=10)
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "Id")
	private User user;
	
//	@Column(name="employedBy", nullable=false, length=10)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "emp_man_id")
	private EmployeeManager employedBy;

	
	public String toString() {
		return String.valueOf(getWareManId());
	}
	
}
