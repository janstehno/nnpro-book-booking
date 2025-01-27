package cz.upce.nnpro.bookbooking.components;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class AuthorityLoggerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull
            HttpServletRequest request,
            @NonNull
            HttpServletResponse response,
            @NonNull
            FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            System.out.printf("Authenticated request to [%s] by [%s] with authorities %s\n",
                              request.getRequestURI(),
                              authentication.getName(),
                              authentication.getAuthorities());
        } else {
            System.out.printf("Unauthenticated request to [%s]\n", request.getRequestURI());
        }
        filterChain.doFilter(request, response);
    }
}
