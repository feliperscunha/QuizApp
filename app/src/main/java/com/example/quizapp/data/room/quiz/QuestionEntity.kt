package com.example.quizapp.data.room.quiz

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val quizId: String,
    val title: String,
    val subtitle: String,
    val question: String,
    val correctAnswer: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String,
)

/*

[
  {
    "id": "1",
    "title": "Ciências",
    "subtitle": "Noções Básicas de Biologia",
    "questionList": [
      {
        "question": "Qual é a usina de energia da célula?",
        "options": ["Núcleo", "Mitocôndria", "Retículo Endoplasmático", "Complexo de Golgi"],
        "correct": "Mitocôndria"
      },
      {
        "question": "Qual gás as plantas absorvem durante a fotossíntese?",
        "options": ["Oxigênio", "Dióxido de Carbono", "Nitrogênio", "Hidrogênio"],
        "correct": "Dióxido de Carbono"
      },
      {
        "question": "Qual é o maior órgão do corpo humano?",
        "options": ["Coração", "Fígado", "Pele", "Pulmões"],
        "correct": "Pele"
      },
      {
        "question": "Qual é a função dos glóbulos vermelhos?",
        "options": ["Transportar oxigênio", "Digerir alimentos", "Produzir anticorpos", "Armazenar energia"],
        "correct": "Transportar oxigênio"
      },
      {
        "question": "Qual cientista é conhecido pela teoria da evolução por seleção natural?",
        "options": ["Isaac Newton", "Charles Darwin", "Albert Einstein", "Gregor Mendel"],
        "correct": "Charles Darwin"
      }
    ]
  },
  {
    "id": "2",
    "title": "Geografia",
    "subtitle": "Capitais do Mundo",
    "questionList": [
      {
        "question": "Qual é a capital da França?",
        "options": ["Berlim", "Madri", "Paris", "Roma"],
        "correct": "Paris"
      },
      {
        "question": "Qual cidade é a capital do Japão?",
        "options": ["Seul", "Pequim", "Tóquio", "Bangkok"],
        "correct": "Tóquio"
      },
      {
        "question": "Canberra é a capital de qual país?",
        "options": ["Austrália", "Canadá", "Nova Zelândia", "África do Sul"],
        "correct": "Austrália"
      },
      {
        "question": "Qual é a capital do Brasil?",
        "options": ["Buenos Aires", "Rio de Janeiro", "Brasília", "São Paulo"],
        "correct": "Brasília"
      },
      {
        "question": "Qual cidade europeia é conhecida como a 'Cidade Eterna'?",
        "options": ["Atenas", "Roma", "Viena", "Barcelona"],
        "correct": "Roma"
      }
    ]
  },
  {
    "id": "3",
    "title": "Culinária Brasileira",
    "subtitle": "Sabores do Brasil",
    "questionList": [
      {
        "question": "Qual é o ingrediente principal do acarajé?",
        "options": ["Feijão-fradinho", "Grão-de-bico", "Mandioca", "Milho"],
        "correct": "Feijão-fradinho"
      },
      {
        "question": "De qual estado brasileiro é originário o Pão de Queijo?",
        "options": ["Goiás", "São Paulo", "Minas Gerais", "Bahia"],
        "correct": "Minas Gerais"
      },
      {
        "question": "O Tucupi é um caldo extraído de qual raiz?",
        "options": ["Inhame", "Mandioca brava", "Batata doce", "Gengibre"],
        "correct": "Mandioca brava"
      },
      {
        "question": "Qual fruta é a base do doce 'Cartola', típico de Pernambuco?",
        "options": ["Manga", "Goiaba", "Banana", "Caju"],
        "correct": "Banana"
      },
      {
        "question": "Qual prato é conhecido como o 'sucesso' das feiras de São Paulo?",
        "options": ["Coxinha", "Pastel com caldo de cana", "Pão com linguiça", "Acarajé"],
        "correct": "Pastel com caldo de cana"
      }
    ]
  },
  {
    "id": "4",
    "title": "Cultura Geek",
    "subtitle": "Filmes e Séries",
    "questionList": [
      {
        "question": "Quem é o diretor da trilogia original de Star Wars?",
        "options": ["Steven Spielberg", "George Lucas", "James Cameron", "Ridley Scott"],
        "correct": "George Lucas"
      },
      {
        "question": "Qual é o metal fictício que compõe o escudo do Capitão América?",
        "options": ["Adamantium", "Vibranium", "Mithril", "Kryptonita"],
        "correct": "Vibranium"
      },
      {
        "question": "Em 'The Office', qual é o nome da empresa de papel?",
        "options": ["Wernham Hogg", "Dunder Mifflin", "Initech", "Pied Piper"],
        "correct": "Dunder Mifflin"
      },
      {
        "question": "Qual é o nome da inteligência artificial do Tony Stark (Homem de Ferro)?",
        "options": ["HAL 9000", "J.A.R.V.I.S.", "Cortana", "Skynet"],
        "correct": "J.A.R.V.I.S."
      },
      {
        "question": "No universo de Harry Potter, qual casa de Hogwarts tem um leão como símbolo?",
        "options": ["Sonserina", "Lufa-Lufa", "Corvinal", "Grifinória"],
        "correct": "Grifinória"
      }
    ]
  },
  {
    "id": "5",
    "title": "Geografia",
    "subtitle": "Explorando o Planeta",
    "questionList": [
      {
        "question": "Qual é o maior país do mundo em extensão territorial?",
        "options": ["Canadá", "China", "Rússia", "Estados Unidos"],
        "correct": "Rússia"
      },
      {
        "question": "Em qual continente fica localizado o Iêmen?",
        "options": ["Ásia", "África", "Europa", "Oceania"],
        "correct": "Ásia"
      },
      {
        "question": "Onde fica localizado o deserto do Atacama?",
        "options": ["América do Sul", "África", "América do Norte", "Ásia"],
        "correct": "América do Sul"
      },
      {
        "question": "Qual desses países NÃO faz fronteira com o Brasil?",
        "options": ["Chile", "Argentina", "Uruguai", "Colômbia"],
        "correct": "Chile"
      },
      {
        "question": "O Monte Everest, ponto mais alto da Terra, fica em qual cordilheira?",
        "options": ["Andes", "Alpes", "Himalaia", "Rocosas"],
        "correct": "Himalaia"
      }
    ]
  },
  {
    "id": "6",
    "title": "Esportes",
    "subtitle": "Mundo Olímpico e Futebol",
    "questionList": [
      {
        "question": "Qual país venceu a Copa do Mundo de 2022?",
        "options": ["França", "Brasil", "Argentina", "Alemanha"],
        "correct": "Argentina"
      },
      {
        "question": "Quantos jogadores de cada lado entram em campo em uma partida de vôlei?",
        "options": ["5", "6", "7", "11"],
        "correct": "6"
      },
      {
        "question": "Quem é o maior medalhista olímpico de todos os tempos?",
        "options": ["Usain Bolt", "Michael Phelps", "Simone Biles", "Nadia Comăneci"],
        "correct": "Michael Phelps"
      },
      {
        "question": "Em qual cidade serão realizados os Jogos Olímpicos de 2024?",
        "options": ["Tóquio", "Los Angeles", "Paris", "Londres"],
        "correct": "Paris"
      },
      {
        "question": "Qual é a distância aproximada de uma maratona?",
        "options": ["10km", "21km", "42km", "100km"],
        "correct": "42km"
      }
    ]
  }
]
 */