package com.email.email_auto_reply_system.controller;

import com.email.email_auto_reply_system.entity.EmailRequest;
import com.email.email_auto_reply_system.service.EmailReplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email-reply")
@CrossOrigin(origins = "*")
public class EmailReplierController {
    @Autowired
    private  EmailReplierService emailReplierService;

//    EmailReplierController(EmailReplierService emailReplierService){
//        this.emailReplierService=emailReplierService;
//    }

    @PostMapping ("/generate")
    public ResponseEntity<String>replyEmail(@RequestBody EmailRequest emailRequest){
        String response=emailReplierService.generateEmailReply(emailRequest);
        return ResponseEntity.ok(response);
    }
}
