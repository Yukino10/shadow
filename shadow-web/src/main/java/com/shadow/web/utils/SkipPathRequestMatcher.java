package com.shadow.web.utils;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

public class SkipPathRequestMatcher implements RequestMatcher {
    private OrRequestMatcher matchers;
    private OrRequestMatcher processingMatchers;
    
    public SkipPathRequestMatcher(List<String> pathsToSkip, List<String> processingPaths) {
        if(null != pathsToSkip) {
            List<RequestMatcher> passList = pathsToSkip.stream().map(path -> new AntPathRequestMatcher(path)).collect(Collectors.toList());
            matchers = new OrRequestMatcher(passList);
        }
        if(null != processingPaths){
            List<RequestMatcher> processList = processingPaths.stream().map(path -> new AntPathRequestMatcher(path)).collect(Collectors.toList());
            processingMatchers = new OrRequestMatcher(processList);
        }
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        if (null != matchers && matchers.matches(request)) {
            return false;
        }
        return null == processingMatchers ? false : processingMatchers.matches(request) ? true : false;
    }
}
