/*
 * Copyright 2017 the original author or authors.
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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.*;

/**
 * @author Greg Turnquist
 */
// tag::1[]
@RunWith(SpringRunner.class)
@SpringBootTest
public class ImageServiceTests {

	@Autowired
	ImageService imageService;

	@MockBean
	ImageRepository repository;
	// end::1[]

	@MockBean
	InitDatabase initDatabase;

	@Before
	public void setUp() throws IOException {
		FileSystemUtils.deleteRecursively(new File(ImageService.UPLOAD_ROOT));
		Files.createDirectory(Paths.get(ImageService.UPLOAD_ROOT));
	}

	@Test
	public void findAllShouldJustReturnTheFlux() {
		// given
		given(repository.findAll()).willReturn(
			Flux.just(
				new Image("1", "alpha.jpg"),
				new Image("2", "bravo.jpg")
			)
		);

		// when
		Flux<Image> images = imageService.findAllImages();

		// then
		then(images).isNotNull();
		then(images.collectList().block())
			.hasSize(2)
			.extracting(Image::getId, Image::getName)
			.contains(
				tuple("1", "alpha.jpg"),
				tuple("2", "bravo.jpg")
			);
	}

	@Test
	public void findOneShouldReturnNotYetFetchedUrl() {
		// when
		Mono<Resource> image = imageService.findOneImage("alpha.jpg");

		// then
		then(image).isNotNull();
		Resource resource = image.block();
		then(resource.getDescription()).isEqualTo("URL [file:upload-dir/alpha.jpg]");
		then(resource.exists()).isFalse();
		then(resource.getClass()).isEqualTo(UrlResource.class);
	}

	@Test
	public void createImageShouldWork() {
		// given
		Image alphaImage = new Image("1", "alpha.jpg");
		Image bravoImage = new Image("2", "bravo.jpg");
		given(repository.save(new Image(any(), alphaImage.getName()))).willReturn(Mono.just(alphaImage));
		given(repository.save(new Image(any(), bravoImage.getName()))).willReturn(Mono.just(bravoImage));
		given(repository.findAll()).willReturn(Flux.just(alphaImage, bravoImage));
		MultipartFile file1 = new MockMultipartFile(alphaImage.getName(), alphaImage.getName(), "JPG", "<image data>".getBytes());
		MultipartFile file2 = new MockMultipartFile(bravoImage.getName(), bravoImage.getName(), "JPG", "<image data>".getBytes());

		// when
		Mono<Void> done = imageService.createImage(Flux.just(file1, file2));

		// then
		then(done.block()).isNull();
	}

	@Test
	public void deleteImageShouldWork() {
		// given
		String imageName = "alpha.jpg";
		Mono<Image> image = Mono.just(new Image("1", imageName));
		given(repository.findByName(any())).willReturn(image);
		given(repository.delete((Image) any())).willReturn(Mono.empty());

		// when
		Mono<Void> done = imageService.deleteImage(imageName);

		// then
		then(done.block()).isNull();
	}

}
