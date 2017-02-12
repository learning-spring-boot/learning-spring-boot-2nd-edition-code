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

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;

/**
 * @author Greg Turnquist
 */
@Controller
public class HomeController {

	private static final String BASE_PATH = "/images";
	private static final String FILENAME = "{filename:.+}";

	private final ImageService imageService;

	public HomeController(ImageService imageService) {
		this.imageService = imageService;
	}

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("mono", Flux.just(
			new Image("1", "learning-spring-boot-cover.jpg"),
			new Image("2", "learning-spring-boot-2nd-edition-cover.jpg"),
			new Image("3", "bazinga.png")
		));
		return "index";
	}

	@GetMapping(BASE_PATH + "/" + FILENAME + "/raw")
	@ResponseBody
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
	public String createFile(@RequestParam("file") MultipartFile file) {
		try {
			imageService.createImage(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return "redirect:/";
	}

	// TODO: Replace with @DeleteMapping pending https://jira.spring.io/browse/SPR-15206
	@PostMapping(BASE_PATH + "/" + FILENAME)
	public String deleteFile(@PathVariable String filename) {
		try {
			imageService.deleteImage(filename);
		} catch (IOException|RuntimeException e) {
			throw new RuntimeException(e);
		}
		return "redirect:/";
	}

}
