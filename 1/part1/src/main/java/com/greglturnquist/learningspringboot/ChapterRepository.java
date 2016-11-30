package com.greglturnquist.learningspringboot;

import org.springframework.data.repository.CrudRepository;

public interface ChapterRepository
	extends CrudRepository<Chapter, Long> {

}
