package com.shadow.web.api.authority;

import com.shadow.web.service.authority.JwtTokenUtil;
import com.shadow.web.utils.SkipPathRequestMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.baseUrl}")
    private String baseUrl;

    @Value("${jwt.route.authentication.logout}")
    private String logoutUrl;
    
    @Value("${jwt.route.authentication.change-pwd}")
    private String changePwdUrl;

    /**
     * 拦截器 拦截什么暂时不知道
     * @param request
     * @param response
     * @param chain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        List<String> pathsToProcess = Arrays.asList(baseUrl,logoutUrl,changePwdUrl,"/test/**");
        SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(null, pathsToProcess);

        if(!matcher.matches(request))
        {
            chain.doFilter(request, response);
            return;
        }

        String authToken = request.getHeader(this.tokenHeader);
        if(null != authToken && authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7);
        }

        if(null != authToken) {
            String username = jwtTokenUtil.getUsernameFromToken(authToken);
            logger.info("checking authentication for user " + username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // It is not compelling necessary to load the use details from the database. You could also store the information
                // in the token and read it from it. It's up to you ;)
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // For simple validation it is completely sufficient to just check the token integrity.
                // You don't have to call the database compellingly. Again it's up to you ;)
                if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication
                        = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    logger.info("authenticated user " + username + ", setting security context");
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request, response);
    }
}
