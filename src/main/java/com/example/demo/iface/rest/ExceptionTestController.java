package com.example.demo.iface.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.shared.exception.ValidationException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/exception-test")
@Tag(name = "Exception Test", description = "例外測試 API")
public class ExceptionTestController {

	@PostMapping("")
	@Operation(summary = "拋出測試用例外", description = "呼叫後固定拋出 ValidationException，用以測試全局例外處理")
	public ResponseEntity<String> throwException() {
		throw new ValidationException("422", "PROBLEM_REPORT_NOT_FOUND");
	}
}
