package com.amazonaws.lambda.token.generator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBHandler {
  private static Connection connection = ConnectionFactory.getConnection();
  private static String campoID = System.getenv("campoIDBD");
  private static String campoSenha = System.getenv("campoPswdBD");
  private static String campoEmail = System.getenv("campoEmailBD");
  private static String tabelaUserBD = System.getenv("tabelaUserBD");
  private static String campoTipo = System.getenv("campoTiporBD");
  private String hash = " ";
  private String id;
  private String email;
  private String tipo;

  public String getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getHash() {
    return hash;
  }

  public DBHandler(String email) {
    this.email = email;

    // Busca os dados no banco de dados
    String sqlQuery = String.format("SELECT %s FROM %s WHERE %s = '%s'",
        campoID + ", " + campoSenha + ", " + campoTipo, tabelaUserBD, campoEmail, email);
    System.out.println("String de query: , " + sqlQuery);
    try {
      PreparedStatement statement = connection.prepareStatement(sqlQuery);
      ResultSet results = statement.executeQuery();
      if (results.next()) {
        id = results.getString(campoID);
        hash = results.getString(campoSenha);
        tipo = results.getString(campoTipo);
      } else {
        System.out.println("email não encontrado no BD: " + email);
      }
    } catch (Exception e) {
      System.out.println("Erro ao accesar banco de dados para autenticar acesso ao API");
      e.printStackTrace();
    }
  }

  public String getTipo() {
    return tipo;
  }
}
