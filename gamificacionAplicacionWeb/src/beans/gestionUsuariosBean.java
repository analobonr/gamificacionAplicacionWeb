package beans;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import constantes.Mensajes;
import constantes.URLs;
import constantes.valoresDesplegables;
import constantes.valoresDesplegables.rol_t;
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
				//Redireccionamos a la interfaz de login
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
	 * Realiza la peticion DELETE para eliminar un listado de usuarios
	 */
	public void eliminarProfesor() {
		
		//Comprobamos que se haya seleccionado algun usuario
		if (this.selectedUsers.size() > 0) {
			
			//Creamos la lista de ids a eliminar
			List<Integer> idsEliminar = new ArrayList<Integer>();
			for (Profesor p : this.selectedUsers) {
				idsEliminar.add(p.getId());
			}
			
			try {
				//Realizamos la peticion Rest DELETE
				String url = URLs.USUARIO;
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.exchange(
				  url ,HttpMethod.DELETE, new HttpEntity<List<Integer>>(idsEliminar), new ParameterizedTypeReference<Object>(){});
				
				//Recargamos la interfaz de gestion de usuarios
				FacesContext contex = FacesContext.getCurrentInstance();
	            contex.getExternalContext().redirect( URLs.URLGestUsuarios );
			//Capturamos los errores
			}catch(Exception e) {
				System.err.println(e);
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage(Mensajes.HEADERERROR,  Mensajes.ERRORDELETEUSER));
			}
		}
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
	/**
	 * Getter de los usuarios registrados
	 * @return Listado usuarios registrados
	 */
	public List<Profesor> getUsers() {
		return users;
	}

	/**
	 * Setter de los usuarios registrados
	 * @param users Listado de usuarios registrado
	 */
	public void setUsers(List<Profesor> users) {
		this.users = users;
	}

	/**
	 * Getter de los usuarios filtrados
	 * @return Listado de usuarios filtrados
	 */
	public List<Profesor> getFilteredUsers() {
		return filteredUsers;
	}

	/**
	 * Setter de los usuarios filtrados
	 * @param filteredUsers Listado de usuarios filtrados
	 */
	public void setFilteredUsers(List<Profesor> filteredUsers) {
		this.filteredUsers = filteredUsers;
	}

	
	/**
	 * Getter del usuario seleccionado para editar
	 * @return Usuario
	 */
	public Profesor getSelectedUser() {
		return selectedUser;
	}

	/**
	 * Setter del usuario seleccionado para editar
	 * @param selectedUser Usuario
	 */
	public void setSelectedUser(Profesor selectedUser) {
		this.selectedUser = selectedUser;
	}
	
	/**
	 * Getter del listado de roles
	 * @return Listado de roles
	 */
	public List<String> getRolesList() {
		return valoresDesplegables.rolesList;
		
    }

	/**
	 * Getter de los usuarios seleccionados
	 * @return Listado de usuarios
	 */
	public List<Profesor> getSelectedUsers() {
		return selectedUsers;
	}

	/**
	 * Setter de los usuarios seleccionados
	 * @param selectedUsers Listado de usuarios
	 */
	public void setSelectedUsers(List<Profesor> selectedUsers) {
		this.selectedUsers = selectedUsers;
	}
}