package br.com.javaparaweb.financeiro.cheque;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable //1*
public class ChequeId implements Serializable {

	private static final long serialVersionUID = -2421987206248460393L;

	@Basic(optional = false) //2*
	@Column(name = "cheque", nullable = false)
	private Integer cheque;

	@Basic(optional = false)
	@Column(name = "conta", nullable = false)
	private Integer conta;

	public ChequeId(Integer conta, Integer cheque) { //3*
		this.conta = conta;
		this.cheque = cheque;
	}

	public ChequeId() { //4*
	}

	public Integer getCheque() {
		return cheque;
	}

	public void setCheque(Integer cheque) {
		this.cheque = cheque;
	}

	public Integer getConta() {
		return conta;
	}

	public void setConta(Integer conta) {
		this.conta = conta;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cheque == null) ? 0 : cheque.hashCode());
		result = prime * result + ((conta == null) ? 0 : conta.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChequeId other = (ChequeId) obj;
		if (cheque == null) {
			if (other.cheque != null)
				return false;
		} else if (!cheque.equals(other.cheque))
			return false;
		if (conta == null) {
			if (other.conta != null)
				return false;
		} else if (!conta.equals(other.conta))
			return false;
		return true;
	}

}

/*
 *  A tabela cheque � composta de uma chave primaria constituida de dois campos, um que representa o n�mero do cheque e outro
 *  que indica a qual conta este esta vinculado.
 *  
 *  Para trasnportar a caracteristica da tabela de ter uma chave prim�ria composta,no modelo orientado a objetos,ser� preciso
 *  criar a classe Chaqueid,que nada mais � que uma classe com duas propriedades que representam os campos que comp�em a chave
 *  prim�ria da tabela.
 *  
 *  
 *      *Usamos chaves prim�rias compostas quando precisamos de dois campos para compor uma chave prim�ria. 
 * 
 * 
 *  1 - indica,nesse caso, que a classe ChequeId � um objeto que n�o precisa de um identificador pr�prio. Isso se d� porque 
 *  objetos dessa natureza s�o identificados pelos objetos das entidades nos quais est�o contidos.
 *  
 *  2 - Usamos uma tag que poderia ser opcional no @Basic. A tag indica que essa propriedade � obrigat�ria. O par�metro 
 *  optional indica que nesse caso,que vamos gerar um validador no campo da tabela do tipo NOT NULL.
 *  
 *  3 - O construtor � um facilitador para os v�rios pontos do sistema em que vamos precisar criar uma inst�ncia do ChequeId.
 *  
 *  4 - O construtor vazio � uma exig�ncia do Hibernate por termos criado um construtor que recebe par�metros.Isso porque, 
 *  por padr�o,toda classe tem um construtor vazio(mesmo que n�o exista fisicamente no arquivo), por�m quando criamos um
 *  construtor com par�metros, o vazio deixa de existir por padr�o e tem que ser criado manualmente.
 * 
 */