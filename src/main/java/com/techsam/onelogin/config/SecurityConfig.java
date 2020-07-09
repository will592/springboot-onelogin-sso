package com.techsam.onelogin.config;

import static org.springframework.security.extensions.saml2.config.SAMLConfigurer.saml;

import com.github.ulisesbocchio.spring.boot.security.saml.annotation.EnableSAMLSSO;
import com.github.ulisesbocchio.spring.boot.security.saml.configurer.ServiceProviderBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@EnableWebSecurity
@EnableSAMLSSO
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Value("${onelogin.metadata-path}")
  private String metadataPath;

  @Value("${onelogin.sp.protocol}")
  private String spProtocol;

  @Value("${onelogin.sp.host}")
  private String spHost;

  @Value("${onelogin.sp.path}")
  private String spBashPath;


  @Value("${onelogin.sp.key-store.file}")
  private String keyStoreFilePath;

  @Value("${onelogin.sp.key-store.alias}")
  private String keyStoreAlias;

  @Value("${onelogin.sp.key-store.password}")
  private String keyStorePassword;

  @Autowired
  private SAMLUserService samlUserService;

  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    //@formatter:off
    http
      .csrf().and()
      .authorizeRequests()
        .antMatchers("/saml/**").permitAll()
        .anyRequest().authenticated()
        .and()
      .apply(saml())
        .userDetailsService(samlUserService)
        .serviceProvider()
          .keyStore()
            .storeFilePath(this.keyStoreFilePath)
            .password(this.keyStorePassword)
            .keyname(this.keyStoreAlias)
            .keyPassword(this.keyStorePassword)
            .and()
            .protocol(spProtocol)
            .hostname(spHost)
            .basePath(spBashPath)
          .and()
      .identityProvider()
        .metadataFilePath(this.metadataPath)
      .and()
    .and();

    //@formatter:on
  }

    @Override
    protected void configure(ServiceProviderBuilder serviceProvider) throws Exception {
        // @formatter:off
        serviceProvider
            .metadataGenerator()
            .entityId("localhost-demo")
        .and()
            .sso()
            .defaultSuccessURL("/home")
            .idpSelectionPageURL("/idpselection")
        .and()
            .logout()
            .defaultTargetURL("/")
        .and()
            .metadataManager()
            .metadataLocations("classpath:/idp-ssocircle.xml")
            .localMetadataLocation("classpath:/sp-ssocircle.xml")
            .refreshCheckInterval(0)
        .and()
            .extendedMetadata()
            .idpDiscoveryEnabled(true)
        .and()
            .localExtendedMetadata()
            .securityProfile("metaiop")
            .sslSecurityProfile("pkix")
            .signMetadata(true)
            .signingKey("localhost")
            .encryptionKey("localhost")
            .requireArtifactResolveSigned(false)
            .requireLogoutRequestSigned(false)
            .idpDiscoveryEnabled(true)
        .and()
            //This Keystore contains also the public key of idp.ssocircle.com
            .keyManager()
            .storeLocation("classpath:/localhost.jks")
            .storePass("foobar")
            .defaultKey("localhost")
            .keyPassword("localhost", "foobar");
        // @formatter:on
    }
}
