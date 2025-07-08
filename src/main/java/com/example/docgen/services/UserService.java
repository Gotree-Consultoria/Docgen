package com.example.docgen.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.docgen.dto.BatchUserInsertResponseDTO;
import com.example.docgen.dto.FailedUserDTO;
import com.example.docgen.dto.UserMapperDTO;
import com.example.docgen.dto.UserResponseDTO;
import com.example.docgen.entities.User;
import com.example.docgen.exceptions.ResourceNotFoundException;
import com.example.docgen.repositories.UserRepository;

@Service
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	// Injetando via construtor
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
	}

	public List<User> findAll() {

		return userRepository.findAll();

	}

	public User findById(Long id) {

		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário com ID " + id + " não encontrado."));

	}

	public User insertUser(User user) {

		validateUser(user);
		// Criptografia de senhas

		String ecryptedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(ecryptedPassword);

		return userRepository.save(user);

	}

	// Insere uma lista de usuarios
	public BatchUserInsertResponseDTO insertUsers(List<User> users) {
		List<UserResponseDTO> successUsers = new ArrayList<>();
		List<FailedUserDTO> failedUsers = new ArrayList<>();

		for (User user : users) {
			try {
				validateUser(user);
				user.setPassword(passwordEncoder.encode(user.getPassword()));
				User saved = userRepository.save(user);
				successUsers.add(UserMapperDTO.toDto(saved));

			} catch (Exception e) {
				failedUsers.add(new FailedUserDTO(user.getEmail(), e.getMessage()));
			}
		}

		return new BatchUserInsertResponseDTO(successUsers, failedUsers);
	}

	// Validação de segurança
	public void validateUser(User user) {

		// Verifica se existe email duplicado
		userRepository.findByEmail(user.getEmail()).ifPresent(u -> {
			throw new DataIntegrityViolationException("Email já cadastrado: " + u.getEmail());
		});
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		System.out.println("🔍 Buscando usuário por email: " + email);
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
	}

}
