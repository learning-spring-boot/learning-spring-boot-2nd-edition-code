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
package com.greglturnquist.learningspringboot.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

import static org.openqa.selenium.chrome.ChromeDriverService.*;

/**
 * Autoconfigure a {@link WebDriver} based on what's available, falling back to {@link HtmlUnitDriver}
 * if none other is available.
 *
 * @author Greg Turnquist
 */
// tag::1[]
@Configuration
@ConditionalOnClass(WebDriver.class)
@EnableConfigurationProperties(WebDriverConfigurationProperties.class)
@Import({ChromeDriverFactory.class, FirefoxDriverFactory.class, SafariDriverFactory.class})
public class WebDriverAutoConfiguration {
// end::1[]

	// tag::2[]
	@Autowired
	WebDriverConfigurationProperties properties;
	// end::2[]

	// tag::3[]
	@Primary
	@Bean(destroyMethod = "quit")
	@ConditionalOnMissingBean(WebDriver.class)
	public WebDriver webDriver(
		FirefoxDriverFactory firefoxDriverFactory,
		SafariDriverFactory safariDriverFactory,
		ChromeDriverFactory chromeDriverFactory) {

		WebDriver driver = firefoxDriver(firefoxDriverFactory);

		if (driver == null) {
			driver = safariDriver(safariDriverFactory);
		}

		if (driver == null) {
			driver = chromeDriver(chromeDriverFactory);
		}

		if (driver == null) {
			driver = htmlUnitDriver();
		}

		return driver;
	}
	// end::3[]

	// tag::4[]
	@Bean
	@Lazy
	public WebDriver firefoxDriver(FirefoxDriverFactory factory) {
		if (properties.getFirefox().isEnabled()) {
			try {
				return factory.getObject();
			} catch (WebDriverException e) {
				e.printStackTrace();
				// swallow the exception
			}
		}
		return null;
	}
	// end::4[]

	@Bean
	@Lazy
	public WebDriver safariDriver(SafariDriverFactory factory) {
		if (properties.getSafari().isEnabled()) {
			try {
				return factory.getObject();
			} catch (WebDriverException e) {
				e.printStackTrace();
				// swallow the exception
			}
		}
		return null;
	}

	@Bean
	@Lazy
	public WebDriver chromeDriver(ChromeDriverFactory factory) {
		if (properties.getChrome().isEnabled()) {
			try {
				return factory.getObject();
			} catch (IllegalStateException e) {
				e.printStackTrace();
				// swallow the exception
			}
		}
		return null;
	}

	// tag::5[]
	@Bean(destroyMethod = "stop")
	@Lazy
	public ChromeDriverService chromeDriverService() {
		System.setProperty("webdriver.chrome.driver",
			"ext/chromedriver");
		return createDefaultService();
	}
	// end::5[]

	// tag::6[]
	@Bean
	@Lazy
	public HtmlUnitDriver htmlUnitDriver() {
		return new HtmlUnitDriver();
	}
	// end::6[]

}
