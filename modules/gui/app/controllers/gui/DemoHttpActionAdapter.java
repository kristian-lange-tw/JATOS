package controllers.gui;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.play.PlayWebContext;
import org.pac4j.play.http.DefaultHttpActionAdapter;
import play.mvc.Result;

import static play.mvc.Results.*;

public class DemoHttpActionAdapter extends DefaultHttpActionAdapter {

    @Override
    public Result adapt(int code, PlayWebContext context) {
        if (code == HttpConstants.UNAUTHORIZED) {
            return unauthorized(views.html.gui.error401.render().toString()).as((HttpConstants.HTML_CONTENT_TYPE));
        } else if (code == HttpConstants.FORBIDDEN) {
            return forbidden(views.html.gui.error403.render().toString()).as((HttpConstants.HTML_CONTENT_TYPE));
        } else {
            return super.adapt(code, context);
        }
    }
}
