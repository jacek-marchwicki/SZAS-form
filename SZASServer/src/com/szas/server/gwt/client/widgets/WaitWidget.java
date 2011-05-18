package com.szas.server.gwt.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.CssResource.ImportedWithPrefix;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Widget;

public class WaitWidget extends Widget {
	private static Resources DEFAULT_RESOURCES;

	private static Resources getDefaultResources() {
		if (DEFAULT_RESOURCES == null) {
			DEFAULT_RESOURCES = GWT.create(Resources.class);
		}
		return DEFAULT_RESOURCES;
	}
	interface Template extends SafeHtmlTemplates {
		@Template("<div class=\"{0}\"></div>")
		SafeHtml loading(String loading);
	}
	public interface Resources extends ClientBundle {
		/**
	     * The loading indicator used while the table is waiting for data.
	     */
	    @ImageOptions(flipRtl = true)
	    ImageResource waitWidgetLoading();
	    
	    @Source(Style.DEFAULT_CSS)
	    Style waitWidgetStyle();
	}
	@ImportedWithPrefix("gwt-WaitWidget")
	public interface Style extends CssResource {
		/**
	     * The path to the default CSS styles used by this resource.
	     */
	    String DEFAULT_CSS = "com/szas/server/gwt/client/widgets/WaitWidget.css";
	    
	    
		String waitWidgetLoading();
	}
	private static Template template;
	private final Resources resources;
	private final Style style;
	public WaitWidget() {
		if (template == null) {
			template = GWT.create(Template.class);
		}
		this.resources = getDefaultResources();
		this.style = resources.waitWidgetStyle();
		this.style.ensureInjected();
		DivElement div = Document.get().createDivElement();
		div.setInnerHTML(template.loading(style.waitWidgetLoading()).asString());
		setElement(div);
	}
}
