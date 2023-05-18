package com.example.springboot;

import apiSteps.GenerateTestCaseApiStep;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {

	@PostMapping("/testcases")
	public String getTestCases(@RequestParam String methodType, @RequestBody String curl) {

		GenerateTestCaseApiStep generateTestCaseApiStep = new GenerateTestCaseApiStep();
		String testcases = generateTestCaseApiStep.generateSpecificTestCaseForAPI(methodType, curl);

		// Return a response
		return testcases;
	}

}
