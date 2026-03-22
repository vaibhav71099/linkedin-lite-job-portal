package com.vaibhav.jobportal.controller;

import com.vaibhav.jobportal.dto.ApiResponse;
import com.vaibhav.jobportal.dto.SearchResponse;
import com.vaibhav.jobportal.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SearchController {

	private final SearchService searchService;

	public SearchController(SearchService searchService) {
		this.searchService = searchService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<SearchResponse>> search(@RequestParam(defaultValue = "") String query) {
		return ResponseEntity.ok(new ApiResponse<>(true, "Search results fetched successfully.", searchService.search(query)));
	}
}
