# Sobre o projeto

Esse projeto é baseado nos meus estudos sobre JDBC. Irei explicar o processo de criar um banco de dados e usar o JDBC para persistir esses dados de maneira limpa e com segurança.
O projeto trata-se de armazenar posts e comentários de uma rede social.

# Dependências:

- JDBC -> mysql-connector-java-8.0.17.jar;
- POOL; DataSource: c3p0-0.9.5.4.jar; mchange-commons-java-0.2.16.jar

# Passos a seguir:

- [x] Criar um banco de dados;
- [x] Criar as tabelas do banco de dados;
- [x] Criar a conexão com o banco de dados (Connection factory);
- [x] Criar as classes que fazem a interligação com os atributos necessários para o banco de dados;
- [x] Criar a DAO de cada tabela;
- [x] Criar uma main que, intânciando uma DAO, rode os querys do banco de dados.

**Primeiro vamos criar o banco de dados**

```sql
create database socialNetwork;
```

**Agora vamos criar as tabelas:**

```sql
CREATE TABLE Posts (id INT AUTO_INCREMENT, name VARCHAR (50) NOT NULL, description VARCHAR(200) NOT NULL, PRIMARY KEY(id)) Engine = InnoDB;
```

```sql
CREATE TABLE Comments (id INT AUTO_INCREMENT, comment VARCHAR(200) NOT NULL, post_id INT NOT NULL, PRIMARY KEY(id)) Engine = InnoDB;

ALTER TABLE Comments ADD FOREIGN KEY (post_id) REFERENCES Posts (id);

```

**Agora podemos ir para o código!**

Teremos uma breve explicação sobre os recursos que iremos utilizar:

### JDBC

- Para acessar o banco de dados, precisamos de um driver(um drive nada mais é que uma biblioteca JAR);

- JDBC: significa JAVA DATABASE CONECTIVITY; Ele define uma camada de abstração entre a sua aplicação e o drive do banco de dados; Essa camada possui, na sua grande maioria, interfaces que o driver implementa.

- Para abrir uma conexão devemos usar o método getConnection, da classe DriverManager;

- O método getConnection recebe uma string de conexão JDBC, que define a URL, usuário e senha.

### Padrão factory

De forma geral todos os padrões factory(Simple Factory, Factory Method, Abstract Factory) encapsulam a criação de um objeto.

Por isso, usamos esse padrão para criar a conexão do nosso banco de daddos, isso porque precisamos encapsular seus dados, além disso, caso um deles forem mudados, mudaremos apenas em um lugar.

A classe ConnectionFactory segue o padrão de criação Factory Method(encapsula a criação de objetos, no entanto, a diferença é que neste padrão encapsula-se a criação de objetos deixando as subclasses decidirem quais objetos criar).

### Statement

Usamos o statement para poder realizar querys no banco de dados. Ele nos devolve um boolean(true ou false) e por isso precisamos de outro objeto para recuperar os valores que vem do nosso database.

O método execute do Statement envia o comando para o banco de dados.

### ResultSet

O ResultSet vai nos permitir receber os valores que vem do banco de dados e assim poderemos manipulá-los.

### PrepareStatement

O PrepareStatement serve para trazer uma segurança a mais ao nosso código, evitando assim um possível sqlInjection.

### Commit e Rollback

- O banco de dados oferece um recurso chamado de transação, para juntar várias alterações como unidade de trabalho;

- Se uma alteração falha, nenhuma alteração é aplicada (é feito um rollback da transação);

- Todas as alterações precisam funcionar para serem aceitas (é feito um commit);

- commit e rollback são operações clássicas de transações
Para garantir o fechamento dos recursos, existe no Java uma cláusula try-with-resources;

- O recurso em questão deve usar a interface Autoclosable.

### Pool e datasource

O pool de conexões define o tanto de conexão que terá nossa aplicação.

Com o pool de conexões podemos abrir conexões e reaproveitá-las.

- É boa prática usar um pool de conexões;

- Um pool de conexões administra/controla a quantidade de conexões abertas. Normalmente tem um mínimo e máximo de conexões;

- Como existe uma interface que representa a conexão (java.sql.Connection), também existe uma interface que representa o pool de conexões (javax.sql.DataSource);

- C3PO é uma implementação Java de um pool de conexão.

### DAO (Data Acess Object)

É um padrão de projeto que armazena tudo que é sobre acesso a objeto (banco de dados; algo externo da aplicação).

# Connection Factory

Iremos criar uma connection factory para podermos ter uma conexão com nosso banco de dados.

**É uma boa prática colocá-la em um package específico para ela.**

```java
package com.br.socialnetwork.connectionfactory;

// Importações necessárias
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class ConnectionFactory {
	// Atributo DataSouce para futuramente pegarmos uma conexão.
	private DataSource dataSource;
	
	// Construtor que será iniciado toda vez que chamarmos essa classe. Ele tem as configurações necessárias para termos uma conexão com nosso 			banco de dados.
	public ConnectionFactory() {
		ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
		comboPooledDataSource.setJdbcUrl("jdbc:mysql://localhost/socialNetwork?useTimezone=true&serverTimezone=UTC");
		comboPooledDataSource.setUser("root");
		comboPooledDataSource.setPassword("yourPassword");
		
		this.dataSource = comboPooledDataSource;
	}
	
	// Método que retornará nossa conexão.
	public Connection retriveConnection() throws SQLException {
		return this.dataSource.getConnection();
	}
}
```
# Classe Posts

```java
package com.br.socialnetwork.classes;

public class Posts {
	// Atributos necessários.
	private Integer id;
	private String name;
	private String description;
	
	// 1º construtor que recebe o name e a description para quando formos inserir um post.
	public Posts(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	// 2º construtor que recebe id, name, description para quando formos listar.
	public Posts(Integer id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	// Para mostrarmos os atributos dessa classe.
	public String toString() {
		return String.format("Posts it's: %d, %s, %s", 
				this.id, this.name, this.description);
	}
}
```

# DAO

```java
package com.br.socialnetwork.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.br.socialnetwork.classes.Posts;

public class PostsDAO {
	// Declarando um atributo connection.
	private Connection connection;
	
	// Recebendo a connection pelo construtor.
	public PostsDAO(Connection connection) {
		this.connection = connection;
	}
	
	public void save(Posts posts) throws SQLException {
		// Query do banco de dados
		String sql = "INSERT INTO Posts (name, description) VALUES (?, ?)";
		
		// Usando o PreparedStatement para tratar as queries do banco de dados.	
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) 			{
			// Setando nos parâmetros da query.
			preparedStatement.setString(1, posts.getName());
			preparedStatement.setString(2, posts.getDescription());
			
			// Executando a query
			preparedStatement.execute();
			
			// Devolvendo o id que será retornado na criação de um post.
			try (ResultSet resultSet = preparedStatement.getGeneratedKeys()){
				while(resultSet.next()) {
					posts.setId(resultSet.getInt(1));
				}
			}
		}
	}
}
```

# Main

```java
package com.br.socialnetwork.main;

import java.sql.Connection;
import java.sql.SQLException;

import com.br.socialnetwork.DAO.PostsDAO;
import com.br.socialnetwork.classes.Posts;
import com.br.socialnetwork.connectionfactory.ConnectionFactory;

public class InsertPosts {

	public static void main(String[] args) throws SQLException {
		// Enviando via construtor os dados que será usado na criação de um post (name, description)
		Posts posts = new Posts("Aliens", "Evidência de vida fora da terra");
		
		// Criando uma conexão
		try (Connection connection = new ConnectionFactory().retriveConnection()){
			// Passando a conexão para o PostsDAO
			PostsDAO postDAO = new PostsDAO(connection);
			// Salvado um post
			postDAO.save(posts);
		}
		
	}

}
```
# Listando com JOIN

### Classes

```java
package com.br.socialnetwork.classes;

import java.util.ArrayList;
import java.util.List;

public class Comments {
	//Declaração de atributos necessários. 
	private Integer id;
	private String comment;
	private Integer post_id;
	// Para listarmos os posts que irá vim na query
	private List<Posts> posts = new ArrayList<Posts>();
	
	// 1° construtor para guardarmos um comentário.
	public Comments(String comment,  Integer post_id) {
		this.comment = comment;
		this.post_id = post_id;
	}
	
	// 2º construtor para listarmos um comentário.
	public Comments(Integer id, String comment,  Integer post_id) {
		this.id = id;
		this.comment = comment;
		this.post_id = post_id;
	}
	
	public String getComment() {
		return comment;
	}
	
	public Integer getPost_id() {
		return post_id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	// Para adicionarmos os posts que virão na query
	public void add(Posts post) {
		posts.add(post);
	}
	
	// Para listarmos os posts
	public List<Posts> getPosts() {
		return posts;
	}
	
	public String toString() {
		return String.format("Comments its: %d, %s, %d", this.id, this.comment, this.post_id);
	}
}

```

### DAO

```java
public List<Comments> list() throws SQLException {
		// Par listarmos os comentários e os posts
		List<Comments> commentsWithPosts = new ArrayList<Comments>();
		// Para não repetir posts
		Comments last = null;
		// query
		String sql = "SELECT c.comment, c.post_id, p.id, p.name, p.description FROM Comments c INNER JOIN"+" Posts p 			ON c.post_id = p.id"; 
		
		// Tratamento da query
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
			// Executando a query
			preparedStatement.execute();
			
			try (ResultSet resultSet = preparedStatement.getResultSet()){
				while(resultSet.next()) {
					// Buscando por comentários que pertencem a um post sem repetílo
					if(last == null || !last.getComment().equals(resultSet.getString(2))) {
						Comments comments = new Comments(resultSet.getString(1), resultSet.getInt(2));
						// Recebendo os comments com seus métodos.
						last = comments;
						// Adicionando os comments a lista.
						commentsWithPosts.add(comments);
					}
					// Adicionando os posts ao método add.
					Posts posts = new Posts(resultSet.getInt(3), resultSet.getString(4), resultSet.getString(5));
					last.add(posts);
				}
			}
		}
		return commentsWithPosts;
	}
```

### Main

```java
package com.br.socialnetwork.main;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.br.socialnetwork.DAO.CommentsDAO;
import com.br.socialnetwork.classes.Comments;
import com.br.socialnetwork.classes.Posts;
import com.br.socialnetwork.connectionfactory.ConnectionFactory;

public class ListCommentsAndPosts {

	public static void main(String[] args) throws SQLException {
		try (Connection connection = new ConnectionFactory().retriveConnection()) {
			// Passando a connection para o construtor.
			CommentsDAO commentsDAO = new CommentsDAO(connection);
			// Recebendo a lista
			List<Comments> list = commentsDAO.list();
			
			// Percorrendo a lista.
			list.stream().forEach(comment -> {
				 System.out.println(comment.getComment() + " " + comment.getPost_id());
				 
				 //Percorrendo os posts de um comentário.
				 for(Posts posts : comment.getPosts()) {
					System.out.println(posts.getName() + " " + posts.getDescription());
				 }
			});
		}

	}

}
```
