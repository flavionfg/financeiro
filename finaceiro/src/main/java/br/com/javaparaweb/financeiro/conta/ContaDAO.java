package br.com.javaparaweb.financeiro.conta;

import java.util.List;

import br.com.javaparaweb.financeiro.usuario.Usuario;

// Esta classe conta apenas com as assinaturas dos m�todos,segue as diretrizes do Design Pattern DAO

public interface ContaDAO {
	public void salvar(Conta conta);

	public void excluir(Conta conta);

	public Conta carregar(Integer conta);

	public List<Conta> listar(Usuario usuario);

	public Conta buscarFavorita(Usuario usuario);
	
	/* Como toda conta est� relacionada a um usu�rio,nessa interface criamos o m�todo listar recebendo um usuario como par�metro,ou 
	 * seja o m�todo listara as contas de um determinado usuario, assim como m�todo buscarFavorita, que retornar� a conta 
	 * favorita de um usuario.
	 */
}
