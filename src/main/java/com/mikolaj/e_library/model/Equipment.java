package com.mikolaj.e_library.model;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mikolaj.e_library.Persistence.CustomDateDeserializer;
import com.mikolaj.e_library.Persistence.CustomDateSerializer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Setter
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="Equipment")
public class Equipment implements Serializable {
	public Equipment() {
	}
	
	@Column(name="equipment_id", nullable=false, unique=true, length=10)
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@org.hibernate.annotations.GenericGenerator(name="COM_MIKOLAJ_EQUIPMENT_EQUIPMENTID_GENERATOR", strategy="identity")
	private int equipmentId;
	
	@Column(name="equipment_name", nullable=false)
	private String equipmentName;
	
	@Column(name="date_of_purchase", nullable=false)
	@Temporal(TemporalType.DATE)
	@JsonSerialize(using = CustomDateSerializer.class)
	@JsonDeserialize(using = CustomDateDeserializer.class)
	private java.util.Date dateOfPurchase;
	
	@Column(name="equipment_status", nullable=false)
	private String equipmentStatus;
	
	@Column(name="purchase_prize", nullable=false, length=10)
	private int purchasePrize;
	
//	@Column(name="lastModified", nullable=false, length=10)
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ware_man_id")
	private WarehouseManager lastModified;

	public String toString() {
		return String.valueOf(getEquipmentId());
	}
	
}
