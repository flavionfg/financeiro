package br.com.javaparaweb.financeiro.categoria;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import br.com.javaparaweb.financeiro.usuario.Usuario;

@Entity
public class Categoria implements Serializable {

	@Id
	@GeneratedValue
	private Integer codigo;

	@ManyToOne
	@JoinColumn(name = "categoria_pai", nullable = true, foreignKey = @ForeignKey(name = "fk_categoria_categoria")) // 1*
	private Categoria pai;

	@ManyToOne
	@JoinColumn(name = "usuario", foreignKey = @ForeignKey(name = "fk_categoria_usuario"))
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Usuario usuario;

	private String descricao;

	private int fator;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE) // 2 *
	@JoinColumn(name = "categoria_pai", updatable = false) // 3 *
	@org.hibernate.annotations.OrderBy(clause = "descricao asc")
	private List<Categoria> filhos;

	public Categoria() { // 4*
	}

	public Categoria(Categoria pai, Usuario usuario, String descricao, int fator) {
		this.pai = pai;
		this.usuario = usuario;
		this.descricao = descricao;
		this.fator = fator;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
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
		Categoria other = (Categoria) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		return true;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public Categoria getPai() {
		return pai;
	}

	public void setPai(Categoria pai) {
		this.pai = pai;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getFator() {
		return fator;
	}

	public void setFator(int fator) {
		this.fator = fator;
	}

	public List<Categoria> getFilhos() {
		return filhos;
	}

	public void setFilhos(List<Categoria> filhos) {
		this.filhos = filhos;
	}

}


/*
 *  1 - @joinCollum - atributo name refere a coluna na tabela, e não ao nome da propriedade na classe.
 *                  - atributo nullable esta como true, pois obrigatóriamente o primeiro nivel de categorias de um usuario(DESPESA e RECEITA)
 *  não tera uma categoria pai,por isso, esse campo deve aceitar nulos.
 *  
 *  Um relacionamento muitos-parra-um vai gerar uma chave estrangeira no banco quando for criada,por padrao o Hibernate vai criar um nome
 *  aleatório, podemos usar a annotation @ForeignKey como parametro do @JoinCollumn para criar um nome mais amigavel.
 *  
 *  2 - Além de referenciar a caregoria pai, uma instância de Categoria também deverá referenciar seus próprios filhos utilizaremos um
 *  mapeamento @oneToMany para fazer essa carga da lista das categorias filhas,queremos fazer uma carga de filhos de uma categoria no 
 *  momento da consulta,por isso,utilizamos o fech igual a FetchType.EAGER em outra situação poderamos utilizar o FetchType.LAZY que
 *  carrega os filhos no momento que foram utilizados.
 *  
 *  o atributo cascade = CascadeType.REMOVE,com essa configuração excluirá os filhos e netos caso a categoria seja excluida, mas para isso
 *  acontecer é necessario que os objetos filhos estejam carregados em memória.
 *  
 *  3 - Estaremos carregando todas as categorias cujo campo categoria_pai (name) seja igual ao código da categoria atual, ou seja, os filhos 
 *  dessa categoria,o atributo updatable = false, garante que atualização (update) em uma categoria não afete seus filhos.Caso updatable 
 *  fosse true e desejássemos salvar uma categoria mudando apenas o nome,por exemplo, sem que seja necessario carregar todosos seus filhos.
 *  Fizemos essa escolha para permitir que seja possivel salvar uma categoria mudando apenas o seu nome, por exemplo, sem que seja necessario
 *  carregar todos os seus filhos,caso updatable fosse true e desejassemos salvar uma categorai sem seus filhos(embora estes existissem no
 *  banco de dados),o Hibernate excluiria todos eles,pois entenderia que a categorai sendo salva não tem mais filhos.
 *  
 *  4 - Criamos um construtor sem parametros,isso é necesssario por que o Hibernate exige um construtor vazio nas classes das entidades
 *  em que trabalha. 
 *  
 *  fetch = buscar.
 *  
 */


