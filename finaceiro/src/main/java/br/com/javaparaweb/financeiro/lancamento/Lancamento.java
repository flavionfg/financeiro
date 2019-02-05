package br.com.javaparaweb.financeiro.lancamento;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import br.com.javaparaweb.financeiro.categoria.Categoria;
import br.com.javaparaweb.financeiro.cheque.Cheque;
import br.com.javaparaweb.financeiro.conta.Conta;
import br.com.javaparaweb.financeiro.usuario.Usuario;

@Entity
@Table(name = "lancamento")
public class Lancamento implements Serializable {

	@Id
	@GeneratedValue
	@Column(name = "codigo")
	private Integer lancamento;

	@ManyToOne(fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "usuario", nullable = false, foreignKey = @ForeignKey(name = "fk_lancamento_usuario"))
	private Usuario usuario;

	@ManyToOne(fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "conta", nullable = false, foreignKey = @ForeignKey(name = "fk_lancamento_conta"))
	private Conta conta;

	@ManyToOne(fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "categoria", nullable = false, foreignKey = @ForeignKey(name = "fk_lancamento_categoria"))
	private Categoria categoria;
	@Temporal(TemporalType.DATE) //1*
	private Date data;
	private String descricao;
	@Column(precision = 10, scale = 2)  //2*
	private BigDecimal valor;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "lancamento")
	private Cheque cheque;

	public Integer getLancamento() {
		return lancamento;
	}

	public void setLancamento(Integer lancamento) {
		this.lancamento = lancamento;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((categoria == null) ? 0 : categoria.hashCode());
		result = prime * result + ((cheque == null) ? 0 : cheque.hashCode());
		result = prime * result + ((conta == null) ? 0 : conta.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((descricao == null) ? 0 : descricao.hashCode());
		result = prime * result + ((lancamento == null) ? 0 : lancamento.hashCode());
		result = prime * result + ((usuario == null) ? 0 : usuario.hashCode());
		result = prime * result + ((valor == null) ? 0 : valor.hashCode());
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
		Lancamento other = (Lancamento) obj;
		if (categoria == null) {
			if (other.categoria != null)
				return false;
		} else if (!categoria.equals(other.categoria))
			return false;
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
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (descricao == null) {
			if (other.descricao != null)
				return false;
		} else if (!descricao.equals(other.descricao))
			return false;
		if (lancamento == null) {
			if (other.lancamento != null)
				return false;
		} else if (!lancamento.equals(other.lancamento))
			return false;
		if (usuario == null) {
			if (other.usuario != null)
				return false;
		} else if (!usuario.equals(other.usuario))
			return false;
		if (valor == null) {
			if (other.valor != null)
				return false;
		} else if (!valor.equals(other.valor))
			return false;
		return true;
	}

	public Cheque getCheque() {
		return cheque;
	}

	public void setCheque(Cheque cheque) {
		this.cheque = cheque;
	}
}


/* 1 - Por padrão no Hibernate,sempre que uma propriedade de classe é definida como java.util.date, ela será gerada como datetime no
 * banco de dados,no nosso caso essa informação vai ser dispensada por meio da annotation @temporal. Essa define a precisão de um
 * campo data,sendo que podemos definir os valores seguintes para esse campo.
 * 
 *  TemporalType.DATE - Armazena somente a data no banco de dados.
 *  TemporalType.TIME - Armazena somente a hora no banco de dados.
 *  TemporalType.TIMESTAMP - Armazena data e horas no banco de dados.
 * 
 * 2 - BigDecimal é recomendado para o hibernate quando se deseja armazenar valores momentários,pois ele permite a definição
 * da precisão desse campo.A precisão é definida pela annotation @Column(precision = 10, scale = 2), sendo que ela indica que o campo
 * terá dez digitos, sendo dois decimais.Nesse caso,o tipo gerado no banco de dados será decimal(10,2).
 * 
 */ 
