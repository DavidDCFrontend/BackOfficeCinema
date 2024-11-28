package es.dsw.controllers;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.dsw.connection.MySqlConnection;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class MainController {

	@GetMapping({"/", "/home"})
	public String home(Model model) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = null;
		
        if (authentication != null && authentication.isAuthenticated()) {	
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {    	
                UserDetails userDetails = (UserDetails) principal;
                username = userDetails.getUsername();
            }
        }
        
        if(username != null) {
        	MySqlConnection objMySqlConnection = new MySqlConnection();
    	    objMySqlConnection.open();
    	    
    	    String sql = """
    	    		SELECT user_film.name_usf as name, user_film.firstsurname_usf as firstsurname
    	    		FROM db_filmcinema.user_film
    	    		WHERE user_film.username_usf = ?;
    	    		""";
    	    
    	    try (ResultSet rs = objMySqlConnection.executeSelect(sql, username)) {
                if (rs != null && rs.next()) {
                    String name = rs.getString("name");
                    String firstsurname = rs.getString("firstsurname");

                    // Agregar datos al modelo para usarlos en la vista
                    model.addAttribute("name", name);
                    model.addAttribute("firstsurname", firstsurname);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                objMySqlConnection.close();
            }
        }
        
		return "home";
	}
	
	@GetMapping("/login")
	public String login(HttpServletRequest request, Model model) {
		
		Cookie[] cookies = request.getCookies();
		
		if(cookies != null) {
			for(Cookie cookie : cookies) {
				if("lastAccess".equals(cookie.getName())) {
					try {
	                    String valorDecodificado = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8.toString());
	                    model.addAttribute("lastAccess", valorDecodificado);
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
					break;
				}
			}
		}
		
		System.out.println("Atributo 'lastAccess' en el modelo: " + model.getAttribute("lastAccess"));

		return "login";
	}
}
