package com.personal.spring_questly.annotation.docs.protecteds;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Get requester only route")
@ApiResponse(
        responseCode = "200",
        content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                        name = "Get requester only response",
                        value = """
                                {
                                    "status": 200,
                                    "message": "Yes this is requester only route",
                                    "data": {
                                        "email": "johndoe@gmail.com",
                                        "authorities": [
                                            "QUESTER",
                                            "REQUESTER"
                                        ]
                                    }
                                }
                                """
                )
        )
)
public @interface GetRequesterOnlyResponseDocs {
}
