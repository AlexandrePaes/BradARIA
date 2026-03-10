# BradARIA

    ### Instruções de instalação e uso:

     1. clone
     2. cd BradARIA
     3. ollama pull phi3
     4. ollama pull nomic-embed-text
     5. # Cria a pasta de configuração do serviço
        sudo mkdir -p /etc/systemd/system/ollama.service.d

        # Cria o arquivo de configuração e adiciona a variável
        echo '[Service]' | sudo tee /etc/systemd/system/ollama.service.d/environment.conf
        echo 'Environment="OLLAMA_HOST=0.0.0.0"' | sudo tee -a /etc/systemd/system/ollama.service.d/environment.conf

        # Recarrega as configurações e reinicia o Ollama
        sudo systemctl daemon-reload
        sudo systemctl restart ollama
     6. docker-compose up --build
     7. Acessar localhost (sem porta ou com a porta indicada)