package converters;


import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import beans.gestionJuegosBean;
import pojos.Juego;


@FacesConverter(value = "juegoConverterGestion")
public class JuegoConverterGestion implements Converter {

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent uiComponent, String idJuego) {
        ValueExpression vex =
                ctx.getApplication().getExpressionFactory()
                        .createValueExpression(ctx.getELContext(),
                                "#{gestionJuegosBean}", gestionJuegosBean.class);

        gestionJuegosBean bean = (gestionJuegosBean)vex.getValue(ctx.getELContext());
        return bean.getJuego(Integer.valueOf(idJuego));
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object juego) {
    	String s = "";
    	
    	if (juego != null) {
    		s = String.valueOf(((Juego) juego).getId_juego());
    	}
    	
    	return s;
    }
        
    
}
