package com.luciano.blogpersonal.user.service.impl;

import com.luciano.blogpersonal.common.dto.PaginatedResponse;
import com.luciano.blogpersonal.common.exception.BlogApiException;
import com.luciano.blogpersonal.common.exception.ResourceNotFoundException;
import com.luciano.blogpersonal.common.utils.AppConstants;
import com.luciano.blogpersonal.user.dto.UserCreateRequest;
import com.luciano.blogpersonal.user.dto.UserPasswordUpdateRequest;
import com.luciano.blogpersonal.user.dto.UserResponse;
import com.luciano.blogpersonal.user.dto.UserUpdateRequest;
import com.luciano.blogpersonal.user.mapper.UserMapper;
import com.luciano.blogpersonal.user.model.User;
import com.luciano.blogpersonal.user.repository.UserRepository;
import com.luciano.blogpersonal.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    //Primero definimos los beanes que necesitamos
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl (UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override //Muestra de que se está implementado un metodo de UserService
    @Transactional //Garantiza la integridad de los datos, se inspira en principios ACID
    public UserResponse createUser (UserCreateRequest userCreateRequest){
        //Validación de usuario
        if (userRepository.existsByUsername(userCreateRequest.getUsername())){
            throw new BlogApiException(HttpStatus.BAD_REQUEST, AppConstants.USERNAME_EXISTS);
        }

        //Validación de email
        if (userRepository.existsByEmail(userCreateRequest.getEmail())){
            throw new BlogApiException(HttpStatus.BAD_REQUEST, AppConstants.EMAIL_EXISTS);
        }

        //Convertimos nuestra request a una entidad
        User user = userMapper.toEntity(userCreateRequest);

        //Utilizamos el password encoder para codificar la contrasena.
        user.setPassword(passwordEncoder.encode(userCreateRequest.getPassword()));

        //Guardamos el usuario en nuestra base de datos
        User savedUser = userRepository.save(user);

        //Retornamos los datos del usuario creado
        return userMapper.toDto(savedUser);

    }

   @Override
   @Transactional(readOnly = true)
   public UserResponse getUserById (Long userId){
        //Buscamos el usuario mediante el userid
        User user = userRepository.findById(userId)
                //En caso no exista lanzamos un mensaje de error
                .orElseThrow(()-> new ResourceNotFoundException("Usuario", "id", userId));
        //Si todo es exitoso mandamos el usuario encontrado como dto
        return userMapper.toDto(user);
   }

   @Override
   @Transactional(readOnly = true)
    public UserResponse getUserByUsername (String username){
        //Buscamos el usuario por su username
        User user = userRepository.findByUsername(username)
                //Si no lo encontramos lanzamos un mensaje de error
                .orElseThrow(()-> new ResourceNotFoundException("Usuario", "username", username));
        //Si todo es exitoso devolvemos la respuesta
        return userMapper.toDto(user);
   }

   @Override
   @Transactional(readOnly = true)
    public UserResponse getCurrentUser(){
        //Obtenemos el usuario autenticado actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //Extraemos el nombre del usuario autenticado
       String currentUser = authentication.getName();
       //Utilizamos la función para obtener Usuario por su username
       return getUserByUsername(currentUser);

   }

   @Override
   @Transactional
   public UserResponse updateUser (Long userId, UserUpdateRequest userUpdateRequest){
        //Almacenamos el objeto del usuario buscado en un objeto user.
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("Usuario", "id", userId));

        //Obtenemos los datos del usuario autenticado actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //Almacenamos el username del usuario autenticado en la variable currentUsername
        String currentUsername = authentication.getName();

        //Validación si existen los permisos necesarios
        if (!user.getUsername().equals(currentUsername) && !authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(AppConstants.ROLE_ADMIN))){
            throw new BlogApiException(HttpStatus.BAD_REQUEST, AppConstants.INSUFFICIENT_PERMISSIONS);
        }

        //validación de duplicados
        if (userUpdateRequest.getEmail() != null && !user.getEmail().equals(userUpdateRequest.getEmail()) && userRepository.existsByEmail(userUpdateRequest.getEmail())){
            throw new BlogApiException(HttpStatus.BAD_REQUEST, AppConstants.EMAIL_EXISTS);
        }

        //Actualizamos el usuario
        userMapper.updateEntityFromDto(user, userUpdateRequest);

        //Guardamos los cambios
       User updatedUser = userRepository.save(user);

       //Devolvemos la respuesta como un dto de Response
        return userMapper.toDto(updatedUser);

   }

   @Override
   @Transactional
    public UserResponse updatePassword (Long userId, UserPasswordUpdateRequest userPasswordUpdateRequest){

        //Buscamos el usuario que desea cambiar su contrasena
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("Usuario", "Id", userId));

       //Obtenemos los datos del usuario autenticado actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       //Almacenamos el username del usuario autenticado en la variable currentUsername
       String currentUsername = authentication.getName();

       //Validación si existen los permisos necesarios
        if (!user.getUsername().equals(currentUsername)){
            throw  new BlogApiException(HttpStatus.BAD_REQUEST, AppConstants.INSUFFICIENT_PERMISSIONS);
        }

        //Verificación de contrasena
       if (!passwordEncoder.matches(userPasswordUpdateRequest.getCurrentPassword(), user.getPassword())){
           throw new BlogApiException(HttpStatus.BAD_REQUEST, AppConstants.INCORRECT_PASSWORD);
       }

       //Confirmación de contrasena
       if (!userPasswordUpdateRequest.getNewPassword().equals(userPasswordUpdateRequest.getConfirmPassword())){
           throw new BlogApiException(HttpStatus.BAD_REQUEST, AppConstants.PASSWORDS_NOT_MATCH);
       }

       //Codificamos la nueva contrasena
       user.setPassword(passwordEncoder.encode(userPasswordUpdateRequest.getNewPassword()));

       //Guardamos los cambios
       User updatedUser = userRepository.save(user);

       //Devolvemos los datos del usuario actualizado
       return userMapper.toDto(updatedUser);

   }

   @Override
   @Transactional
   public void deleteUser (Long userId){

        //Buscamos el usuario a eliminar
        User user = userRepository.findById(userId)
                .orElseThrow(()->new ResourceNotFoundException("Usuario", "Id", userId));

        //Eliminamos al usuario
        userRepository.delete(user);
   }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<UserResponse> getAllUsers (int pageNo, int pageSize, String sortBy, String sortDir){

        //Creamos un objeto del tipo Sort para manejar el ordenamiento.
        //Empleamos un equal que nos permite ignorar cualquier tipo de caracter extra
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())?
                Sort.by(sortBy).ascending():    //Si sortDir es asc
                Sort.by(sortBy).descending();   //Si sortDir no es asc

        //Creamos la paginación con los atributos requeridos
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        //Creamos una página de usuarios con todos los usuarios de nuestra aplicación
        Page<User> userPage = userRepository.findAll(pageable);
        //Creamos una lista de usuarios con los usuarios de la pagina anteriormente creada
        List<User> users = userPage.getContent();

        //Podemos recorrer cada usuario y convertirlos uno por uno gracias a stream
        List<UserResponse> content = users.stream()
                .map(userMapper::toDto)
                //recolectamos los cambios en la misma lista
                .collect(Collectors.toList());

        //Retornamos el objeto PaginatedResponse con UserResponses además de todos los atributos necesarios para la construcción del objeto
        return PaginatedResponse.<UserResponse>builder()
                .content(content)
                .pageNo(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .first(userPage.isFirst())
                .last(userPage.isLast())
                .build();
    }




   @Override
   @Transactional(readOnly = true)
   //Si ya existe un usuario con el email devuelve false
    public boolean isEmailExist(String email){
        return userRepository.existsByEmail(email);
   }

    @Override
    @Transactional(readOnly = true)
    //Si ya existe un usuario con el username devuelve false
    public boolean isUsernameExist(String username){
        return userRepository.existsByUsername(username);
    }


    @Override
    @Transactional(readOnly = true)
    //Devuelve un usuario según su correo electrónico o username
    public User getUserByUsernameOrEmail(String usernameOrEmail){
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(()-> new ResourceNotFoundException("Usuario", "Username o Email", usernameOrEmail));
    }



}
