package com.jadebloom.goblin_api.shared.service;

import java.io.IOException;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@Service
public class HttpResponseService {

	private final String API_DOCS_URI;

	public HttpResponseService(@Value("${api.docs.uri}") String API_DOCS_URI) {
		this.API_DOCS_URI = API_DOCS_URI;
	}

	public void writeHttpErrorResponse(HttpServletResponse response, Exception ex)
			throws IOException {

		ErrorResponse errorResponse = ErrorResponse.builder(
				ex,
				HttpStatus.FORBIDDEN,
				"Invalid token")
				.type(URI.create(API_DOCS_URI))
				.title("Invalid token")
				.build();

		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(response.getOutputStream(), errorResponse);
	}

}
