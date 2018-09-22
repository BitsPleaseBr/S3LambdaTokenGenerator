package com.amazonaws.lambda.token.generator;

import org.mindrot.jbcrypt.BCrypt;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LambdaFunctionHandler implements RequestHandler<Input, Output> {

  @Override
  public Output handleRequest(Input input, Context context) {
    context.getLogger().log("Input: email: " + input.getEmail() + ", senha: " + input.getSenha());

    // Coleta os dados do input e do usuário
    String key = System.getenv("secretKey");
    String email = input.getEmail();
    String pswd = input.getSenha();
    DBHandler handler = new DBHandler(email);
    String hash = handler.getHash();

    // Confere a senha
    boolean autorizado = false;
    try {
      autorizado = BCrypt.checkpw(pswd, hash);
    } catch (IllegalArgumentException e) {
      if (hash != " ") {
        context.getLogger().log("Erro na conferência do hash. Hash salvo: " + hash);
      } else {
        context.getLogger()
            .log("Erro de conferência de hash devido a hash vazio. Usuário não encontrado.");
      }
    }

    // Gera um número como salt do hash
    String salt = BCrypt.gensalt();

    // Gera o token a partir de um hash do hash da senha + a key armazenada na lambda
    String token = BCrypt.hashpw(hash + key, salt);

    // Adiciona o id do usuário no token
    token = handler.getId() + ":" + token;

    Output output = new Output();
    if (autorizado) {
      output.setStatus("200");
      output.setToken(token);
    } else {
      throw new RuntimeException("401");
    }
    return output;
  }

}
