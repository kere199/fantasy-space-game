package com.motycka.edu.game.config

import com.motycka.edu.game.account.AccountRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.SecurityFilterChain

private val logger = KotlinLogging.logger {}

@Configuration
@EnableWebSecurity
class SecurityConfig(private val accountRepository: AccountRepository) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/accounts").permitAll() // Allow unauthenticated access to register
                    .requestMatchers("/api/characters/**").authenticated()
                    .anyRequest().permitAll()
            }
            .httpBasic()
            .and()
            .csrf().disable()
        return http.build()
    }

    @Bean
    fun userDetailsService(): UserDetailsService {
        return UserDetailsService { username ->
            logger.info { "Authenticating user: $username" }
            val account = accountRepository.findAccountByUsername(username)
                ?: throw UsernameNotFoundException("User not found: $username")
            User.withUsername(account.username)
                .password("{noop}${account.password}") // {noop} for plain text passwords
                .roles("USER")
                .build()
        }
    }
}