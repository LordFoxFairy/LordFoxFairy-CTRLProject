package com.example.authority.filter;

import cn.hutool.core.util.StrUtil;
import com.example.authority.entity.SysUser;
import com.example.authority.service.SysPermissionService;
import com.example.authority.service.SysUserService;
import com.example.authority.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    
    @Resource
    private JwtUtils jwtUtils;
    
    @Resource
    private SysPermissionService sysPermissionService;
    
    @Resource
    private SysUserService sysUserService;
    
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String jwt = request.getHeader(jwtUtils.getHeader());
        // 这里如果没有jwt，继续往后走，因为后面还有鉴权管理器等去判断是否拥有身份凭证，所以是可以放行的
        // 没有jwt相当于匿名访问，若有一些接口是需要权限的，则不能访问这些接口
        if (StrUtil.isBlankOrUndefined(jwt)) {
            chain.doFilter(request, response);
            return;
        }
        
        Claims claim = jwtUtils.getUsernameFromToken(jwt);
        if (claim == null) {
            throw new JwtException("token 异常");
        }
        if (jwtUtils.validateToken(claim)) {
            throw new JwtException("token 已过期");
        }
        
        String username = claim.getSubject();
        // 获取用户的权限等信息
        
        SysUser sysUser = sysUserService.selectByName(username);
        
        // 构建UsernamePasswordAuthenticationToken,这里密码为null，是因为提供了正确的JWT,实现自动登录
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null,
                sysPermissionService.getGrantedAuthorityList(sysUser));
        SecurityContextHolder.getContext().setAuthentication(token);
        
        chain.doFilter(request, response);
        
    }
}

