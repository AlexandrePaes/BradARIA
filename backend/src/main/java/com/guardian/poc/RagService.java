package com.guardian.poc;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class RagService {

    private final ChatClient chatClient;
    private final SimpleVectorStore vectorStore;

    public RagService(ChatClient.Builder chatClientBuilder, SimpleVectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
        ingestLocalFiles();
    }

    private void ingestLocalFiles() {
        try {
            File dataDir = new File("/app/data");
            if (dataDir.exists() && dataDir.listFiles() != null) {
                for (File file : dataDir.listFiles()) {
                    if (file.getName().endsWith(".pdf") || file.getName().endsWith(".csv")) {
                        TikaDocumentReader reader = new TikaDocumentReader(new FileSystemResource(file));
                        List<Document> documents = reader.get();
                        TokenTextSplitter splitter = new TokenTextSplitter();
                        vectorStore.add(splitter.apply(documents));
                        System.out.println("✅ Arquivo ingerido com sucesso: " + file.getName());
                    }
                }
            } else {
                System.out.println("⚠️ Nenhuma pasta /app/data encontrada ou ela está vazia.");
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao ingerir arquivos: " + e.getMessage());
        }
    }

    public String askQuestion(String question) {
        List<Document> similarDocs = vectorStore.similaritySearch(question);
        StringBuilder context = new StringBuilder();
        for (Document doc : similarDocs) {
            context.append(doc.getContent()).append("\n");
        }

        String prompt = "Você é um assistente jurídico especialista em compliance bancário no Brasil. " +
                "Responda à pergunta do usuário usando APENAS as informações do contexto abaixo. " +
                "Se a resposta não estiver no contexto, diga 'Não encontrei a informação no documento'. " +
                "Responda sempre em Português do Brasil.\n\n" +
                "Contexto:\n" + context + "\n\n" +
                "Pergunta: " + question;

        String answer = chatClient.prompt().user(prompt).call().content();
        saveToMarkdown(question, answer);
        return answer;
    }

    private void saveToMarkdown(String question, String answer) {
        try {
            String content = "# Relatório ARIA\n\n**Pergunta:** " + question + "\n\n**Resposta:**\n" + answer;
            Path outputPath = Paths.get("/app/output/relatorio_" + System.currentTimeMillis() + ".md");
            Files.writeString(outputPath, content);
            System.out.println("📄 Relatório salvo em: " + outputPath.toString());
        } catch (Exception e) {
            System.err.println("❌ Erro ao salvar arquivo .md: " + e.getMessage());
        }
    }
}