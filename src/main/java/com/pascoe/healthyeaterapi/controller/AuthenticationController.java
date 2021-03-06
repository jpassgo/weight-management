package com.pascoe.healthyeaterapi.controller;

import com.pascoe.healthyeaterapi.authentication.AuthToken;
import com.pascoe.healthyeaterapi.model.UserAccount;
import com.pascoe.healthyeaterapi.model.UserCredentials;
import com.pascoe.healthyeaterapi.repository.UserAccountRepository;
import com.pascoe.healthyeaterapi.service.AuthenticationUtils;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping
public class AuthenticationController {

  private final UserAccountRepository userAccountRepository;
  private final AuthenticationUtils authenticationUtils;

  @PostMapping("/authentication")
  public ResponseEntity authenticate(
      @RequestBody UserCredentials userCredentials, HttpServletResponse response) {
    Optional<UserAccount> optionalUserAccount =
        userAccountRepository.findByUserCredentialsUserName(userCredentials.getUsername());

    if (optionalUserAccount.isPresent()) {
      if (authenticationUtils.doesPasswordMatch(userCredentials, optionalUserAccount)) {
        String jwt = authenticationUtils.generateAuthToken(userCredentials);
        return new ResponseEntity(new AuthToken(jwt), HttpStatus.OK);
      }
    }

    return new ResponseEntity(HttpStatus.UNAUTHORIZED);
  }

  @RequestMapping(method = RequestMethod.OPTIONS)
  public ResponseEntity options() {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("access-control-allow-origin", "*");

    return new ResponseEntity(httpHeaders, HttpStatus.OK);
  }
}
