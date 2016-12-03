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
package com.greglturnquist.learningspringboot.images;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Greg Turnquist
 */
// tag::headline[]
@RestController
public class UploadController {
	// end::headline[]

	private static final String BASE_PATH = "/images";
	private static final String FILENAME = "{filename:.+}";

	private final ImageService imageService;

	public UploadController(ImageService imageService) {
		this.imageService = imageService;
	}

	@GetMapping(BASE_PATH + "/" + FILENAME + "/raw")
	public ResponseEntity<?> oneRawImage(@PathVariable String filename) {

		try {
			Resource file = imageService.findOneImage(filename);
			return ResponseEntity.ok()
				.contentLength(file.contentLength())
				.contentType(MediaType.IMAGE_JPEG)
				.body(new InputStreamResource(file.getInputStream()));
		} catch (IOException e) {
			return ResponseEntity.badRequest()
				.body("Couldn't find " + filename + " => " + e.getMessage());
		}

	}

	@PostMapping(value = BASE_PATH)
	public ResponseEntity<?> createFile(@RequestParam("file") MultipartFile file) {
		try {
			imageService.createImage(file);
			return ResponseEntity.ok(
				"Successfully uploaded " + file.getName());
		} catch (IOException e) {
			return ResponseEntity.badRequest().body(
				"Failed to upload " + file.getName() + " => " + e.getMessage());
		}
	}

	// tag::secured[]
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping(BASE_PATH + "/" + FILENAME)
	public ResponseEntity<?> deleteFile(
		@PathVariable String filename) {
		try {
			imageService.deleteImage(filename);
			return ResponseEntity.ok(
				"Successfully deleted " + filename);
		} catch (IOException|RuntimeException e) {
			return ResponseEntity.badRequest().body(
				"Failed to delete " + filename + " => "
									+ e.getMessage());
		}
	}
	// end::secured[]

}
