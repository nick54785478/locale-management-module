package com.example.demo.iface.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.infra.shared.exception.ValidationException;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/exception-test")
public class ExceptionTestController {

	@PostMapping("")
	public ResponseEntity<String> throwException() {
		throw new ValidationException("422", "PROBLEM_REPORT_NOT_FOUND");
	}
}
