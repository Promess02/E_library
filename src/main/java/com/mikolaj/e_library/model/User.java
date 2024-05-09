
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
@Table(name="`User`")
public class User implements Serializable {
	public User() {
	}
	
	@Column(name="Id", nullable=false, unique=true, length=10)	
	@Id	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@org.hibernate.annotations.GenericGenerator(name="COM_MIKOLAJ_USER_ID_GENERATOR", strategy="identity")
	private int id;
	
	@Column(name="name", nullable=false, length=255)	
	private String name;
	
	@Column(name="surname", nullable=false, length=255)	
	private String surname;
	
	@Column(name="email", nullable=false, length=255)	
	private String email;
	
	@Column(name="phone_number", nullable=true, length=255)
	private String phoneNumber;
	
	@Column(name="password", nullable=false, length=255)	
	private String password;
	
	@Column(name="image_url", nullable=true, length=255)
	private String imageUrl;

	public User(String name, String surname, String email, String phoneNumber, String password) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.password = password;
	}

	public String toString() {
		return String.valueOf(getId());
	}
	
}
