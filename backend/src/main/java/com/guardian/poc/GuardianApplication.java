package com.guardian.poc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.embedding.EmbeddingModel;

@SpringBootApplication
public class GuardianApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuardianApplication.class, args);
    }

    // Cria o banco vetorial em memória para o PoC
    @Bean
    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel) {
        return new SimpleVectorStore(embeddingModel);
    }
}