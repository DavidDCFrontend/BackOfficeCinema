package es.dsw.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import es.dsw.connection.MySqlConnection;

import static org.springframework.security.config.Customizer.withDefaults;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	
	private static InMemoryUserDetailsManager objInMemoryUser;
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		http
		   .authorizeHttpRequests((authorize) -> authorize
				   				   .requestMatchers("/styles/**").permitAll()
				   				   .requestMatchers("/bootstrap/**").permitAll()
				   				   .requestMatchers("/img/**").permitAll()
				   				   .requestMatchers("/js/**").permitAll()
				   				   .requestMatchers("/login").permitAll()
				                   .anyRequest().authenticated())
		   .httpBasic(withDefaults())
		   .formLogin(form -> form
				              .loginPage("/login")
				              .loginProcessingUrl("/loginprocess")
				              .successHandler(authenticationSuccessHandler())
				              .permitAll())				
		   .logout((logout) -> logout.logoutSuccessUrl("/login?logout").permitAll());
		   
		
		return http.build();
	}
	
	@Bean
    AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomAuthSuccessHandler();
    }
	
	@Bean
	InMemoryUserDetailsManager userDetailsService() {

	    MySqlConnection objMySqlConnection = new MySqlConnection();
	    objMySqlConnection.open();

	    String sql = """
	            SELECT 
	                USER_FILM.USERNAME_USF AS username,
	                USER_FILM.PASSWORD_USF AS password,
	                ROL_FILM.ROLCODE_RF AS role
	            FROM 
	                DB_FILMCINEMA.USER_FILM
	            INNER JOIN 
	                DB_FILMCINEMA.USERROL_FILM ON USER_FILM.IDUSER_USF = USERROL_FILM.IDUSER_URF
	            INNER JOIN 
	                DB_FILMCINEMA.ROL_FILM ON USERROL_FILM.IDROL_URF = ROL_FILM.IDROL_RF
	            WHERE 
	                USER_FILM.S_ACTIVEROW_USF = b'1' AND 
	                ROL_FILM.S_ACTIVEROW_RF = b'1';
	            """;

	    ResultSet rs = objMySqlConnection.executeSelect(sql); 
	   
	    List<String[]> usuarios = new ArrayList<>();
	    
	    try {
			while(rs.next()) {
				String[] usuario = new String[3];
				usuario[0] = rs.getString("username");
				usuario[1] = rs.getString("password");
				usuario[2] = rs.getString("role");
				usuarios.add(usuario);
			}
			
			Map<String, List<String>> userRolesMap = new HashMap<>();
			Map<String, String> userPasswordsMap = new HashMap<>(); 
			
			for (String[] usuario : usuarios) {
	            String username = usuario[0];
	            String password = usuario[1];
	            String role = usuario[2];

	            // Si el 'username' no está presente en 'userRolesMap', se agrega con un valor inicial de una lista vacía.
	            if (!userRolesMap.containsKey(username)) {
	                userRolesMap.put(username, new ArrayList<>());
	            }
	            userRolesMap.get(username).add(role);

	            // Guardar la contraseña del usuario 
	            userPasswordsMap.put(username, password);
	        }
			
			 objInMemoryUser = new InMemoryUserDetailsManager();

			// Crear usuarios y asignar roles
			 for (Map.Entry<String, List<String>> entry : userRolesMap.entrySet()) {
		            String username = entry.getKey();
		            List<String> roles = entry.getValue();
		            String[] rolesArray = roles.toArray(new String[0]);

		            // Obtener la contraseña desde el mapa de contraseñas
		            String password = userPasswordsMap.get(username);

		            // Crear el UserDetails para cada usuario
		            @SuppressWarnings("deprecation")
		            UserDetails user = User.withDefaultPasswordEncoder()
		                    .username(username)
		                    .password(password) // Usar la contraseña específica del usuario
		                    .roles(rolesArray)
		                    .build();

		            // Agregar el usuario al manager
		            objInMemoryUser.createUser(user);       
		        }
			 
		} catch (SQLException e) {
			
			e.printStackTrace();
			
		} finally {
			objMySqlConnection.close();
		}
	    
	    return objInMemoryUser;
	  }	
}