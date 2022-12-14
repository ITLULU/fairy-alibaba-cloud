package com.fairy.auth.authorization.config;

import com.fairy.auth.authorization.exception.CustomWebResponseExceptionTranslator;
import com.fairy.auth.authorization.oauth2.enhance.JwtTokenEnhancer;
import com.fairy.auth.authorization.oauth2.granter.CustomUserDetailsService;
import com.fairy.auth.authorization.properties.SkipUrls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

/**
 * @author ?????????
 * @date 2022/10/13 19:39
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtTokenEnhancer jwtTokenEnhancer;

    @Autowired
    @Qualifier("jwtTokenStore")
    private TokenStore tokenStore;

    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        //?????????????????? ?????????client????????????header???body???
        security.allowFormAuthenticationForClients()
                // ??????/oauth/token_key???????????????????????????
//                .tokenKeyAccess("permitAll()")
                .tokenKeyAccess("isAuthenticated()")
                // ??????/oauth/check_token??????????????????????????????
                .checkTokenAccess("permitAll()");
//                .checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // ??????????????????????????????????????????????????????oauth_client_details???
        clients.jdbc(dataSource).passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {


        // ??????token???????????????????????????tokenServices?????????,????????????????????????
        // ?????????????????????TokenStore???TokenGranter???OAuth2RequestFactory
        endpoints.tokenStore(tokenStore)
                .accessTokenConverter(jwtAccessTokenConverter)
                //??????tokenEnhancer
                .authenticationManager(authenticationManager)
                .authorizationCodeServices(authorizationCodeServices())
                .approvalStore(approvalStore())
                .exceptionTranslator(customExceptionTranslator())
                .tokenEnhancer(tokenEnhancerChain())
                .userDetailsService(userDetailsService)
                //????????????????????????????????? true
                .reuseRefreshTokens(false)
//                .tokenGranter(tokenGranter(endpoints))
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
    }

    /**
     * ?????????OAuth2????????????
     *
     * @return CustomWebResponseExceptionTranslator
     */
    @Bean
    public WebResponseExceptionTranslator<OAuth2Exception> customExceptionTranslator() {
        return new CustomWebResponseExceptionTranslator();
    }

    /**
     * ???????????????????????????
     *
     * @return JdbcApprovalStore
     */
    @Bean
    public ApprovalStore approvalStore() {
        return new JdbcApprovalStore(dataSource);
    }

    /**
     * ?????????????????????????????????code
     *
     * @return JdbcAuthorizationCodeServices
     */
    @Bean
    protected AuthorizationCodeServices authorizationCodeServices() {
        // ??????????????????????????????????????????jdbc?????????oauth_code???
        return new JdbcAuthorizationCodeServices(dataSource);
    }


    /**
     * ?????????token
     *
     * @return tokenEnhancerChain
     */
    @Bean
    public TokenEnhancerChain tokenEnhancerChain() {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(jwtTokenEnhancer,jwtAccessTokenConverter));
        return tokenEnhancerChain;
    }



//    /**
//     * ??????????????????granter,????????????????????????
//     *
//     * @param endpoints
//     * @return
//     * @auth joe_chen
//     */
//    public TokenGranter tokenGranter(final AuthorizationServerEndpointsConfigurer endpoints) {
//        List<TokenGranter> granters = Lists.newArrayList(endpoints.getTokenGranter());
//        granters.add(new MobileTokenGranter(
//                authenticationManager,
//                endpoints.getTokenServices(),
//                endpoints.getClientDetailsService(),
//                endpoints.getOAuth2RequestFactory()));
//        return new CompositeTokenGranter(granters);
//    }

}