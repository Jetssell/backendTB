package pe.gob.salud.tb.middleware;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  public JwtAuthenticationFilter(JwtService jwtService){ this.jwtService = jwtService; }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true; // preflight
    String p = request.getRequestURI();
    // Esta chain solo se registra en /api/**, pero igual excluimos auth pública
    return p.startsWith("/api/auth/");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {

    String token = resolveToken(req); // Authorization, cookie tb_token, ?access_token
    if (token != null) {
      try {
        Map<String,Object> claims = jwtService.parseClaims(token);

        String uid = (String) Optional.ofNullable(claims.get("sub")).orElse(claims.get("uid"));
        Collection<SimpleGrantedAuthority> auths = new ArrayList<>();

        Object rolesObj = claims.get("role");
        if (rolesObj instanceof Collection<?> c) {
          for (Object r : c) if (r != null) auths.add(new SimpleGrantedAuthority("ROLE_" + r.toString()));
        } else if (rolesObj instanceof String s && !s.isBlank()) {
          for (String r : s.split(",")) auths.add(new SimpleGrantedAuthority("ROLE_" + r.trim()));
        }

        if (uid != null) {
          var auth = new UsernamePasswordAuthenticationToken(uid, null, auths);
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      } catch (Exception ex) {
        SecurityContextHolder.clearContext(); // token inválido -> seguirá como anónimo
      }
    }
    chain.doFilter(req,res);
  }

  private String resolveToken(HttpServletRequest req) {
    String h = req.getHeader(HttpHeaders.AUTHORIZATION);
    if (h != null && h.startsWith("Bearer ")) return h.substring(7);

    Cookie[] cookies = req.getCookies();
    if (cookies != null) {
      for (Cookie c : cookies) {
        if ("tb_token".equals(c.getName()) && c.getValue() != null && !c.getValue().isBlank()) {
          return c.getValue();
        }
      }
    }
    String q = req.getParameter("access_token");
    if (q != null && !q.isBlank()) return q;

    return null;
  }
}