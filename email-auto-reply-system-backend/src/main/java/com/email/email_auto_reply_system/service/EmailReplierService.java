package com.email.email_auto_reply_system.service;

import com.email.email_auto_reply_system.entity.EmailRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
@Service
public class EmailReplierService {
    WebClient webClient;
    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value(("${gemini.api.key}"))
    private String geminiApiKey;
    public EmailReplierService(WebClient.Builder webClientBuilder){
        this.webClient= WebClient.builder().build();
    }
    public String generateEmailReply(EmailRequest emailRequest){
        //Build the prompt
        String prompt= buildPrompt(emailRequest);
        //craft a request
        Map<String,Object>requestBody= Map.of(
                "contents",new Object[]{
                        Map.of("parts",new Object[]{
                                Map.of("text",prompt)
                        })
                }
        );
        //Do request abd get response
        String response=webClient.post()
                .uri(geminiApiUrl+geminiApiKey)
                .header("content_type","application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        // extract and return response
        return extractResponseContent(response);
    }

    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder promt=new StringBuilder();
        promt.append("Generate a professional email reply for the following email content. Please don't generate a subject line ");
        if(emailRequest.getTone()!=null&& !emailRequest.getTone().isEmpty()){
            promt.append("Use a ").append(emailRequest.getTone()).append(" tone.");
        }
        promt.append("\nOriginal email: \n").append(emailRequest.getEmailContent());
        return promt.toString();
    }
    private String extractResponseContent(String response){
        try{
            ObjectMapper mapper=new ObjectMapper();
            JsonNode jsonNode=mapper.readTree(response);
//            System.out.println(jsonNode.toString());
            return jsonNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text")
                    .asText();
        }
        catch (Exception e){
            return "Error processing request: "+e.getMessage();
        }
    }
}
