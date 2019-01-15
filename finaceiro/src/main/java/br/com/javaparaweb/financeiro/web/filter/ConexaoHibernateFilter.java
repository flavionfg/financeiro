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
	
	/* Com essa Annotation @WebFilter � possivel configurar qual tipo de requisi��o web  esta classe Filter vai interceptar,queremos
	 * que sejam abertar conex�es sempre que for requisita uma pagina com a extens�o .jsf, vale tanto para uma URL quando ao envio 
	 * de um formulario preenchido ou acionamento de um bot�o.
	 */

	public void init(FilterConfig config) throws ServletException { //este m�todo s� � executado quando o aplicativo web � colocado no ar
		this.sf = HibernateUtil.getSessionFactory();//cria��o da classe SessionFactory que cria todas as sess�es do Hibernate, a cada requisi��o.
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) //requise��o web � intercepta aqui neste metodo.
			throws ServletException {

		Session currentSession = this.sf.getCurrentSession();

		Transaction transaction = null;

		try {
			transaction = currentSession.beginTransaction(); //inicia uma transa��o no banco
			chain.doFilter(servletRequest, servletResponse); //pega a requisi��o e passa a diante, para a execu��o normal.
			transaction.commit(); // conclui a transa��o do banco de dados.
			if (currentSession.isOpen()) {
				currentSession.close();
			}
		} catch (Throwable ex) {
			try {
				if (transaction.isActive()) {
					transaction.rollback(); // se tiver algum erro ele realiza um Rollback da transa��o.
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
			throw new ServletException(ex);
		}
	}

	public void destroy() { //� executado quando o aplicativo web � desativo do servidor(Tomcat, neste caso) � retirado do ar.
	}
}
