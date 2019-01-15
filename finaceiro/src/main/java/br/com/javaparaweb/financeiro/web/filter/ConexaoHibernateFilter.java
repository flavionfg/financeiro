package br.com.javaparaweb.financeiro.web.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import br.com.javaparaweb.financeiro.util.HibernateUtil;

@WebFilter(urlPatterns = { "*.jsf" })//  
public class ConexaoHibernateFilter implements Filter {
	private SessionFactory sf;
	
	/* Com essa Annotation @WebFilter é possivel configurar qual tipo de requisição web  esta classe Filter vai interceptar,queremos
	 * que sejam abertar conexões sempre que for requisita uma pagina com a extensão .jsf, vale tanto para uma URL quando ao envio 
	 * de um formulario preenchido ou acionamento de um botão.
	 */

	public void init(FilterConfig config) throws ServletException { //este método só é executado quando o aplicativo web é colocado no ar
		this.sf = HibernateUtil.getSessionFactory();//criação da classe SessionFactory que cria todas as sessões do Hibernate, a cada requisição.
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) //requiseção web é intercepta aqui neste metodo.
			throws ServletException {

		Session currentSession = this.sf.getCurrentSession();

		Transaction transaction = null;

		try {
			transaction = currentSession.beginTransaction(); //inicia uma transação no banco
			chain.doFilter(servletRequest, servletResponse); //pega a requisição e passa a diante, para a execução normal.
			transaction.commit(); // conclui a transação do banco de dados.
			if (currentSession.isOpen()) {
				currentSession.close();
			}
		} catch (Throwable ex) {
			try {
				if (transaction.isActive()) {
					transaction.rollback(); // se tiver algum erro ele realiza um Rollback da transação.
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
			throw new ServletException(ex);
		}
	}

	public void destroy() { //é executado quando o aplicativo web é desativo do servidor(Tomcat, neste caso) é retirado do ar.
	}
}
