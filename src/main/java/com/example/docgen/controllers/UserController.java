package com.example.docgen.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.docgen.dto.UserMapperDTO;
import com.example.docgen.dto.UserRequestDTO;
import com.example.docgen.dto.UserResponseDTO;
import com.example.docgen.entities.User;
import com.example.docgen.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
	public ResponseEntity<List<UserResponseDTO>> findAll() {
		List<User> users = userService.findAll();
		return ResponseEntity.ok(UserMapperDTO.toDtoList(users));
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
		User user = userService.findById(id);
		return ResponseEntity.ok(UserMapperDTO.toDto(user));

	}

	@PostMapping
	public ResponseEntity<UserResponseDTO> insertUser(@RequestBody @Valid UserRequestDTO dto) {
		User user = UserMapperDTO.toEntity(dto);
		User createdUser = userService.insertUser(user);
		return ResponseEntity.ok().body(UserMapperDTO.toDto(createdUser));
	}

}
