package br.com.javaparaweb.financeiro.categoria;

import java.util.List;

import br.com.javaparaweb.financeiro.usuario.Usuario;


//os nomes dos métodos são iguais ao de usuario e Conta,isso facilita a manuntenção do sistema e o entendimento da arquitetura.

public interface CategoriaDAO {
	public Categoria salvar(Categoria categoria);

	public void excluir(Categoria categoria);

	public Categoria carregar(Integer categoria);

	public List<Categoria> listar(Usuario usuario);
}
