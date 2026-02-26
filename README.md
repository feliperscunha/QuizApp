# QuizApp

Este é um aplicativo de quiz para Android desenvolvido em Kotlin, feito para oferecer uma experiência interativa de perguntas e respostas. Os usuários podem participar de quizzes, acompanhar seu histórico, verificar sua posição no placar de líderes e visualizar suas estatísticas de desempenho.

## Funcionalidades

*   **Autenticação de Usuário:** Registro e login de usuários.
*   **Execução de Quiz:** Participe de quizzes com diversas perguntas.
*   **Histórico de Quizzes:** Visualize seu desempenho passado.
*   **Placar de Líderes:** Compare sua pontuação com outros jogadores em um ranking global.
*   **Estatísticas do Usuário:** Acompanhe suas estatísticas detalhadas dos quizzes.


## Estrutura do Projeto

A estrutura do projeto segue as melhores práticas do Android, com pacotes bem definidos para diferentes responsabilidades:

*   `app/src/main/java/com/example/quizapp/`: Pacote raiz do código-fonte.
    *   `auth/`: Lógica de autenticação.
    *   `data/`: Repositórios, banco de dados (Firebase, Room).
    *   `domain/`: Modelos de domínio.
    *   `navigation/`: Lógica de navegação.
    *   `ui/`: Camada de interface do usuário.

---
