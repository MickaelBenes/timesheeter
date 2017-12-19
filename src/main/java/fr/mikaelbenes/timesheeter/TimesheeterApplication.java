package fr.mikaelbenes.timesheeter;

import fr.mikaelbenes.timesheeter.data.domain.Activity;
import fr.mikaelbenes.timesheeter.data.repository.ActivityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@SpringBootApplication
public class TimesheeterApplication {

	private static final Logger log = LoggerFactory.getLogger( TimesheeterApplication.class );

	public static void main( String[] args ) {
		SpringApplication.run( TimesheeterApplication.class, args );
	}

	// CORS
	@Bean
	FilterRegistrationBean corsFilter( @Value("${tagit.origin:http://localhost:50001}") String origin ) {
		return new FilterRegistrationBean(new Filter() {

			@Override
			public void doFilter( ServletRequest req, ServletResponse res, FilterChain chain ) throws IOException, ServletException {
				HttpServletRequest request		= (HttpServletRequest) req;
				HttpServletResponse response	= (HttpServletResponse) res;
				String method					= request.getMethod();

				log.info( "origin default value: {}", origin );
				log.info( "request method: {}", method );

				// this origin value could just as easily have come from a database
				response.setHeader( "Access-Control-Allow-Origin", origin );
				response.setHeader( "Access-Control-Allow-Credentials", "true" );
				response.setHeader( "Access-Control-Allow-Methods", "POST,PUT,PATCH,GET,OPTIONS,DELETE" );
				response.setHeader( "Access-Control-Max-Age", Long.toString(3600) );
				response.setHeader(
						"Access-Control-Allow-Headers",
						"Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization"
				);

				if ( "OPTIONS".equals(method) ) {
					response.setStatus( HttpStatus.OK.value() );
				}
				else {
					chain.doFilter( req, res );
				}
			}

			public void init( FilterConfig filterConfig ) {}

			public void destroy() {}

		});
	}

	@Configuration
	@EnableWebSecurity
	@Order( SecurityProperties.ACCESS_OVERRIDE_ORDER )
	protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {

		@Autowired
		public void globalUserDetails( AuthenticationManagerBuilder auth ) throws Exception {
			auth.inMemoryAuthentication()
					.withUser( "user" )
						.password( "pass" )
						.roles( "USER", "ADMIN" );
		}

		@Override
		protected void configure( HttpSecurity http ) throws Exception {
			http.httpBasic()
				.and()
				.authorizeRequests()
					.antMatchers( HttpMethod.OPTIONS, "/**" ).permitAll()
//					.anyRequest().authenticated()
					.anyRequest().hasRole( "USER" )
				.and()
				.csrf().disable();
		}

	}

	CommandLineRunner init(ActivityRepository repoActivity) {
		return evt -> {
			Activity activityInit	= new Activity("Activity init.", "Redmine", "0");
			LocalDateTime stopTime	= LocalDateTime.parse(activityInit.getStartTime().toString())
					.plusSeconds(34)
					.plusMinutes(13)
					.plusHours(2);
			activityInit.setStopTime(stopTime);

			repoActivity.save(activityInit);
		};
	}

}
