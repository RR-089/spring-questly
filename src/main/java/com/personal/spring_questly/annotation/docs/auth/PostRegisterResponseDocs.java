package com.personal.spring_questly.annotation.docs.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Register new user")
@ApiResponse(
        responseCode = "201",
        content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                        name = "Register response",
                        value = """
                                {
                                    "status": 201,
                                    "message": "Register user is successful",
                                    "data": "h1zgN_ThY_vfA5mx...."
                                }
                                """
                )
        )
)
public @interface PostRegisterResponseDocs {
}
