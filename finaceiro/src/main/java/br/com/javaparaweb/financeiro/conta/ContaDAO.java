package br.com.javaparaweb.financeiro.conta;

import java.util.List;

import br.com.javaparaweb.financeiro.usuario.Usuario;

// Esta classe conta apenas com as assinaturas dos métodos,segue as diretrizes do Design Pattern DAO

public interface ContaDAO {
	public void salvar(Conta conta);

	public void excluir(Conta conta);

	public Conta carregar(Integer conta);

	public List<Conta> listar(Usuario usuario);

	public Conta buscarFavorita(Usuario usuario);
	
	/* Como toda conta está relacionada a um usuário,nessa interface criamos o método listar recebendo um usuario como parâmetro,ou 
	 * seja o método listara as contas de um determinado usuario, assim como método buscarFavorita, que retornará a conta 
	 * favorita de um usuario.
	 */
}
