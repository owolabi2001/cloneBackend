package com.clone.cloneBackend.controller;



import com.clone.cloneBackend.domain.AppUser;
import com.clone.cloneBackend.dto.AuthenticationDto;
import com.clone.cloneBackend.dto.EmailDetails;
import com.clone.cloneBackend.dto.RegistrationDto;
import com.clone.cloneBackend.dto.UpdatePasswordDto;
import com.clone.cloneBackend.dto.response.GenericResponse;

import com.clone.cloneBackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;



    @PostMapping(path = "/register")
    public ResponseEntity<GenericResponse> register(@RequestBody RegistrationDto registrationDto){
        RegistrationDto status = userService.register(registrationDto);
        RegistrationDto returnResponse = new RegistrationDto();
        if(status != null){
            returnResponse.setEmail(status.getEmail());
            returnResponse.setFirstName(status.getFirstName());
            returnResponse.setLastName(status.getLastName());
            returnResponse.setGender(status.getGender());
            returnResponse.setPassword("saved");
            returnResponse.setDateOfBirth(status.getDateOfBirth());
            returnResponse.setRole(status.getRole());
            returnResponse.setAddress(status.getAddress());


            return new ResponseEntity<>(
                    new GenericResponse("00"
                            ,"Registration Successfull"
                            ,returnResponse,null)
                    , HttpStatus.CREATED);
        }else{
            returnResponse.setEmail(registrationDto.getEmail());
            return new ResponseEntity<>(
                    new GenericResponse("11",
                            "user with email "+ registrationDto.getEmail() +" already exist"
                            ,returnResponse,null)
                    ,HttpStatus.BAD_GATEWAY);
        }


    }

    @PostMapping(path = "/authenticate")
    public ResponseEntity<GenericResponse> authenticate
            (@RequestBody AuthenticationDto authenticationDto){
        GenericResponse response = userService.authenticate(authenticationDto);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }


    @PostMapping(path = "/updatePassword")
    public ResponseEntity<GenericResponse> updatePasswordEmail(@RequestBody EmailDetails emailDetails){
        int status = userService.sendSimpleMail(emailDetails);

        if(status == 0){
            return  new ResponseEntity<>(
                    new GenericResponse("11","User not found",null,null)
                    ,HttpStatus.CONTINUE
                    );
        }
        else{
            return new ResponseEntity<>(new GenericResponse("00",
                    "Message Sent",
                    null,null),HttpStatus.ACCEPTED);

        }

    }
    public ResponseEntity<GenericResponse> updatePassword(){
        return new ResponseEntity<>(new GenericResponse(),HttpStatus.CONTINUE);
    }


    @PostMapping(path = "/resetPassword/{token}")
    public ResponseEntity<GenericResponse> resetPassword
            (@PathVariable String token, @RequestBody UpdatePasswordDto updatedpassword){

        userService.resetPassword(token,updatedpassword.getPassword());
        GenericResponse response = new GenericResponse();
        response.setCode("00");
        response.setMessage("Check your mail");
        return new ResponseEntity<>(response,HttpStatus.CONTINUE);
    }

}
