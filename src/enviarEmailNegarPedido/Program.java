package enviarEmailNegarPedido;

import java.math.BigDecimal;
import java.sql.ResultSet;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.ModifingFields;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.ws.ServiceContext;

public class Program implements EventoProgramavelJava {

	BigDecimal nuNota;
	BigDecimal codSolic;
	BigDecimal seq;
	
	String obsLib;
	String email;
	String nomeUsu;
	String nomeUsulog;

	@Override
	public void afterDelete(PersistenceEvent event) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterInsert(PersistenceEvent event) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterUpdate(PersistenceEvent event) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeCommit(TransactionContext event) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeDelete(PersistenceEvent event) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeInsert(PersistenceEvent event) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("inicio codigo depois update");

		JdbcWrapper JDBC = JapeFactory.getEntityFacade().getJdbcWrapper();
		NativeSql nativeSql = new NativeSql(JDBC);
		JapeSession.SessionHandle hnd = JapeSession.open();
		
		EnviaEmail envemail = new EnviaEmail();
		Historico his = new Historico();

		ModifingFields mofFields = event.getModifingFields();
		// instanciando o modifi

		DynamicVO newVO = (DynamicVO) event.getVo();
		DynamicVO oldVO = (DynamicVO) event.getOldVO();

		if (mofFields.isModifing("REPROVADO")) {
			System.out.println("PAssou no if do modifing");
			
			if (newVO.asString("REPROVADO").equals("S")) {

				System.out.println("Entrou no If do reprovado");
				
				System.out.println("Reprovado :" + newVO.asString("REPROVADO"));

				BigDecimal usuarioLogado = ((AuthenticationInfo) ServiceContext.getCurrent().getAutentication())
						.getUserID();
				// Aqui e para capturar usuario logado

				System.out.println("Usuario logado" + usuarioLogado);
				
				ResultSet codG = nativeSql.executeQuery("SELECT NOMEUSU FROM TSIUSU WHERE CODUSU = " + usuarioLogado);

				while (codG.next()) {
					nomeUsulog = codG.getString("NOMEUSU");
				}

				nuNota = oldVO.asBigDecimal("NUCHAVE");
				System.out.println("nuChave" + nuNota);
				
				ResultSet rssolic = nativeSql.executeQuery("SELECT CODUSUSOLICIT FROM TSILIB WHERE NUCHAVE ="+oldVO.asBigDecimal("NUCHAVE"));
				
				while (rssolic.next()) {
					codSolic = rssolic.getBigDecimal("CODUSUSOLICIT");
					System.out.println("codSolicitante " + codSolic);
				}
				
				obsLib = oldVO.asString("OBSLIB");
				
				System.out.println(obsLib);
				
				ResultSet rsEmail = nativeSql.executeQuery("SELECT EMAIL, NOMEUSU FROM TSIUSU WHERE CODUSU = "+codSolic);
				
				while (rsEmail.next()) {
					email = rsEmail.getString("EMAIL");
					nomeUsu = rsEmail.getString("NOMEUSU");
					System.out.println("Email " + email);
				}
								
				envemail.enviarEmail(email, nomeUsu, nuNota, obsLib);
				his.insereHistorico(nomeUsulog, nuNota);
			}
		}
		JapeSession.close(hnd);
	}
}
