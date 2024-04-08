package com.mikolaj.e_library.model;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Setter
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="Equipment", indexes={ @Index(name="FKEquipment208971", columnList="lastModified") })
public class Equipment implements Serializable {
	public Equipment() {
	}
	
	@Column(name="equipmentId", nullable=false, unique=true, length=10)	
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@org.hibernate.annotations.GenericGenerator(name="COM_MIKOLAJ_EQUIPMENT_EQUIPMENTID_GENERATOR", strategy="identity")
	private int equipmentId;
	
	@Column(name="equipmentName", nullable=false)
	private String equipmentName;
	
	@Column(name="dateOfPurchase", nullable=false)	
	@Temporal(TemporalType.DATE)	
	private java.util.Date dateOfPurchase;
	
	@Column(name="equpmentStatus", nullable=false)
	private String equpmentStatus;
	
	@Column(name="PurchasePrize", nullable=false, length=10)	
	private int purchasePrize;
	
	@Column(name="lastModified", nullable=false, length=10)	
	private int lastModified;

	public String toString() {
		return String.valueOf(getEquipmentId());
	}
	
}
