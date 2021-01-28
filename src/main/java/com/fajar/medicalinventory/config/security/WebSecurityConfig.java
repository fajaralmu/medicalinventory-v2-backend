package com.fajar.medicalinventory.config.security;
//package com.fajar.livestreaming.config.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@EnableWebSecurity  
//public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//	 
//
//	@Bean
//	public BCryptPasswordEncoder passwordEncoder() { 
//		return new BCryptPasswordEncoder();
//	}
//
//	@Bean 
//	public DaoAuthenticationProvider authenticationProvider() {
//
//		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(); 
//		authProvider.setUserDetailsService(userDetailsService()); 
//		authProvider.setPasswordEncoder(passwordEncoder());
//		return authProvider;
//	}
//
//	@Override
//
//	protected void configure(AuthenticationManagerBuilder auth) {
//
//		auth.authenticationProvider(authenticationProvider());
//	}
//
//	@Override
//
//	protected void configure(HttpSecurity http) throws Exception {
//		log.info("############# configure(HttpSecurity http)  ############");
//		http 
//				.authorizeRequests().antMatchers("/wallPage").hasAnyRole("ADMIN", "USER") 
//				.and() 
//				.authorizeRequests().antMatchers("/login", "/res/**").permitAll().and()
//
////				.authorizeRequests() .antMatchers("/login")
////		            .permitAll()
////		        .antMatchers("/**")
////		            .hasAnyRole("ADMIN", "USER")
////		        .and()
//		            .formLogin()
//		            .loginPage("/login")
//		            .defaultSuccessUrl("/wallPage")
//		            .failureUrl("/login?error=true")
//		            .permitAll()
//		        .and()
//		            .logout()
//		            .logoutSuccessUrl("/login?logout=true")
//		            .invalidateHttpSession(true)
//		            .permitAll()
//		        .and()
//		            .csrf()
//		            .disable();
//
//	}
//
//}
