package com.fairy.auth.authorication.client.service.impl;

import com.fairy.auth.authorication.client.properties.NotAuthUrlProperties;
import com.fairy.auth.authorication.client.provider.AuthProvider;
import com.fairy.auth.authorication.client.service.IAuthService;
import com.fairy.common.entity.dto.PermissionDTO;
import com.fairy.common.response.Result;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService implements IAuthService {
    /**
     * Authorization认证开头是"bearer "
     */
    private static final String BEARER = "Bearer ";

    @Autowired
    private AuthProvider authProvider;

    /**
     * jwt token 密钥，主要用于token解析，签名验证
     */
    @Value("${spring.security.oauth2.jwt.signingKey}")
    private String signingKey;

    /**
     * 不需要网关签权的url配置(/oauth,/open)
     * 默认/oauth开头是不需要的
     */
    @Autowired
    private NotAuthUrlProperties notAuthUrlProperties;

    @Override
    public Result dataAuthenticate(String authentication, String groupCode, PermissionDTO permissionDTO) {
        return authProvider.dataAuth(authentication, permissionDTO);
    }

    @Override
    public Result authenticate(String authentication, String url, String method) {
        return authProvider.auth(authentication, url, method);
    }

    @Override
    public boolean ignoreAuthentication(String url) {
        Boolean rs = notAuthUrlProperties.getShouldSkipUrls().stream().anyMatch(ignoreUrl -> url.startsWith(StringUtils.trim(ignoreUrl)));
        return rs;
    }

    @Override
    public boolean hasPermission(Result authResult) {
        log.info("签权结果:{}", authResult.getData());
        return authResult.isSuccess() && (boolean) authResult.getData();
    }

    @Override
    public boolean hasPermission(String authentication, String url, String method) {
        // 如果请求未携带token信息, 直接权限
        if (StringUtils.isEmpty(authentication)) {
            log.error("user token is null");
            return Boolean.FALSE;
        }
        //token是否有效，在网关进行校验，无效/过期等
        if (invalidJwtAccessToken(authentication)) {
            return Boolean.FALSE;
        }
        //从认证服务获取是否有权限,远程调用
        return hasPermission(authenticate(authentication, url, method));
    }

    @Override
    public Jws<Claims> getJwt(String jwtToken) {
        if (jwtToken.startsWith(BEARER)) {
            jwtToken = StringUtils.substring(jwtToken, BEARER.length());
        }
        return Jwts.parser()  //得到DefaultJwtParser
                .setSigningKey(signingKey.getBytes()) //设置签名的秘钥
                .parseClaimsJws(jwtToken);
    }

    @Override
    public boolean invalidJwtAccessToken(String authentication) {
        // 是否无效true表示无效
        boolean invalid = Boolean.TRUE;
        try {
            getJwt(authentication);
            invalid = Boolean.FALSE;
        } catch (SignatureException | ExpiredJwtException | MalformedJwtException ex) {
            log.error("user token error :{}", ex.getMessage());
        }
        return invalid;
    }
}
