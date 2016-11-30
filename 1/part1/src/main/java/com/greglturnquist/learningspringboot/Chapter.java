package com.greglturnquist.learningspringboot;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Chapter {

	@Id @GeneratedValue
	private Long id;

	private String name;

	private Chapter() {
		// No one but JPA uses this.
	}

	public Chapter(String name) {
		this.name = name;
	}

}
