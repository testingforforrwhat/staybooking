package com.test.staybooking;


import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.maps.GeoApiContext;
import com.test.staybooking.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import java.io.ByteArrayInputStream;
import java.io.IOException;


@Configuration
public class AppConfig {


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()
                                .requestMatchers("/bookings/**").hasAuthority("ROLE_GUEST")
                                .requestMatchers("/listings/search").hasAuthority("ROLE_GUEST")
                                .requestMatchers("/listings/**").hasAuthority("ROLE_HOST")
                                .anyRequest().authenticated()
                )
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public Storage storage() throws IOException {
        String jsonCredentials =
                """
                        {
                          "type": "service_account",
                          "project_id": "staybooking-439106",
                          "private_key_id": "a4b3de27dab6ab6dab17f12eb8a473894c4ae7b0",
                          "private_key": "-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDa5oPBuu4+D18G\\nxAhFb94tmDmRK47VhEWYla/FDQbUcfQeuDio/5XfH5V18RSgFpOt+LbUFZAQKeog\\nhp3fFEldBg3W4bdaRR/Q4Lbhxk5O8x1XaYTszQIVyt4R3unbZ3+ldZVfRpkj8fs1\\n/tF0ukuJ9AGyycBMUPfzOMLysIQnhhW2b5nU3WgriLON/2a1S9takw43ilW0VVU2\\nlzaUriknR6ePEnbWXPClLg6buQ8YirHRA1PbpUGf2rYtv4b5aaBSuYLTiELmseiD\\nKdErCFkz9iG+X166uwnCAxGWxlX1YsXDq00qUowtsQBskuoKphuwIcgdsuZJYS4Y\\nsvGWndznAgMBAAECggEAIB0KPk0cr3kU0UrIBdvMBfdblqsDuNo/VYu8LhXjpx4S\\nmMEg+tYIGCQripJ0GqpFPxQKSmHhcSWdBemJ1pTE0INXW2tguTsD2GJnIgc0LMvo\\nxY6i5BSNVkIdQxTnTPtJlC4EXGZgiA/zcS/xbYT5GQN1jlOfxPJp9cIhg3YW5S82\\nnj41WBhyL6DZUHM4jU+wJHI4RnGIUg6iPKRVSp9QyUCBGSdK4QlmHPmLFfoWjTob\\nWkpwoLqSTkjdBDVL8B3W7fviETioU4RPGnfnuJgZ59cIV8CJLgqb9BuMA7NN2vLt\\nPloo3mu+s2zHDr4L9GccJHpqM8kyn8cn0tI4MkNA2QKBgQDxsgGphHC/MKZu9/Iq\\nnQZ4EogyFtmuOD+RG2fOCfI6jmTfahBI8lkhmhaoBHzQlYJZnjdiaI3ingqNgeZg\\nsu6LSvfkRq67LXypZSiQgzd9h7qjdVlxIhj7gq50Bj+QRB9tK2VjOj4Vxa8bSSk5\\ngeq0C9OqQIdL0IuA+bDZzCwWbwKBgQDn2yLuALyxEo3Ioc5sQt3x/uBpUsr3qvm1\\nDg51vIyh8BfTYHEQckXI/TpptJzsj4C74JRnDdorV6ArhLZbsLaC2xUGe9e+s9Nq\\n80TrK9x0NDcfJ5jNKcQf1BDceezo6Zjdd3jN7LVqiigJtZAKLGPKEGuZw8jf+yMX\\nB0hptSGdCQKBgQC34itbknTJMTVe9etbum4Cim7uVO42m3rZZpVab7zICi/6vlI3\\nfZKMexRDrm5br3QlxLb0ewF/6aezs6HX7iqI9XeWSKSFqfzPUDRFhl+AdPRtikmL\\nRlrld65Mfbh8hSe3wi14CgbUP7uSsd3Yb9xmMR1PyZpl9vs8HDwN6EWepwKBgF4m\\nsZ9+cyFgzGydsEpsON6NRqnecgdyyaYJSDvtThpweexfLGqmTMpacn78VgEGIRuq\\nDflvOZoCIkupv7eqIeMvffE9FxYcxgyXLvRkwkf58CxZkwY1IKxgHFy7skW+nlHI\\nsyjEJ8NUyP9wWEaZUgh69IbjZ031GIHAdlpesICpAoGANohM1/hmq9FZJwDCue2P\\n8ssXK0TJXTEeufAI7FNH2Uqw8dr6feOxUIC8w1DYh3yHLKSbSlAZBhcV+xTDN5FI\\nGipWTWK1B/45t5KhJwl/ZXFhd3U9uXDra87dlJ/nU6hPQOOQOgRHwyBOHA4mTrre\\nNJyUtj5ZACiEQntS4EqjOIA=\\n-----END PRIVATE KEY-----\\n",
                          "client_email": "my-service-account@staybooking-439106.iam.gserviceaccount.com",
                          "client_id": "102679897474732466471",
                          "auth_uri": "https://accounts.google.com/o/oauth2/auth",
                          "token_uri": "https://oauth2.googleapis.com/token",
                          "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
                          "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/my-service-account%40staybooking-439106.iam.gserviceaccount.com",
                          "universe_domain": "googleapis.com"
                        }
                """;
        Credentials credentials = ServiceAccountCredentials.fromStream(new ByteArrayInputStream(jsonCredentials.getBytes()));

//        Credentials credentials = ServiceAccountCredentials.fromStream(getClass().getClassLoader().getResourceAsStream("credentials.json"));

        return StorageOptions.newBuilder().setCredentials(credentials).build().getService();
    }


    @Bean
    public GeoApiContext geoApiContext(@Value("${staybooking.geocoding.key}") String apiKey) {
        return new GeoApiContext.Builder().apiKey(apiKey).build();
    }
}


