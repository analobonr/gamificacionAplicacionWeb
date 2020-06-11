package beans;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import constantes.Mensajes;
import constantes.URLs;
import constantes.valoresDesplegables;
import constantes.valoresDesplegables.rol_t;
import pojos.Estado;
import pojos.Profesor;



/**
 * Bean para el manejo de la interfaz de gestion de usuarios
 * @author Ana Lobón
 * @version: 1.0 (03/05/2020)
 */
@ManagedBean
public class gestionUsuariosBean implements Serializable {
	
	private static final long serialVersionUID = 3885381981170292304L;
	
	//Listado completo de usuarios
	private List<Profesor> users;
	
	//Listado de usuarios filtrados
	private List<Profesor> filteredUsers;
	
	//Usuario seleccionado para edicion
	private Profesor selectedUser;
	
	//Listado de usuarios seleccionados para borrado
	private List<Profesor> selectedUsers;
	
	
	//Usuario loggeado
	private Profesor login;
	
	
	/**
	 * Método que se ejecuta automaticamente cuando carga la interfaz
	 */
	@PostConstruct
    public void init() {
		
		//Obtenemos la session del usuario
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		HttpSession userSession = request.getSession(true);
		login = (Profesor) userSession.getAttribute("profesor");
		
		ExternalContext eContext = FacesContext.getCurrentInstance().getExternalContext();

		//Si el usuario loggeado no tiene rol de superusuario
		if((login == null) || (login.getRol() != rol_t.SUPER)) {
			
			try{
				//Redireccionamos
	            eContext.redirect( eContext.getRequestContextPath() + URLs.pathlogin );
			}catch(  Exception e ){
				System.err.println(e);
			}
			
		}else {
			
        //Obtenemos la lista de usuarios
			this.users = this.listarProfesores();
		}
    }
	
	/**
	 * Método que realiza la peticion GET para obtener el listado de profesores registrados
	 * @return Lista de profesores registrados
	 */
	public List<Profesor> listarProfesores(){
		

		String url = URLs.LISTUSUARIOS;
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<List<Profesor>> response = restTemplate.exchange(
		  url ,HttpMethod.GET, null, new ParameterizedTypeReference<List<Profesor>>(){});
		
		
		return response.getBody();
	
	}
	
	
	/**
	 * 
	 */
	public void eliminarProfesor() {
		
		if (this.selectedUsers.size() > 0) {
			
			//Creamos la lista de ids a eliminar
			List<Integer> idsEliminar = new ArrayList<Integer>();
			for (Profesor p : this.selectedUsers) {
				System.out.println(p.getId() + ": "+p.getEmail());
				idsEliminar.add(p.getId());
			}
			
			try {
			//Realizamos la peticion Rest
			String url = URLs.USUARIO;
			System.out.println("Petición eliminar usuarios: "+url);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<Object> response = restTemplate.exchange(
			  url ,HttpMethod.DELETE, new HttpEntity<List<Integer>>(idsEliminar), new ParameterizedTypeReference<Object>(){});
			
			System.out.println("Status: "+response.getStatusCodeValue());
			}catch(Exception e) {
				System.err.println(e);
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage(Mensajes.HEADERERROR,  Mensajes.ERRORDELETEUSER));
			}
			
			
			
		}
		
		
		
		
		/*if (p != null) {
			String url = URLs.USUARIO + p.getEmail();
			System.out.println("Petición eliminar usuario: "+url);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<Estado> response = restTemplate.exchange(
			  url ,HttpMethod.DELETE, null, new ParameterizedTypeReference<Estado>(){});
			
			
			//PARCHE
			try{
	            FacesContext contex = FacesContext.getCurrentInstance();
	            contex.getExternalContext().redirect( URLs.URLGestUsuarios );
			}catch(  Exception e ){
				System.err.println( "Error al redireccionar a: " + URLs.URLGestUsuarios );
				System.err.println(e.getMessage());
			}
		}*/
	}
	
	/**
	 * Método que realiza la petición PUT para modificar un usuario
	 * @param user Objeto con el usuario a modificar
	 */
	public void modificar(Profesor user) {
		
			try {
				
				//Petición PUT para modificar el usuario
				String url = constantes.URLs.USUARIO;
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.put(url, user);
				
				//Recargamos la interfaz de gestion de usuarios
				FacesContext contex = FacesContext.getCurrentInstance();
	            contex.getExternalContext().redirect( URLs.URLGestUsuarios );
		
	        //Capturamos los errores
			}catch(Exception e){
				System.err.println(e.getMessage());
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage(Mensajes.HEADERERROR,  Mensajes.ERROREDITUSER));
			}
				
			
	}
	
	//Getters and Setters
	public List<Profesor> getUsers() {
		return users;
	}

	public void setUsers(List<Profesor> users) {
		this.users = users;
	}

	public List<Profesor> getFilteredUsers() {
		return filteredUsers;
	}

	public void setFilteredUsers(List<Profesor> filteredUsers) {
		this.filteredUsers = filteredUsers;
	}

	public Profesor getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(Profesor selectedUser) {
		this.selectedUser = selectedUser;
	}
	
	public List<String> getRolesList() {
		return valoresDesplegables.rolesList;
		
    }

	public List<Profesor> getSelectedUsers() {
		return selectedUsers;
	}

	public void setSelectedUsers(List<Profesor> selectedUsers) {
		this.selectedUsers = selectedUsers;
	}
}