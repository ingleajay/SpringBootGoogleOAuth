package com.googleauth;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;


@Controller
public class HomeController {
	
	@Autowired
	private OAuth2AuthorizedClientService authorizedClientService;

	@RequestMapping("/home")
	public String home() {
		return "home";
	}

	@GetMapping("/user")
	public Principal user(Principal principal) {
		System.out.println("username" + principal.getName());
		return principal;
	}

	@RequestMapping("/welcome")
	public String welcome(Principal principal , Model m , OAuth2AuthenticationToken authentication) {
		String user = principal.getName();
		OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
			        authentication.getAuthorizedClientRegistrationId(), 
			          authentication.getName()
		);
		
		// you will get url => https://www.googleapis.com/oauth2/v3/userinfo
		
		String userInfoEndpointUri = client.getClientRegistration()
				  .getProviderDetails().getUserInfoEndpoint().getUri();
		
				if (!StringUtils.isEmpty(userInfoEndpointUri)) {
					
					// to consumes rest api 
					
				    RestTemplate restTemplate = new RestTemplate();
				    HttpHeaders headers = new HttpHeaders();
				    headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken()
				      .getTokenValue());
				    
				    HttpEntity entity = new HttpEntity("", headers);
				    ResponseEntity<Map> response = restTemplate
				      .exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
				    Map userAttributes = response.getBody();
				    m.addAttribute("user",userAttributes );
				}
	    
		return "welcome";
	}
}
