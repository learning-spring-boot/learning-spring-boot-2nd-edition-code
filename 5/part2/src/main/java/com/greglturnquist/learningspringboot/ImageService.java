/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.greglturnquist.learningspringboot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.actuate.metrics.repository.InMemoryMetricRepository;
import org.springframework.boot.actuate.metrics.writer.Delta;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Greg Turnquist
 */
@Service
public class ImageService {

	private static String UPLOAD_ROOT = "upload-dir";

	private final ImageRepository repository;
	private final ResourceLoader resourceLoader;

// tag::metric-1[]
	private final CounterService counterService;
	private final GaugeService gaugeService;
	private final InMemoryMetricRepository inMemoryMetricRepository;

	public ImageService(ImageRepository repository,
						ResourceLoader resourceLoader,
						CounterService counterService,
						GaugeService gaugeService,
						InMemoryMetricRepository
								inMemoryMetricRepository) {

		this.repository = repository;
		this.resourceLoader = resourceLoader;
		this.counterService = counterService;
		this.gaugeService = gaugeService;
		this.inMemoryMetricRepository = inMemoryMetricRepository;
	}
// end::metric-1[]

	public Page<Image> findPage(Pageable pageable) {
		return repository.findAll(pageable);
	}


	public Resource findOneImage(String filename) {
		return resourceLoader.getResource("file:" + UPLOAD_ROOT + "/" + filename);
	}

	// tag::metric-2[]
	public void createImage(MultipartFile file) throws IOException {

		if (!file.isEmpty()) {
			Files.copy(file.getInputStream(),
				Paths.get(UPLOAD_ROOT, file.getOriginalFilename()));

			repository.save(new Image(UUID.randomUUID().toString(),
				file.getOriginalFilename()));

			counterService.increment("files.uploaded");

			gaugeService.submit("files.uploaded.lastBytes",
				file.getSize());

			inMemoryMetricRepository.increment(
				new Delta<Number>("files.uploaded.totalBytes",
					file.getSize()));
		}
	}
	// end::metric-2[]

	public void deleteImage(String filename) throws IOException {

		final Image byName = repository.findByName(filename);
		repository.delete(byName);
		Files.deleteIfExists(Paths.get(UPLOAD_ROOT, filename));
	}

	/**
	 * Pre-load some fake images
	 *
	 * @return Spring Boot {@link CommandLineRunner} automatically run after app context is loaded.
	 */
	@Bean
	CommandLineRunner setUp(ImageRepository repository) throws IOException {

		return (args) -> {
			FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));

			Files.createDirectory(Paths.get(UPLOAD_ROOT));

			repository.deleteAll();

			FileCopyUtils.copy("Test file", new FileWriter(UPLOAD_ROOT + "/test"));
			repository.save(new Image(UUID.randomUUID().toString(), "test"));

			FileCopyUtils.copy("Test file2", new FileWriter(UPLOAD_ROOT + "/test2"));
			repository.save(new Image(UUID.randomUUID().toString(), "test2"));

			FileCopyUtils.copy("Test file3", new FileWriter(UPLOAD_ROOT + "/test3"));
			repository.save(new Image(UUID.randomUUID().toString(), "test3"));
		};

	}

}
