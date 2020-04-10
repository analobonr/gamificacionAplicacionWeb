package converters;


import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import beans.configurarPartida;
import pojos.Juego;


@FacesConverter(value = "juegoConverter")
public class JuegoConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent uiComponent, String idJuego) {
        ValueExpression vex =
                ctx.getApplication().getExpressionFactory()
                        .createValueExpression(ctx.getELContext(),
                                "#{configurarPartida}", configurarPartida.class);

        configurarPartida confP = (configurarPartida)vex.getValue(ctx.getELContext());
        return confP.getJuego(Integer.valueOf(idJuego));
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object juego) {
    	String s = "";
    	
    	if (juego != null) {
    		s = String.valueOf(((Juego) juego).getIdJuego());
    	}
    	
    	return s;
    }
        
    
}
