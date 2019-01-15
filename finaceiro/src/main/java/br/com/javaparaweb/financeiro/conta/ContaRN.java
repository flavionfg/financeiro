package br.com.javaparaweb.financeiro.conta;

import java.util.Date;
import java.util.List;

import br.com.javaparaweb.financeiro.usuario.Usuario;
import br.com.javaparaweb.financeiro.util.DAOFactory;

public class ContaRN {
	private ContaDAO contaDAO; //Declarando uma propriedade com o tipo de DAO utilizada na classe.

	public ContaRN() {
		this.contaDAO = DAOFactory.criarContaDAO(); //Construtor,obtendo inst�ncia desse DAO na classe DAOFactory
		
		/*
		 * como vimos acima,dessa forma a propriedade contaDAO estar� disponivel a todos os m�todos da classe para realizar as 
		 * opera��es de banco de dados necessarias para cumprir as regras de negocio.
		 */
	}

	public List<Conta> listar(Usuario usuario) {
		return this.contaDAO.listar(usuario);
	}

	public Conta carregar(Integer conta) {
		return this.contaDAO.carregar(conta);
	}

	public void salvar(Conta conta) { //a data atual � definida como data de cadastro, esta pequena opera��o pode ser considaderada uma RN.
		conta.setDataCadastro(new Date());
		this.contaDAO.salvar(conta);
	}

	public void excluir(Conta conta) {
		this.contaDAO.excluir(conta);
	}

	//registra uma conta favorita do usuario
	public void tornarFavorita(Conta contaFavorita) {
		Conta conta = this.buscarFavorita(contaFavorita.getUsuario());
		if (conta != null) {
			conta.setFavorita(false);
			this.contaDAO.salvar(conta);
		}
		contaFavorita.setFavorita(true);
		this.contaDAO.salvar(contaFavorita);
		
		/*
		 * primeiro obtivemos a conta favorita atual do usuario para salvar como false,depois marcamos a conta recebida no par�metro
		 * como true e tamb�m salvamos.
		 * 
		 * A principio a conta ativa ser� a favorita.
		 */
	}

	public Conta buscarFavorita(Usuario usuario) {
		return this.contaDAO.buscarFavorita(usuario);
	}
}
