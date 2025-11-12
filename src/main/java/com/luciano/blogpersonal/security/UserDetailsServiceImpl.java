package com.luciano.blogpersonal.security;

import com.luciano.blogpersonal.common.exception.ResourceNotFoundException;
import com.luciano.blogpersonal.common.utils.AppConstants;
import com.luciano.blogpersonal.user.model.User;
import com.luciano.blogpersonal.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * Esta clase se encarga de almacenar y cargar los datos del usuario a nivel autenticación cuando se autentica
 */

//Declaramos la clase como un bean del tipo servicio
@Service
//La clase implementa una clase de spring security que se encarga de almacenar y cargar los datos
public class UserDetailsServiceImpl implements UserDetailsService {
    //declaramos el user repository ya que es el parametro de un metodo de la clase
    private final UserRepository userRepository;

    //Constructor
    @Autowired
    public UserDetailsServiceImpl (UserRepository userRepository){
        this.userRepository = userRepository;
    }

    //Metodo de la clase
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername (String usernameOrEmail) throws UsernameNotFoundException {

       User user =  userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(()-> new ResourceNotFoundException("User","username or email", usernameOrEmail));

       if (!user.isActive()){
           throw new UsernameNotFoundException(AppConstants.USER_NOT_ACTIVE);
       }

       //Retorna los datos del usuario autenticado
       return new org.springframework.security.core.userdetails.User(
               user.getUsername(),
               user.getPassword(),
               Collections.singleton(new SimpleGrantedAuthority(user.getRole()))
       );
    }
}
