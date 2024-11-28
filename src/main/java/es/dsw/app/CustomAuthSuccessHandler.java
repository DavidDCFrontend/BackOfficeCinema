package es.dsw.app;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, 
			                            HttpServletResponse response,
			                            Authentication authentication) 
			                            		throws IOException, ServletException {
		
		LocalDateTime fechaHoraActual = LocalDateTime.now();
		DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		String fechaHoraFormateada = fechaHoraActual.format(formato);
		
		String fechaCodificada = URLEncoder.encode("Fecha del último acceso: " + fechaHoraFormateada, StandardCharsets.UTF_8.toString());
		
		Cookie lastAccessCookie = new Cookie("lastAccess", fechaCodificada);
		lastAccessCookie.setMaxAge(7 * 24 * 60 * 60);
		lastAccessCookie.setHttpOnly(true);
		lastAccessCookie.setPath("/");
		
		response.addCookie(lastAccessCookie);
		
		response.sendRedirect("/home");
	}
}
