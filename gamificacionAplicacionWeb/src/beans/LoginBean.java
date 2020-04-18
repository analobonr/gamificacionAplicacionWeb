package beans;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import constantes.Mensajes;
import constantes.URLs;
import pojos.Profesor;

import constantes.valoresDesplegables;
import constantes.valoresDesplegables.rol_t;


//Bean que gestiona el login a la aplicacion
@ManagedBean
public class LoginBean implements Serializable{

	
	private static final long serialVersionUID = 2057658592045182866L;

	
	//Propiedades para recoger los datos del formulario
	private String email;
	private String password;
	
	
	
	//Propiedad que recoge los datos del profesor logeado
	private Profesor p;
	private String rolUsuario = "";
	
	
	
	/**Metodo que procesa el login**/
	public void enviarDatos() {
		
		String redir = null;
		
		
		//Comprobamos que se ha introducido mail y password
		if ((this.email != "") && (this.password != "")) {
			
			//Peticion Rest para obtener el usuario
			String url = constantes.URLs.USUARIO + this.getEmail() + "/" + this.getPassword();
			System.out.println("Petición de login: "+url);
			try {
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<Profesor> response = restTemplate.exchange(
					url ,HttpMethod.GET, null, new ParameterizedTypeReference<Profesor>(){});
			
			//Obtenemos la respuesta
			p = response.getBody();
			
			if (p.getRol() != rol_t.USER) {
				this.setRolUsuario(valoresDesplegables.rolesMap.get(p.getRol()));
			}else {
				this.setRolUsuario("");
			}
			
	    	if((!response.getStatusCode().is2xxSuccessful()) || (p == null) || (p.getEmail().length() == 0)) {
				 FacesContext context = FacesContext.getCurrentInstance();
			     context.addMessage(null, new FacesMessage(Mensajes.HEADERUPS,  Mensajes.ERRORACCESO) );
			 }else {
				 //Creamos la sesion del usuario
				 HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
				 HttpSession userSession = request.getSession(true);
				 userSession.setAttribute("profesor", p);
				 
				 FacesContext contex = FacesContext.getCurrentInstance();
		         try {
					contex.getExternalContext().redirect(URLs.URLIndex);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
			}catch(HttpServerErrorException httpe) {
				httpe.printStackTrace();
				FacesContext context = FacesContext.getCurrentInstance();
			    context.addMessage(null, new FacesMessage(Mensajes.HEADERUPS,  Mensajes.ERRORACCESO) );
			}
		}
       
    }
	
	
	/** Metodo que genera una nueva password e informa por mail* */
	public void enviarEmail() {
		
		
		if (email != "") {
			
			//Peticion para obtener una nueva password para el usuario
			String url = constantes.URLs.USUARIO + this.email;
			System.out.println("Petición nueva pass: "+url);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<Profesor> response = restTemplate.exchange(
					url ,HttpMethod.GET, null, new ParameterizedTypeReference<Profesor>(){});
			
			
			//Obtenemos la respuesta
	    	if(!response.getStatusCode().is2xxSuccessful()) {
				 System.out.println("La petición ha fallado");
				 FacesContext context = FacesContext.getCurrentInstance();
			     context.addMessage(null, new FacesMessage(Mensajes.HEADERERROR,  Mensajes.ERRORNEWPASS));
				 
			//Enviamos el mail con la nueva password	 
			}else {
				p = response.getBody();
			
				//Configuramos las propiedades
				Properties properties = new Properties();
				properties.put("mail.smtp.host", "smtp.gmail.com");
				properties.put("mail.smtp.starttls.enable", "true");
				properties.put("mail.smtp.port","587");
				properties.put("mail.smtp.auth", "true");
		 
				//Preparamos la sesion
				Session session = Session.getDefaultInstance(properties);
				
				String correoRemitente = "adjamail52@gmail.com";
				String passwordRemitente = "ju3g0sd1d4ct1c0s";
				String correoReceptor = email;
				String asunto = Mensajes.ASUNTOMAIL;
				String mensaje =  Mensajes.MENSAJEMAIL.replace("[NEWPASS]", p.getPassword());
				
				try{
					//Construimos el mensaje
					MimeMessage message = new MimeMessage(session);
					message.setFrom(new InternetAddress(correoRemitente));
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(correoReceptor));
					message.setSubject(asunto);
					message.setText(mensaje);
					
					//Enviamos el correo
					Transport t = session.getTransport("smtp");
					t.connect(correoRemitente, passwordRemitente);
					t.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
					t.close();
					
					System.out.println("Correo enviado correctamente");
					FacesContext context = FacesContext.getCurrentInstance();
			        context.addMessage(null, new FacesMessage(Mensajes.HEADERNEWPASS,  Mensajes.NEWPASS.replace("[MAIL]", correoReceptor)));
			        
			        
				}catch (MessagingException me){
		                
					System.err.println("Error al enviar email: "+ me);
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage(null, new FacesMessage(Mensajes.HEADERERROR,  Mensajes.ERRORNEWPASS));
				}
			 }
		}else {
			FacesContext context = FacesContext.getCurrentInstance();
	        context.addMessage(null, new FacesMessage(Mensajes.HEADERERROR,  Mensajes.ERRORNEWPASS2));
		}
		
	}


	//Cierra la sesion del usuario
	public void cerrarSesion() {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		HttpSession userSession = request.getSession(true);
		userSession.invalidate();
	}
	
	/**GETTERS & SETTERS**/
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Profesor getP() {
		return p;
	}

	public void setP(Profesor p) {
		this.p = p;
	}


	public String getRolUsuario() {
		return rolUsuario;
	}


	public void setRolUsuario(String rolUsuario) {
		this.rolUsuario = rolUsuario;
	}
}
	
	
