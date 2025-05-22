# Sistema de Monitoramento Climático para São Diadério - BA

Este projeto é um sistema de monitoramento climático desenvolvido em **Java** utilizando o framework **Spring Boot**. O objetivo principal é fornecer uma ferramenta robusta para monitorar e gerenciar dados climáticos específicos de São Diadério, Bahia, com um foco especial em análises para a agricultura sustentável na região oeste da Bahia.

---

## 🌟 Funcionalidades Atuais

Atualmente, o sistema oferece as seguintes funcionalidades:

* **Estrutura Inicial do Projeto:** Configurado com Spring Boot e Maven para gerenciamento de dependências.
* **APIs REST para Dados Climáticos:** Endpoints para obter dados climáticos atuais e previsão.
* **Boletim Climático Detalhado:** Geração de boletins formatados com temperatura, umidade, condições e análise de risco agrícola.
* **Análise de Risco Agrícola:** Previsão de risco para plantio nos próximos 3 dias, considerando temperatura e precipitação.
* **Notificações Automáticas:** Envio periódico de boletins climáticos para usuários logados.
* **Sistema de Usuários:** Autenticação e cadastro de usuários, com persistência em arquivo JSON.
* **Persistência de Dados:** Uso de arquivos JSON para armazenar dados de usuários e histórico de notificações.
* **Controle de Versão:** Projeto inicializado e sincronizado com GitHub.
* **Interface de Terminal Interativa:** Menu principal e menu de usuário com opções para interagir com o sistema.
* **Alertas Especiais:** Notificações de alerta para condições climáticas extremas (temperatura alta, umidade baixa).

---

## 🚀 Tecnologias Utilizadas

* **Java 17+**: Linguagem de programação principal.
* **Spring Boot**: Framework para construção de aplicações Java.
    * `spring-boot-starter-web`: Para desenvolvimento de APIs REST.
    * `spring-boot-starter-json` e `jackson-databind`: Para manipulação de dados JSON.
    * `spring-boot-starter-actuator`: Para monitoramento da aplicação.
    * `spring-boot-devtools`: Para facilitar o desenvolvimento com LiveReload.
    * `spring-boot-starter-test`: Para testes.
* **Maven**: Ferramenta de automação de construção e gerenciamento de dependências.
    * **Maven Wrapper**: Permite a execução do projeto sem a necessidade de instalar o Maven globalmente.
* **Lombok**: Biblioteca para reduzir código boilerplate.
* **Gson**: Biblioteca para serialização/desserialização JSON.
* **OpenWeatherMap API**: API externa para obtenção de dados climáticos reais.

---

## 🛠️ Como Rodar o Projeto

### Pré-requisitos

* **Java 17** ou versão superior instalado.
* **Git** instalado no seu sistema.
* Opcional: Maven instalado globalmente (não necessário se usar o Maven Wrapper).
* Uma chave de API válida do OpenWeatherMap. Você precisará adicioná-la no arquivo `src/main/resources/application.properties` como `openweathermap.api.key=SUA_CHAVE_AQUI`.

### Passos para Execução

1.  **Clonar o repositório:**

    ```bash
    git clone [https://github.com/Nanycs13/climate-monitoring-system.git](https://github.com/Nanycs13/climate-monitoring-system.git)
    cd climate-monitoring-system/climatemonitoring
    ```

2.  **Configurar a chave da API do OpenWeatherMap:**
    Abra o arquivo `src/main/resources/application.properties` e substitua `c83a9b491f99c00c44dfd4af7d22b870` pela sua chave de API do OpenWeatherMap.

3.  **Executar o projeto:**

    * Com o Maven Wrapper:
        ```bash
        ./mvnw spring-boot:run
        ```
    * Ou com Maven global:
        ```bash
        mvn spring-boot:run
        ```

4.  **Acessar a aplicação (se aplicável):**
    O sistema é executado em modo de terminal. As APIs REST podem ser acessadas em: [http://localhost:8080](http://localhost:8080).

---

## 📜 Licença

Este projeto é apenas para fins educacionais e não possui uma licença específica no momento.

---
