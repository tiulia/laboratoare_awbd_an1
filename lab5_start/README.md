
# Project 5

This project contains examples of Spring Security configurations.
- using JDBC authentication.
- working with roles.
- use roles in Tymeleaf views.

#### Project configuration

Add maven dependencies for Spring Security:
```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity6</artifactId>
</dependency>
```

### Spring Security
Spring Security provides application security.
- password encoding. (BCrypt, SCrypt, Pbkdf2 etc.)
- protection from common security vulnerabilities.
- provides support for **authentication** (verifying credentials) and **authorization** (establishes if a user has the right to perform an action)

Examples of **authentication providers** supported by Spring:

- In Memory authentication
- JDBC/Database authetication
- LDAP(Lightweight Directory Access Protocol): LDAP  store information about users, groups, network resources, and other objects in a hierarchical structure.
- Keyclock: open-source identity and access management solution, supports various identity protocols including OpenID Connect, OAuth 2.0, SAML, and LDAP.
- OpenID: allows users to log in to multiple applications or websites using a single set of credentials. 

### Exercise 1

Add a package config with a class SecurityJpaConfig that will be used when profile mysql is active:
A bean of type BCryptPasswordEncoder will be created for password encryption.

```java
@Configuration
@Profile("mysql")
public class SecurityJpaConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### JDBC authentication

#### UserDetails Service

Spring MVC framework provides the interface UserDetails service and a custom implementation 
of this interface based on some default tables within the database schema.
To provide a custom implementation for UserDetails Service we need:
- **User** and **Authority** JPA entities. 
- **JPA repositories** for User and Authority JPA entities.
- **UserDetail Service** custom implementation that will use User and Authority repositories.  


#### User and Authority

#### Exercise 2
Create a new package domain/security and the entities User and Authority.

```java
import jakarta.persistence.*;
import java.util.Set;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority", joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")}, inverseJoinColumns = {@JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID")})
    private Set<Authority> authorities;

    private Boolean accountNonExpired = true;
    private Boolean accountNonLocked = true;
    private Boolean credentialsNonExpired = true;
    private Boolean enabled = true;
    
}
```

```java
import jakarta.persistence.*;
import java.util.Set;

@Entity
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String role;

    @ManyToMany(mappedBy = "authorities")
    private Set<User> users;
}
```

#### Exercise 3
Update the script schema-mysql.sql by adding DDL commands 
for USER and AUTHORITY tables:

```
DROP TABLE IF EXISTS USER_AUTHORITY;
DROP TABLE IF EXISTS USER;
DROP TABLE IF EXISTS AUTHORITY;
```

```
CREATE TABLE USER(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    account_non_expired BOOLEAN NOT NULL DEFAULT true,
    account_non_locked BOOLEAN NOT NULL DEFAULT true,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE AUTHORITY(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE USER_AUTHORITY(
    user_id BIGINT,
    authority_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES USER(id),
    FOREIGN KEY (authority_id) REFERENCES AUTHORITY(id),
    PRIMARY KEY (user_id, authority_id)
);
```

#### Builder in Lombok
Builder Pattern creational pattern is used to create objects 
from base components, using a step-by-step approach.
Project Lombok has specific annotations for Builder Pattern.

#### Exercise 4
Use Lombok annotations for User and Authority Entities. Avoid using @Data with many-to-many relationships,
as hashCode method will cause an infinite recursion.

```java
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
```
```java
@Singular
@ManyToMany(cascade = CascadeType.ALL)
@JoinTable(name = "user_authority", joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")}, inverseJoinColumns = {@JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID")})
private Set<Authority> authorities;

@Builder.Default
private Boolean accountNonExpired = true;
```

#### Exercise 5
Add the package for security repositories: repositories/security and the repositories for User and Authority.
```java
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByUsername(String username);
}
```

```java
public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
}
```

#### CommandLineRunner 
CommandLineRunner is a Spring Boot interface with a single method (run method). 
Spring Boot will automatically call the run method of all beans implementing the interface CommandLineRunner after the application context has been loaded.

#### Exercise 6
Create package com.awbd.lab5.bootstrap and class com.awbd.lab5.bootstrap.DataLoader.
If there are no users found by UserRepository, the CommandLineRunner will create two users and two roles.
The CommandLineRunner will run only when mysql profile is active and will use the PasswordEncoder defined in the SecurityJpaConfig class.

```java
@AllArgsConstructor
@Component
@Profile("mysql")
public class DataLoader implements CommandLineRunner {

    private AuthorityRepository authorityRepository;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;


    private void loadUserData() {
        if (userRepository.count() == 0){
            Authority adminRole = authorityRepository.save(Authority.builder().role("ROLE_ADMIN").build());
            Authority guestRole = authorityRepository.save(Authority.builder().role("ROLE_GUEST").build());

            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("12345"))
                    .authority(adminRole)
                    .build();

            User guest = User.builder()
                    .username("guest")
                    .password(passwordEncoder.encode("12345"))
                    .authority(guestRole)
                    .build();

            userRepository.save(admin);
            userRepository.save(guest);
        }
    }


    @Override
    public void run(String... args) throws Exception {
        loadUserData();
    }
}
```

#### Exercise 7
Add a package service/security and a JpaUserDetailsService class:
The UserDetail service will use a UserRepository injected by the constructor 
generated with annotation @RequiredArgConstructor.

```java
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("mysql")
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

}
```

#### Exercise 8
Implement UserDetailService method: loadUserByUsername.

```java
@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user;

    Optional<User> userOpt= userRepository.findByUsername(username);
    if (userOpt.isPresent())
        user = userOpt.get();
    else
        throw new UsernameNotFoundException("Username: " + username);

    log.info(user.toString());

    return new org.springframework.security.core.userdetails.User(user.getUsername(),
            user.getPassword(),user.getEnabled(), user.getAccountNonExpired(),
            user.getCredentialsNonExpired(),user.getAccountNonLocked(),
            getAuthorities(user.getAuthorities()));
}

```

#### Exercise 9
Define method: getAuthorities.

```java
private Collection<? extends GrantedAuthority> getAuthorities(Set<Authority> authorities) {
    if (authorities == null){
        return new HashSet<>();
    } else if (authorities.size() == 0){
        return new HashSet<>();
    }
    else{
        return authorities.stream()
                .map(Authority::getRole)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        }
}
```

#### Exercise 10
Add in class SecurityJpaConfig a dependency to JpaUserDetailService.

```java
private final JpaUserDetailsService userDetailsService;

public SecurityJpaConfig(JpaUserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
}
```

#### Exercise 11
Run the application and test the default login form. http://localhost:8080/product

### Security Filters
Web Security is based on Filters. 
The client sends a request. The request passes filter chain. 
A filter may prevent the processing of the request or may alter the request or the response.

The method **SecurityFilterChain** configures a filter chain 
responsible for handling security authorization and authentication in the application.
- **authorizeRequests**: authorization rules for URL patterns.
- **requestMatchers**: URL patterns to which the following authorization rules will apply.
  - **hasAnyRole**: configures the access control for the specified URL patterns based on a set of roles.
  - **hasRole**: configures the access control for the specified URL patterns based on a specific role.
  - **permitAll**: allows access to the specified URL patterns without requiring authentication.
  - **anyRequest**: configures the default access control rule for any request that doesn't match any of the previous rules.
  - **authenticated**: requires authentication for the specified requests.

- **httpBasic()**: configures HTTP Basic authentication, a simple authentication mechanism where the client sends its credentials (username and password) in the HTTP headers with each request. 

- **Customizer.withDefaults()**: sets up some default configurations for HTTP Basic authentication. 
It configures the default realm name (string that identifies the protection space) and default entry point. 

#### Exercise 12
Add a logout button near the login button.

```html
<li class="nav-item">
  <form id="logout-form" th:action="@{/logout}" method="post">
    <button type="submit" class="nav-link btn-primary">
      <i class="fas fa-sign-out-alt"></i> Logout
    </button>
  </form>
</li>
```

#### Exercise 13
Add authorization rules based on roles for specific endpoints.
Add in SecurityJpaConfig:

```java
    @Bean
PasswordEncoder passwordEncoder() {
  return new BCryptPasswordEncoder();
}

@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
  return http
          .authorizeRequests(auth -> auth
                  .requestMatchers("/product/form").hasRole("ADMIN")
                  .requestMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()
                  .requestMatchers("/product/*").hasAnyRole("ADMIN", "GUEST")
                  .requestMatchers("/categories/*").hasAnyRole("ADMIN", "GUEST")
                  .anyRequest().authenticated()
          )
          .userDetailsService(userDetailsService)
          .httpBasic(Customizer.withDefaults())
          .build();
}
```

#### Exercise 14
Add endpoint for custom login and access denied page.
Add in MainController:

```java
@GetMapping("/login")
public String showLogInForm(){ return "login"; }

@GetMapping("/access_denied")
public String accessDeniedPage(){ return "accessDenied"; }
```

#### Exercise 15
Configure the SecurityFilterChain to use custom login and access_denied endpoints/views:

```java
 .formLogin(formLogin ->
        formLogin
            .loginPage("/login")
            .permitAll()
            .loginProcessingUrl("/perform_login")
        )
 .exceptionHandling(ex -> ex.accessDeniedPage("/access_denied"))
```

#### Exercise 16
Add Tymeleaf sec:authorize on roduct/form url.

```
sec:authorize="hasRole('ADMIN')"
```


### Docs

[1] https://www.baeldung.com/spring-security-authentication-with-a-database

[2] https://www.baeldung.com/spring-boot-console-app
